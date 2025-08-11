package com.hmdp.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisData;
import com.hmdp.utils.SystemConstants;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //按id查询商铺
    @Override
    public Result queryById(Long id) {
        String key = CACHE_SHOP_KEY + id;

        // 从缓存中查询商铺数据
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        Shop shop = null;//创建shop对象

        // 判断是否存在
        if (StrUtil.isNotBlank(shopJson))
        {
            shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }

        // 判断命中的是否是空值
        if (Objects.nonNull(shopJson))
        {
            return Result.fail("店铺不存在");
        }

        // 缓存不存在，查询数据库
        shop = this.getById(id);
        // 判断数据库是否存在
        if (Objects.isNull(shop))
        {
            // 缓存穿透
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.SECONDS);
            return Result.fail("店铺不存在");
        }
        // 数据库存在，写入缓存
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return Result.ok(shop);
    }

    //按id查询商铺-互斥锁
    @Override
    public Result queryByIdWithLock(Long id) {
        String key = CACHE_SHOP_KEY + id;
        // 从缓存中查询商铺数据
        Result result = getShopFromCache(key);
        if (Objects.nonNull(result)) {
            // 缓存命中，直接返回
            return result;
        }
        String lockKey = LOCK_SHOP_KEY + id;
        try{
            boolean isLock = tryLock(lockKey);
            // 获取锁失败
            if (!isLock)
            {
                Thread.sleep(50);
                return queryByIdWithLock(id);
            }
            result = getShopFromCache(key);
            if (Objects.nonNull(result)) {
                return result;
            }
            Shop shop = this.getById(id);
            if (Objects.isNull(shop))
            {
                // 缓存穿透
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.SECONDS);
                return Result.fail("店铺不存在");
            }
            // 数据库存在，写入缓存
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
            return Result.ok(shop);
        } catch (Exception e) {
            throw new RuntimeException("发生异常");
        } finally {
            //释放锁（释放锁一定要记得放在finally中，防止死锁）
            unLock(lockKey);
        }

    }

    /**
     * 从缓存中获取店铺数据
     * @param key
     * @return
     */
    private Result getShopFromCache(String key) {
        String shopJson = stringRedisTemplate.opsForValue().get(key);
        // 判断缓存是否命中
        if (StrUtil.isNotBlank(shopJson)) {
            // 缓存数据有值，说明缓存命中了，直接返回店铺数据
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        // 判断缓存中查询的数据是否是空字符串(isNotBlank把 null 和 空字符串 给排除了)
        if (Objects.nonNull(shopJson)) {
            // 当前数据是空字符串，说明缓存也命中了（该数据是之前缓存的空对象），直接返回失败信息
            return Result.fail("店铺不存在");
        }
        // 缓存未命中（缓存数据既没有值，又不是空字符串）
        return null;
    }

    boolean tryLock(String key)
    {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10L, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    void unLock(String key)
    {
        stringRedisTemplate.delete(key);
    }

    //按id查询商铺-逻辑过期
    /**
     * 缓存重建线程池
     */
    public static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    @Override
    public Result queryByIdWithLogicalTTL(Long id) {
        String key = CACHE_SHOP_KEY + id;

        // 从缓存中查询商铺数据
        String shopJson = stringRedisTemplate.opsForValue().get(key);

        // 判断是否存在
        if (StrUtil.isBlank(shopJson))
        {
            return Result.fail("店铺不存在");
        }

        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        JSONObject data = (JSONObject) redisData.getData();
        Shop shop = JSONUtil.toBean(data, Shop.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now()))
        {
            return Result.ok(shop);
        }

        // 缓存重建
        String lockkey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockkey);
        if (isLock)
        {
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    this.saveShopToCache(id, CACHE_SHOP_LOGICAL_TTL);
                } catch (Exception e) {
                    log.error("缓存重建失败，shopId: {}"); // 建议添加日志
                } finally {
                    unLock(lockkey);
                }
            });
        }

        //获取失败
        shopJson = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(shopJson))
        {
            return Result.fail("店铺不存在");
        }

        //缓存命中
        redisData = JSONUtil.toBean(shopJson, RedisData.class);
        shop = JSONUtil.toBean((JSONObject) redisData.getData(), Shop.class);
        expireTime = redisData.getExpireTime();
        if (expireTime.isAfter(LocalDateTime.now()))
        {
            // 当前缓存数据未过期，直接返回
            return Result.ok(shop);
        }
        //返回过期数据
        return Result.ok(shop);

    }
    /**
     * 将数据保存到缓存中
     *
     * @param id            商铺id
     * @param expireSeconds 逻辑过期时间
     */
    public void saveShopToCache(Long id, Long expireSeconds) {
        // 从数据库中查询店铺数据
        Shop shop = this.getById(id);
        // 封装逻辑过期数据
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 将逻辑过期数据存入Redis中
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }


    @Override
    public Result update(Shop shop) {
        // 更新数据库
        boolean update = this.updateById(shop);
        if (!update)
        {
            return Result.fail("更新失败");
        }
        // 删除缓存
        update = stringRedisTemplate.delete(CACHE_SHOP_KEY + shop.getId());
        if (!update)
        {
            return Result.fail("删除缓存失败");
        }
        return Result.ok();
    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        // 1.判断是否需要根据坐标查询
        if (x == null || y == null) {
            // 不需要坐标查询，按数据库查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }

        // 2.计算分页参数
        int from = (current - 1) * SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current * SystemConstants.DEFAULT_PAGE_SIZE;

        // 3.查询redis、按照距离排序、分页。结果：shopId、distance
        String key = SHOP_GEO_KEY + typeId;
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = stringRedisTemplate.opsForGeo() // GEOSEARCH key BYLONLAT x y BYRADIUS 10 WITHDISTANCE
                .search(
                        key,
                        GeoReference.fromCoordinate(x, y),
                        new Distance(5000),
                        RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end)
                );
        // 4.解析出id
        if (results == null) {
            return Result.ok(Collections.emptyList());
        }
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = results.getContent();
        if (list.size() <= from) {
            // 没有下一页了，结束
            return Result.ok(Collections.emptyList());
        }
        // 4.1.截取 from ~ end的部分
        List<Long> ids = new ArrayList<>(list.size());
        Map<String, Distance> distanceMap = new HashMap<>(list.size());
        list.stream().skip(from).forEach(result -> {
            // 4.2.获取店铺id
            String shopIdStr = result.getContent().getName();
            ids.add(Long.valueOf(shopIdStr));
            // 4.3.获取距离
            Distance distance = result.getDistance();
            distanceMap.put(shopIdStr, distance);
        });
        // 5.根据id查询Shop
        String idStr = StrUtil.join(",", ids);
        List<Shop> shops = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Shop shop : shops) {
            shop.setDistance(distanceMap.get(shop.getId().toString()).getValue());
        }
        // 6.返回
        return Result.ok(shops);
    }
}
