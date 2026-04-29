package cn.deru.backend.config;

import cn.deru.backend.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthInterceptor authInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                // 拦截所有需要认证的接口
                .addPathPatterns("/api/**")
                // 放行登录、注册接口以及期刊浏览接口
                .excludePathPatterns(
                    "/api/auth/register", 
                    "/api/auth/login",
                    "/api/auth/refresh",
                    "/api/journals/**",
                    "/api/auth/verify-code",
//                    "/api/comments/**"
                    "/api/comments/recent",
                    "/api/comments/journal/**" // 查看评论不需要登录
                );
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
