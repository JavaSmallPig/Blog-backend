package com.blog.interceptor;

import com.blog.context.UserContextHolder;
import com.blog.properties.JwtProperties;
import com.blog.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.blog.constant.Constant.*;

/**
 * @Author Java小猪
 * @Date 2025/3/26 10:16
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {

    @Resource
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }
        
        log.info("JWT拦截器拦截请求: {}", request.getRequestURI());

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getTokenName());
        
        if (token == null || token.isEmpty()) {
            log.warn(TOKEN_IS_NULL);
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(TOKEN_ERROR);
            return false;
        }

        //2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getSecretKey(), token);
            Integer userId = Integer.valueOf(claims.get(USER_ID).toString());
            log.info("当前用户id：{}", userId);
            // 把当前登录用户的id存入本地线程变量中
            UserContextHolder.setCurrentId(userId);
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(TOKEN_ERROR);
            log.error("token验证失败:{}", ex.getMessage());
            return false;
        }
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理ThreadLocal，防止内存泄漏
        UserContextHolder.removeCurrentId();
        log.debug("已清理用户上下文ThreadLocal");
    }
}
