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
    
    // 投稿体验字段
    private String reviewTime;
    private String isAccepted;
    private String publishPeriod;
    private String isFirstPublish;
    private Integer reviewFee;
    private Integer pageFee;
    private Integer payment;
    private Integer wordCount;
    private String education;
    private String title;
    private String hasProject;
    private String hasReply;
    private String publishType;
    private String topic;
    
    private Integer replyCount;
    
    private Date createTime;
}
