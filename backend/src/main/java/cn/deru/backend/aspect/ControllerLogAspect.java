package cn.deru.backend.aspect;

import cn.deru.backend.util.SensitiveUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logController(ProceedingJoinPoint pjp) throws Throwable {
        String method = pjp.getSignature().toShortString();
        Object[] args = pjp.getArgs();
        String[] desensitizedArgs = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            desensitizedArgs[i] = SensitiveUtil.desensitizeToString(args[i]);
        }

        long start = System.currentTimeMillis();
        log.info(">>> {} 参数: {}", method, Arrays.toString(desensitizedArgs));

        try {
            Object result = pjp.proceed();
            long cost = System.currentTimeMillis() - start;
            log.info("<<< {} 耗时: {}ms", method, cost);
            return result;
        } catch (Exception e) {
            long cost = System.currentTimeMillis() - start;
            log.error("!!! {} 异常, 耗时: {}ms", method, cost);
            throw e;
        }
    }
}
