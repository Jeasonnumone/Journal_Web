package cn.deru.backend.interceptor;

import cn.deru.backend.model.Result;
import cn.deru.backend.util.UserContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String role = UserContext.getRole();
        if (!"ADMIN".equals(role)) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json;charset=UTF-8");
            Result<Void> result = Result.error(4030, "需要管理员权限");
            response.getWriter().write(objectMapper.writeValueAsString(result));
            log.warn("非管理员访问管理接口，角色：{}，接口：{}", role, request.getRequestURI());
            return false;
        }

        return true;
    }
}