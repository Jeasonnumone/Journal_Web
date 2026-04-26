package cn.deru.backend.dto;

import cn.deru.backend.annotation.Sensitive;
import cn.deru.backend.annotation.SensitiveType;
import lombok.Data;

@Data
public class LoginRequest {
    private String username;

    @Sensitive(type = SensitiveType.PASSWORD)
    private String password;
}
