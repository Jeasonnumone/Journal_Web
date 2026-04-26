package cn.deru.backend.interceptor;

import cn.deru.backend.util.JwtUtil;
import cn.deru.backend.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理跨域OPTIONS请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 获取Authorization头
        String authHeader = request.getHeader("Authorization");
        
        // 检查是否有Authorization头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"No-Authorization, Need Token\"}");
            log.warn("请求无token，被拦截返回");
            return false;
        }
        
        // 提取token
        String token = authHeader.substring(7);
        
        try {
            // 验证token
            if (jwtUtil.validateToken(token)) {
                // 将用户信息存入ThreadLocal中
                UserContext.setUserInfo(
                        jwtUtil.getUserIdFromToken(token),
                        jwtUtil.getUsernameFromToken(token),
                        jwtUtil.getRoleFromToken(token)
                );
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"message\":\"token expirationed\"}");
                log.warn("token时间过期");
                return false;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"message\":\"Invalid token\"}");
            return false;
        }
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求处理完成后清除ThreadLocal中的用户信息
//        UserContext.clear();
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 请求完成后清除ThreadLocal中的用户信息
        UserContext.clear();
    }
}