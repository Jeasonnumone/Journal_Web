package cn.deru.backend.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
        private String role;
    }
}
