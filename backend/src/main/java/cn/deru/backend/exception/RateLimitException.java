package cn.deru.backend.exception;

import lombok.Getter;

/**
 * 限流异常
 */
@Getter
public class RateLimitException extends RuntimeException {

    private final String message;

    public RateLimitException(String message) {
        super(message);
        this.message = message;
    }
}