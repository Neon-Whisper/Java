package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.hmdp.dto.Result;
import com.hmdp.entity.SeckillVoucher;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.SimpleRedisLock;
import com.hmdp.utils.UserHolder;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.aop.framework.AopContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
//@Service
//public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
//
//    @Resource
//    private ISeckillVoucherService seckillVoucherService;
//
//    @Resource
//    private RedisIdWorker redisIdWorker;
//
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Resource
//    private RedissonClient redissonClient;
//
//    // 抢购秒杀优惠券
//    //自己锁
////    @Override
////    public Result seckillVoucher(Long voucherId) {
////        //查询秒杀优惠券
////        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
////        // 判断秒杀优惠券是否合法
////        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
////            return Result.fail("秒杀尚未开始");
////        }
////        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
////            return Result.fail("秒杀已经结束");
////        }
////        if (seckillVoucher.getStock() < 1) {
////            return Result.fail("库存不足");
////        }
////        //创建订单
////        Long userId = UserHolder.getUser().getId();
////
////        SimpleRedisLock lock = new SimpleRedisLock(stringRedisTemplate, "order:" + userId);
////        boolean isLock = lock.tryLock(1200);
////        if (!isLock) {
////            // 索取锁失败，重试或者直接抛异常（这个业务是一人一单，所以直接返回失败信息）
////            return Result.fail("一人只能下一单");
////        }
////        try {
////            // 索取锁成功，创建代理对象，使用代理对象调用第三方事务方法， 防止事务失效
////            IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
////            return proxy.createVoucherOrder(userId, voucherId);
////        } finally {
////            lock.unlock();
////        }
////   }
//
//    //Redisson实现
//    @Override
//    public Result seckillVoucher(Long voucherId) {
//        //查询秒杀优惠券
//        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
//        // 判断秒杀优惠券是否合法
//        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now())) {
//            return Result.fail("秒杀尚未开始");
//        }
//        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now())) {
//            return Result.fail("秒杀已经结束");
//        }
//        if (seckillVoucher.getStock() < 1) {
//            return Result.fail("库存不足");
//        }
//        //创建订单
//        Long userId = UserHolder.getUser().getId();
//        RLock lock = redissonClient.getLock("lock:order:" + userId);
//        boolean isLock = lock.tryLock();
//   }
//
//    /**
//     * 创建订单
//     *
//     * @param userId
//     * @param voucherId
//     * @return
//     */
//    @Transactional
//    @Override
//    public Result createVoucherOrder(Long userId, Long voucherId) {
//        // 1、判断当前用户是否是第一单
//        int count = this.count(new LambdaQueryWrapper<VoucherOrder>()
//                .eq(VoucherOrder::getUserId, userId));
//        if (count > 0) {
//            // 当前用户不是第一单
//            return Result.fail("用户已购买");
//        }
//        // 2、用户是第一单，可以下单，秒杀券库存数量减一
//        boolean flag = seckillVoucherService.update(new LambdaUpdateWrapper<SeckillVoucher>()
//                .eq(SeckillVoucher::getVoucherId, voucherId)
//                .gt(SeckillVoucher::getStock, 0)
//                .setSql("stock = stock -1"));
//        if (!flag) {
//            throw new RuntimeException("秒杀券扣减失败");
//        }
//        // 3、创建对应的订单，并保存到数据库
//        VoucherOrder voucherOrder = new VoucherOrder();
//        long orderId = redisIdWorker.nextId("order");
//        voucherOrder.setId(orderId);
//        voucherOrder.setUserId(UserHolder.getUser().getId());
//        voucherOrder.setVoucherId(voucherOrder.getId());
//        flag = this.save(voucherOrder);
//        if (!flag) {
//            throw new RuntimeException("创建秒杀券订单失败");
//        }
//        // 4、返回订单id
//        return Result.ok(orderId);
//       }
//
//}












//异步秒杀优化
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Resource
    private ISeckillVoucherService seckillVoucherService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 当前类初始化完毕就立马执行该方法
     */
    @PostConstruct
    private void init() {
        // 执行线程任务
        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
    }

