package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.constant.JwtClaimsConstant;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.properties.JwtProperties;
import com.hmdp.service.IUserService;
import com.hmdp.utils.JwtUtil;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.SystemConstants;
import com.hmdp.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.*;
import static com.hmdp.utils.SystemConstants.USER_NICK_NAME_PREFIX;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtProperties jwtProperties;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 校验手机号格式是否有效
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 格式不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 格式有效，生成验证码
        String code = RandomUtil.randomNumbers(6);  // 随机6位数字
        // 保存验证码到redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone ,code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        // 模拟发送短信验证码
        log.info("向{}发送短信验证码成功，验证码：{}", phone, code);
        // 返回ok
        return Result.ok();
    }

//    @Override
//    public Result login(LoginFormDTO loginForm, HttpSession session) {
//        //校验手机号
//        String phone = loginForm.getPhone();
//        if (RegexUtils.isPhoneInvalid(phone)) {
//            //手机号不符合
//            return Result.fail("手机号格式错误");
//        }
//        //从redis中获取验证码 校验验证码
//        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
//        String code = loginForm.getCode();
//        if (cacheCode == null || !cacheCode.equals(code)) {
//            //不一致 报错
//            return Result.fail("验证码错误");
//        }
//        //一致 根据手机号查询用户
//        User user = this.query().eq("phone", phone).one();
//        //判断用户是否存在
//        if (user == null) {
//            //不存在 创建新用户
//            user = createUserWithPhone(phone);
//        }
//
//        //生成token
//        String token = UUID.randomUUID().toString();
//        //userDTO转map
//        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
//        Map<String, Object> map = BeanUtil.beanToMap(userDTO, new HashMap<>()
//                , CopyOptions.create().setIgnoreNullValue(true)
//                        .setFieldValueEditor(
//                                (name, value) -> value.toString()
//                        ));
//        //保存用户信息到redis
//        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, map);
//        //设置过期时间
//        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.SECONDS);
//        return Result.ok(token);
//    }

    // 使用JWT实现登录功能
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1.校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }

        // 3.从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY + phone);
        String code = loginForm.getCode();

        //  假如要生成1k个用户测试的话，注释掉以下部分，先不校验验证码
//        if(cacheCode == null || !cacheCode.equals(code)){
//            //3.不一致，报错
//            return Result.fail("验证码错误");
//        }


        //一致，根据手机号查询用户
        User user = query().eq("phone", phone).one();

        //5.判断用户是否存在
        if(user == null){
            //不存在，则创建
            user =  createUserWithPhone(phone);
        }

        // 6.生成JWT
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String jwttoken = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);


        // 7.2.将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(), //beanToMap方法执行了对象到Map的转换
                CopyOptions.create()
                        .setIgnoreNullValue(true) //BeanUtil在转换过程中忽略所有null值的属性
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString())); //对于每个字段值，它简单地调用toString()方法，将字段值转换为字符串。
        // 7.3.存储
        String tokenKey = LOGIN_USER_KEY + userDTO.getId();
        // 7.4.将jwttoken存入userMap中
        userMap.put("jwttoken",jwttoken);
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.5.设置redis中 userId的有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 8.返回token
        return Result.ok(jwttoken);

    }


    @Override
    public Result logout(String token) {
        String key = "login:token:" + token;
        Boolean isDeleted = stringRedisTemplate.delete(key);
        if (Boolean.TRUE.equals(isDeleted)) {
            return Result.ok();
        }
        return Result.fail("登出失败");
    }

    /**
     * 用户签到
     *
     * @return
     */
    @Override
    public Result sign() {
        // 获取当前登录用户
        Long userId = UserHolder.getUser().getId();
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 写入Redis SETBIT key offset 1
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        return Result.ok();
    }

    /**
     * 记录连续签到的天数
     *
     * @return
     */
    @Override
    public Result signCount() {
        // 1、获取签到记录
        // 获取当前登录用户
        Long userId = UserHolder.getUser().getId();
        // 获取日期
        LocalDateTime now = LocalDateTime.now();
        // 拼接key
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_KEY + userId + keySuffix;
        // 获取今天是本月的第几天
        int dayOfMonth = now.getDayOfMonth();
        // 获取本月截止今天为止的所有的签到记录，返回的是一个十进制的数字 BITFIELD sign:5:202203 GET u14 0
        List<Long> result = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );
        // 2、判断签到记录是否存在
        if (result == null || result.isEmpty()) {
            // 没有任何签到结果
            return Result.ok(0);
        }
        // 3、获取本月的签到数（List<Long>是因为BitFieldSubCommands是一个子命令，可能存在多个返回结果，这里我们知识使用了Get，
        // 可以明确只有一个返回结果，即为本月的签到数，所以这里就可以直接通过get(0)来获取）
        Long num = result.get(0);
        if (num == null || num == 0) {
            // 二次判断签到结果是否存在，让代码更加健壮
            return Result.ok(0);
        }
        // 4、循环遍历，获取连续签到的天数（从当前天起始）
        int count = 0;
        while (true) {
            // 让这个数字与1做与运算，得到数字的最后一个bit位，并且判断这个bit位是否为0
            if ((num & 1) == 0) {
                // 如果为0，说明未签到，结束
                break;
            } else {
                // 如果不为0，说明已签到，计数器+1
                count++;
            }
            // 把数字右移一位，抛弃最后一个bit位，继续下一个bit位
            num >>>= 1;
        }
        return Result.ok(count);
    }



    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickName(SystemConstants.USER_NICK_NAME_PREFIX + RandomUtil.randomString(10));
        this.save(user);
        return user;
    }

}
