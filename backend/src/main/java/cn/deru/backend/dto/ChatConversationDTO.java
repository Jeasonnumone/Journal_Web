package cn.deru.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatConversationDTO {

    private Long id;

    private Long userId;

    private String username;

    private String userAvatar;

    private String lastMessage;

    private LocalDateTime lastTime;

    private Integer unreadAdmin;

    private LocalDateTime createdAt;
}
