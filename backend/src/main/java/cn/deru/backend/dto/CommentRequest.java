package cn.deru.backend.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CommentRequest {
    
    @NotBlank(message = "评论内容不能为空")
    private String content;
    
    @NotNull(message = "期刊 ID 不能为空")
    private Long journalId;
    
    private Long rootId;
    
    private Long parentId;
    
    private Long replyToUserId;
    
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
}
