package cn.deru.backend.aspect;

import cn.deru.backend.annotation.RateLimit;
import cn.deru.backend.exception.RateLimitException;
import cn.deru.backend.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 限流切面
 * 基于 Redis + Lua 实现滑动窗口限流算法
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private HttpServletRequest request;

    private DefaultRedisScript<Long> redisScript;

    /**
     * 初始化 Lua 脚本
     */
    @PostConstruct
    public void init() {
        redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/rate_limit.lua")));
        redisScript.setResultType(Long.class);
    }

    @Around("@annotation(cn.deru.backend.annotation.RateLimit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        if (rateLimit == null) {
            return point.proceed();
        }

        // 构建限流 key
        String key = buildKey(rateLimit, method);

        // 执行 Lua 脚本
        List<String> keys = Collections.singletonList(key);
        long now = System.currentTimeMillis();
        Long result = redisTemplate.execute(redisScript, keys,
                String.valueOf(rateLimit.time()),
                String.valueOf(rateLimit.count()),
                String.valueOf(now));

        if (result != null && result == 0) {
            log.warn("接口限流触发: key={}, ip={}", key, getIpAddress());
            throw new RateLimitException(rateLimit.message());
        }

        return point.proceed();
    }

    /**
     * 构建限流 key
     */
    private String buildKey(RateLimit rateLimit, Method method) {
        String prefix = "rate_limit:";
        
        // 方法名作为 key 的一部分
        String methodName = method.getDeclaringClass().getSimpleName() + ":" + method.getName();
        
        // 自定义 key 或使用方法名
        String customKey = rateLimit.key().isEmpty() ? methodName : rateLimit.key();
        
        // 根据限流类型添加标识
        String identifier = "";
        switch (rateLimit.limitType()) {
            case IP:
                identifier = getIpAddress();
                break;
            case USER:
                Long userId = UserContext.getUserId();
                identifier = userId != null ? String.valueOf(userId) : getIpAddress();
                break;
            case ALL:
                identifier = "all";
                break;
        }
        
        return prefix + customKey + ":" + identifier;
    }

    /**
     * 获取客户端 IP 地址
     */
    private String getIpAddress() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个代理的情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}