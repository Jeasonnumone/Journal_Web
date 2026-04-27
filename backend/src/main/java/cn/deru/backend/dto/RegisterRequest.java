package cn.deru.backend.dto;

import cn.deru.backend.annotation.Sensitive;
import cn.deru.backend.annotation.SensitiveType;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;

    @Sensitive(type = SensitiveType.PASSWORD)
    private String password;

    @Sensitive(type = SensitiveType.EMAIL)
    private String email;

    private String verifyCode;
}