//    /**
//     * 存储订单的阻塞队列
//     */
//    private BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024 * 1024);

    /**
     * 线程池
     */
    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();

    /**
     * 队列名
     */
    private static final String queueName = "stream.orders";

    /**
     * 线程任务: 不断从阻塞队列中获取订单
     */
    private class VoucherOrderHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                // 从阻塞队列中获取订单信息，并创建订单
                try {
                    //获取消息队列的订单信息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
                    );
                    //判断消息是否获取成功
                    if (list == null || list.isEmpty()) {
                        // 如果获取失败，说明没有消息，继续下一次循环
                        continue;
                    }
                    // 获取订单信息
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    //如果获取成功，可下单
                    handleVoucherOrder(voucherOder);
                    // ACK确认
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());

                } catch (Exception e) {
                    log.error("处理订单异常", e);
                    handlePendingList();
                }
            }
        }
        private void handlePendingList() {
            while (true) {
                // 从阻塞队列中获取订单信息，并创建订单
                try {
                    //获取消息队列的订单信息
                    List<MapRecord<String, Object, Object>> list = stringRedisTemplate.opsForStream().read(
                            Consumer.from("g1", "c1"),
                            StreamReadOptions.empty().count(1),
                            StreamOffset.create(queueName, ReadOffset.from("0"))
                    );
                    //判断消息是否获取成功
                    if (list == null || list.isEmpty()) {
                        // 如果获取失败，说明没有消息，继续下一次循环
                        break;
                    }
                    // 获取订单信息
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), true);
                    //如果获取成功，可下单
                    handleVoucherOrder(voucherOder);
                    // ACK确认
                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());

                } catch (Exception e) {
                    log.error("处理订单Pending-List异常", e);
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }


    /**
     * 创建订单
     *
     * @param voucherOrder
     */
    private void handleVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        RLock lock = redissonClient.getLock("lock:order:" + userId);
        boolean isLock = lock.tryLock();
        if (!isLock) {
            // 索取锁失败，重试或者直接抛异常（这个业务是一人一单，所以直接返回失败信息）
            log.error("一人只能下一单");
            return;
        }
        try {
            // 创建订单（使用代理对象调用，是为了确保事务生效）
            // 限制Stream长度，只保留最近10000条消息
            stringRedisTemplate.opsForStream().trim(queueName, 10000);
            proxy.createVoucherOrder(voucherOrder);
        } finally {
            lock.unlock();
        }
    }

    /**
     * 加载 判断秒杀券库存是否充足 并且 判断用户是否已下单 的Lua脚本
     */
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
    static {
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/stream-seckill.lua"));
        SECKILL_SCRIPT.setResultType(Long.class);
    }

    /**
     * VoucherOrderServiceImpl类的代理对象
     * 将代理对象的作用域进行提升，方面子线程取用
     */
    private IVoucherOrderService proxy;

    /**
     * 抢购秒杀券
     *
     * @param voucherId
     * @return
     */

    //采用阻塞队列
//    @Transactional
//    @Override
//    public Result seckillVoucher(Long voucherId) {
//        // 1、执行Lua脚本，判断用户是否具有秒杀资格
//        Long result = null;
//        try {
//            result = stringRedisTemplate.execute(
//                    SECKILL_SCRIPT,
//                    Collections.emptyList(),
//                    voucherId.toString(),
//                    UserHolder.getUser().getId().toString()
//            );
//        } catch (Exception e) {
//            log.error("Lua脚本执行失败");
//            throw new RuntimeException(e);
//        }
//        if (result != null && !result.equals(0L)) {
//            // result为1表示库存不足，result为2表示用户已下单
//            int r = result.intValue();
//            return Result.fail(r == 2 ? "不能重复下单" : "库存不足");
//        }
//        // 2、result为0，用户具有秒杀资格，将订单保存到阻塞队列中，实现异步下单
//        long orderId = redisIdWorker.nextId("order");
//        // 创建订单
//        VoucherOrder voucherOrder = new VoucherOrder();
//        voucherOrder.setId(orderId);
//        voucherOrder.setUserId(UserHolder.getUser().getId());
//        voucherOrder.setVoucherId(voucherId);
//        // 将订单保存到阻塞队列中
//        orderTasks.add(voucherOrder);
//        // 索取锁成功，创建代理对象，使用代理对象调用第三方事务方法， 防止事务失效
//        IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//        this.proxy = proxy;
//        return Result.ok();
//    }

