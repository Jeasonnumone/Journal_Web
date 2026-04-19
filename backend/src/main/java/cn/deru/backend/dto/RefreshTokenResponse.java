package cn.deru.backend.dto;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String accessToken;
    private String refreshToken;
}
