package cn.deru.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String role;
    private LocalDateTime createTime;
    
    public UserDTO() {}
    
    public UserDTO(Long id, String username, String email, String avatar, String role, LocalDateTime createTime) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.avatar = avatar;
        this.role = role;
        this.createTime = createTime;
    }
}