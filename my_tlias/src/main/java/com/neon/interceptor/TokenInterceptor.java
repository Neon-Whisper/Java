package com.neon.interceptor;

import com.neon.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取url
        String url = request.getRequestURI();

        //放行login
        if(url.contains("login")){
            log.info("放行login");
            return true;
        }

        //获取token
        String jwt = request.getHeader("token");

        //判断token是否存在
        if(!StringUtils.hasLength(jwt)){
            log.info("token为空");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //解析token
        try {
            JwtUtils.parseJWT(jwt);
        }
        catch (Exception e){
            e.printStackTrace();
            log.info("token解析失败");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        //放行
        log.info("放行");
        return true;
    }
}
