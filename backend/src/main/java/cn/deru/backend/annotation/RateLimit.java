package cn.deru.backend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口限流注解
 * 基于 Redis + Lua 实现滑动窗口限流算法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 限流 key 前缀，默认按方法名
     */
    String key() default "";

    /**
     * 时间窗口大小（秒）
     */
    int time() default 60;

    /**
     * 时间窗口内最大请求次数
     */
    int count() default 100;

    /**
     * 限流类型：IP / USER / ALL
     */
    LimitType limitType() default LimitType.IP;

    /**
     * 限流提示消息
     */
    String message() default "请求过于频繁，请稍后再试";

    enum LimitType {
        /**
         * 按 IP 限流
         */
        IP,
        /**
         * 按用户 ID 限流
         */
        USER,
        /**
         * 全局限流（所有请求共享计数）
         */
        ALL
    }
}