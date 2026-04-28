package cn.deru.backend.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    
    private final int code;
    
    public BusinessException(BusinessCode businessCode) {
        super(businessCode.getMessage());
        this.code = businessCode.getCode();
    }
    
    public BusinessException(BusinessCode businessCode, String message) {
        super(message);
        this.code = businessCode.getCode();
    }
    
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