//    //redis实现消息队列
//    @Transactional
//    @Override
//    public Result seckillVoucher(Long voucherId) {
//        // 1、执行Lua脚本，判断用户是否具有秒杀资格
//        Long result = null;
//        long orderId = redisIdWorker.nextId("order");
//        try {
//            result = stringRedisTemplate.execute(
//                    SECKILL_SCRIPT,
//                    Collections.emptyList(),
//                    voucherId.toString(),
//                    UserHolder.getUser().getId().toString(),
//                    String.valueOf(orderId)
//            );
//        } catch (Exception e) {
//            log.error("Lua脚本执行失败");
//            throw new RuntimeException(e);
//        }
//        if (result != null && !result.equals(0L)) {
//            // result为1表示库存不足，result为2表示用户已下单
//            int r = result.intValue();
//            return Result.fail(r == 2 ? "不能重复下单" : "库存不足");
//        }
//
//        // 索取锁成功，创建代理对象，使用代理对象调用第三方事务方法， 防止事务失效
//        IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//        this.proxy = proxy;
//        return Result.ok();
//    }

    //rabbitMQ实现消息队列
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public Result seckillVoucher(Long voucherId)
    {
        // 1、执行Lua脚本，判断用户是否具有秒杀资格
        Long result = null;
        long orderId = redisIdWorker.nextId("order");
        try {
            result = stringRedisTemplate.execute(
                    SECKILL_SCRIPT,
                    Collections.emptyList(),
                    voucherId.toString(),
                    UserHolder.getUser().getId().toString(),
                    String.valueOf(orderId)
            );
        } catch (Exception e) {
            log.error("Lua脚本执行失败");
            throw new RuntimeException(e);
        }
        if (result != null && !result.equals(0L)) {
            // result为1表示库存不足，result为2表示用户已下单
            int r = result.intValue();
            return Result.fail(r == 2 ? "不能重复下单" : "库存不足");
        }
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setId(orderId);
        voucherOrder.setUserId(UserHolder.getUser().getId());
        voucherOrder.setVoucherId(voucherId);

        rabbitTemplate.convertAndSend("hmdianping.direct", "seckill.order", voucherOrder);
        return Result.ok(orderId);
    }


    /**
     * 创建订单
     *
     * @param voucherOrder
     * @return
     */
    @Transactional
    @Override
    public void createVoucherOrder(VoucherOrder voucherOrder) {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();
        // 1、判断当前用户是否是第一单
        int count = query().eq("user_id", userId).eq("voucher_id", voucherId).count();
        if (count > 0) {
            // 当前用户不是第一单
            log.error("当前用户不是第一单");
            return;
        }
        // 2、用户是第一单，可以下单，秒杀券库存数量减一
        boolean flag = seckillVoucherService.update()
                .eq("voucher_id", voucherId)
                .gt("stock", 0)
                .setSql("stock = stock -1")
                .update();
        if (!flag) {
            throw new RuntimeException("秒杀券扣减失败");
        }
        // 3、将订单保存到数据库
        flag = this.save(voucherOrder);
        if (!flag) {
            throw new RuntimeException("创建秒杀券订单失败");
        }
    }
}



//大佬的（修改后）

