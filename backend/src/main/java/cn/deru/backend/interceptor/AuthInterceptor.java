package cn.deru.backend.interceptor;

import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.model.Result;
import cn.deru.backend.util.JwtUtil;
import cn.deru.backend.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 处理跨域 OPTIONS 请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }
        
        // 获取 Authorization 头
        String authHeader = request.getHeader("Authorization");
        
        // 检查是否有 Authorization 头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.error(BusinessCode.UNAUTHORIZED.getCode(), BusinessCode.UNAUTHORIZED.getMessage());
            response.getWriter().write(objectMapper.writeValueAsString(result));
            log.warn("请求无 token，被拦截返回");
            return false;
        }
        
        // 提取 token
        String token = authHeader.substring(7);
        
        try {
            // 验证 token
            if (jwtUtil.validateToken(token)) {
                // 将用户信息存入 ThreadLocal 中
                UserContext.setUserInfo(
                        jwtUtil.getUserIdFromToken(token),
                        jwtUtil.getUsernameFromToken(token),
                        jwtUtil.getRoleFromToken(token)
                );
                return true;
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json;charset=UTF-8");
                Result<Void> result = Result.error(BusinessCode.TOKEN_INVALID.getCode(), "Token 已过期");
                response.getWriter().write(objectMapper.writeValueAsString(result));
                log.warn("token 时间过期");
                return false;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.error(BusinessCode.TOKEN_INVALID.getCode(), "Token 无效");
            response.getWriter().write(objectMapper.writeValueAsString(result));
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