package cn.deru.backend.exception;

public enum BusinessCode {
    SUCCESS(200, "操作成功"),
    
    BAD_REQUEST(4000, "请求参数错误"),
    
    USER_ALREADY_EXISTS(4002, "用户已注册"),
    VERIFY_CODE_ERROR(4003, "验证码错误"),
    VERIFY_CODE_EXPIRED(4004, "验证码已过期或未发送"),
    EMAIL_ALREADY_EXISTS(4005, "该邮箱已被注册"),
    
    UNAUTHORIZED(4010, "未登录或登录已过期"),
    TOKEN_INVALID(4011, "Token 无效"),
    
    FORBIDDEN(4030, "权限不足"),
    
    RESOURCE_NOT_FOUND(4040, "资源不存在"),
    
    INTERNAL_ERROR(5000, "服务器内部错误"),
    DATABASE_ERROR(5001, "数据库操作失败");
    
    private final int code;
    private final String message;
    
    BusinessCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() {
        return code;
    }
    
    public String getMessage() {
        return message;
    }
}