//@Service
//public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {
//
//    @Resource
//    private ISeckillVoucherService seckillVoucherService;
//
//    @Resource
//    private RedisIdWorker redisIdWorker;
//
//    @Resource
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Resource
//    private RedissonClient redissonClient;
//
//    /**
//     * 当前类初始化完毕就立马执行该方法
//     */
//    @PostConstruct
//    private void init() {
//        // 执行线程任务
//        SECKILL_ORDER_EXECUTOR.submit(new VoucherOrderHandler());
//    }
//
//    /**
//     * 线程池
//     */
//    private static final ExecutorService SECKILL_ORDER_EXECUTOR = Executors.newSingleThreadExecutor();
//
//    /**
//     * 队列名
//     */
//    private static final String queueName = "stream.orders";
//
//    /**
//     * 线程任务: 不断从消息队列中获取订单
//     */
//    private class VoucherOrderHandler implements Runnable {
//        @Override
//        public void run() {
//            while (true) {
//                try {
//                    // 1、从消息队列中获取订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 1000 STREAMS streams.order >
//                    List<MapRecord<String, Object, Object>> messageList = stringRedisTemplate.opsForStream().read(
//                            Consumer.from("g1", "c1"),
//                            StreamReadOptions.empty().count(1).block(Duration.ofSeconds(1)),
//                            StreamOffset.create(queueName, ReadOffset.lastConsumed())
//                    );
//                    // 2、判断消息获取是否成功
//                    if (messageList == null || messageList.isEmpty()) {
//                        // 2.1 消息获取失败，说明没有消息，进入下一次循环获取消息
//                        continue;
//                    }
//                    // 3、消息获取成功，可以下单
//                    // 将消息转成VoucherOrder对象
//                    MapRecord<String, Object, Object> record = messageList.get(0);
//                    Map<Object, Object> messageMap = record.getValue();
//                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(messageMap, new VoucherOrder(), true);
//                    handleVoucherOrder(voucherOrder);
//                    // 4、ACK确认 SACK stream.orders g1 id
//                    stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
//                } catch (Exception e) {
//                    log.error("处理订单异常", e);
//                    // 处理异常消息
//                    handlePendingList();
//                }
//            }
//        }
//    }
//
//    private void handlePendingList() {
//        while (true) {
//            try {
//                // 1、从pendingList中获取订单信息 XREADGROUP GROUP g1 c1 COUNT 1 BLOCK 1000 STREAMS streams.order 0
//                List<MapRecord<String, Object, Object>> messageList = stringRedisTemplate.opsForStream().read(
//                        Consumer.from("g1", "c1"),
//                        StreamReadOptions.empty().count(1).block(Duration.ofSeconds(1)),
//                        StreamOffset.create(queueName, ReadOffset.from("0"))
//                );
//                // 2、判断pendingList中是否有效性
//                if (messageList == null || messageList.isEmpty()) {
//                    // 2.1 pendingList中没有消息，直接结束循环
//                    break;
//                }
//                // 3、pendingList中有消息
//                // 将消息转成VoucherOrder对象
//                MapRecord<String, Object, Object> record = messageList.get(0);
//                Map<Object, Object> messageMap = record.getValue();
//                VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(messageMap, new VoucherOrder(), true);
//                handleVoucherOrder(voucherOrder);
//                // 4、ACK确认 SACK stream.orders g1 id
//                stringRedisTemplate.opsForStream().acknowledge(queueName, "g1", record.getId());
//            } catch (Exception e) {
//                log.error("处理订单异常", e);
//                // 这里不用调自己，直接就进入下一次循环，再从pendingList中取，这里只需要休眠一下，防止获取消息太频繁
//                try {
//                    Thread.sleep(20);
//                } catch (InterruptedException ex) {
//                    log.error("线程休眠异常", ex);
//                }
//            }
//        }
//    }
//
//    /**
//     * 创建订单
//     *
//     * @param voucherOrder
//     */
//    private void handleVoucherOrder(VoucherOrder voucherOrder) {
//        Long userId = voucherOrder.getUserId();
//        RLock lock = redissonClient.getLock("lock:order:" + userId);
//        boolean isLock = lock.tryLock();
//        if (!isLock) {
//            // 索取锁失败，重试或者直接抛异常（这个业务是一人一单，所以直接返回失败信息）
//            log.error("一人只能下一单");
//            return;
//        }
//        try {
//            // 创建订单（使用代理对象调用，是为了确保事务生效）
//            proxy.createVoucherOrder(voucherOrder);
//        } finally {
//            lock.unlock();
//        }
//    }
//
//    /**
//     * 加载 判断秒杀券库存是否充足 并且 判断用户是否已下单 的Lua脚本
//     */
//    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;
//
//    static {
//        SECKILL_SCRIPT = new DefaultRedisScript<>();
//        SECKILL_SCRIPT.setLocation(new ClassPathResource("lua/stream-seckill.lua"));
//        SECKILL_SCRIPT.setResultType(Long.class);
//    }
//
//    /**
//     * VoucherOrderServiceImpl类的代理对象
//     * 将代理对象的作用域进行提升，方面子线程取用
//     */
//    private IVoucherOrderService proxy;
//
//    /**
//     * 抢购秒杀券
//     *
//     * @param voucherId
//     * @return
//     */
//    @Transactional
//    @Override
//    public Result seckillVoucher(Long voucherId) {
//        // 1、执行Lua脚本，判断用户是否具有秒杀资格
//        Long result = null;
//        long orderId = redisIdWorker.nextId("order");
//        try {
//            result = stringRedisTemplate.execute(
//                    SECKILL_SCRIPT,
//                    Collections.emptyList(),
//                    voucherId.toString(),
//                    UserHolder.getUser().getId().toString(),
//                    String.valueOf(orderId)
//            );
//        } catch (Exception e) {
//            log.error("Lua脚本执行失败");
//            throw new RuntimeException(e);
//        }
//        if (result != null && !result.equals(0L)) {
//            // result为1表示库存不足，result为2表示用户已下单
//            int r = result.intValue();
//            return Result.fail(r == 2 ? "不能重复下单" : "库存不足");
//        }
//
//        // 获取代理对象并保存引用
//        IVoucherOrderService proxy = (IVoucherOrderService) AopContext.currentProxy();
//        this.proxy = proxy;
//        return Result.ok();
//    }
//
//    /**
//     * 创建订单
//     *
//     * @param voucherOrder
//     * @return
//     */
//    @Transactional
//    @Override
//    public void createVoucherOrder(VoucherOrder voucherOrder) {
//        Long userId = voucherOrder.getUserId();
//        Long voucherId = voucherOrder.getVoucherId();
//        // 1、判断当前用户是否是第一单
//        int count = query().eq("user_id", userId).count();
//        if (count >= 1) {
//            // 当前用户不是第一单
//            log.error("当前用户不是第一单");
//            return;
//        }
//        // 2、用户是第一单，可以下单，秒杀券库存数量减一
//        boolean flag = seckillVoucherService.update()
//                .eq("voucher_id", voucherId)
//                .gt("stock", 0)
//                .setSql("stock = stock - 1")
//                .update();
//        if (!flag) {
//            throw new RuntimeException("秒杀券扣减失败");
//        }
//        // 3、将订单保存到数据库
//        flag = this.save(voucherOrder);
//        if (!flag) {
//            throw new RuntimeException("创建秒杀券订单失败");
//        }
//    }
//
//}




