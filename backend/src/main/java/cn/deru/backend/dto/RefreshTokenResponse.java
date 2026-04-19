package cn.deru.backend.dto;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long accessTokenExpiresIn; // Access Token 过期时间（秒）
}
