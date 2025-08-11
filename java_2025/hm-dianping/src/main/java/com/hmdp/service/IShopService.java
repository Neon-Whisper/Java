package com.hmdp.service;

import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
public interface IShopService extends IService<Shop> {

    //按id查询商铺
    Result queryById(Long id);
    //按id查询商铺-互斥锁
    Result queryByIdWithLock(Long id);
    //按id查询商铺-逻辑过期
    Result queryByIdWithLogicalTTL(Long id);

    Result update(Shop shop);

    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
