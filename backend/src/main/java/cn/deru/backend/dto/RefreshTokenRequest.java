package cn.deru.backend.dto;

import cn.deru.backend.annotation.Sensitive;
import cn.deru.backend.annotation.SensitiveType;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @Sensitive(type = SensitiveType.CUSTOM)
    private String refreshToken;
}
