package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.constant.JwtClaimsConstant;
import com.hmdp.dto.UserDTO;
import com.hmdp.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_TTL;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    // new出来的对象是无法直接注入IOC容器的（LoginInterceptor是直接new出来的）
    // 所以这里需要再配置类中注入，然后通过构造器传入到当前类中
    private final StringRedisTemplate stringRedisTemplate;

    @Resource
    private JwtProperties jwtProperties;

//    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
//        this.stringRedisTemplate = stringRedisTemplate;
//    }

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        // 1、获取token，并判断token是否存在
//        String token = request.getHeader("authorization");
//        if (StrUtil.isBlank(token)){
//            // token不存在，说明当前用户未登录，不需要刷新直接放行
//            return true;
//        }
//        // 2、判断用户是否存在
//        String tokenKey = LOGIN_USER_KEY + token;
//        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
//        if (userMap.isEmpty()){
//            // 用户不存在，说明当前用户未登录，不需要刷新直接放行
//            return true;
//        }
//        // 3、用户存在，则将用户信息保存到ThreadLocal中，方便后续逻辑处理，比如：方便获取和使用用户信息，Redis获取用户信息是具有侵入性的
//        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
//        UserHolder.saveUser(BeanUtil.copyProperties(userMap, UserDTO.class));
//        // 4、刷新token有效期
//        stringRedisTemplate.expire(token, LOGIN_USER_TTL, TimeUnit.SECONDS);
//        return true;
//    }


    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate,JwtProperties jwtProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.jwtProperties = jwtProperties; // 手动接收依赖
    }
    //检验JWT
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
//        String token = request.getHeader("authorization");
        String token = request.getHeader(jwtProperties.getUserTokenName());
        if (StrUtil.isBlank(token)) {
            return true;
        }
        Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(),token);
        Long userId =  claims.get(JwtClaimsConstant.USER_ID,Long.class);
        // 2.基于userId获取redis中的用户
        String key  = LOGIN_USER_KEY + userId;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        // 3.判断用户是否存在
        if (userMap.isEmpty()) {
            return true;
        }
        // 4.判断token是否一致,防止有以前生成的jwt，仍然能够登录
        String jwttoken = userMap.get("jwttoken").toString();
        if(!jwttoken.equals(token)){
            return true;
        }
        // 5.将查询到的hash数据转为UserDTO
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 6.存在，保存用户信息到 ThreadLocal
        UserHolder.saveUser(userDTO);
        // 7.刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 8.放行
        return true;
    }
}
