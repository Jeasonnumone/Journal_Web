package cn.deru.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatConversationDTO {

    private Long id;

    private Long userId;

    private String username;

    private String userAvatar;

    private Long adminId;

    private String adminName;

    private String adminAvatar;

    private String lastMessage;

    private LocalDateTime lastTime;

    private Integer unreadUser;

    private Integer unreadAdmin;

    private LocalDateTime createdAt;
}
