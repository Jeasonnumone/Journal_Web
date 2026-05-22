package cn.deru.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageDTO {

    private Long id;

    private Long conversationId;

    private Long senderId;

    private String senderName;

    private String senderAvatar;

    private String content;

    private Integer isRead;

    private LocalDateTime createdAt;
}
