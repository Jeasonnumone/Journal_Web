package cn.deru.backend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDTO {
    
    private Long id;
    
    private Long journalId;
    
    private Long userId;
    
    private String username;
    
    private String userAvatar;
    
    private Long rootId;
    
    private Long parentId;
    
    private Long replyToUserId;
    
    private String replyToUsername;
    
    private String content;
    
    private Integer replyCount;
    
    private Date createTime;
}
