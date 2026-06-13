package cn.deru.backend.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("comments")
public class Comment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long journalId;
    
    private Long userId;
    
    private Long rootId;
    
    private Long parentId;
    
    private Long replyToUserId;
    
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
    
    // 评论图片，JSON数组格式存储
    private String images;
    
    private Integer isDeleted;
    
    private Integer replyCount;
    
    private Date createTime;
    
    private Date updateTime;
}
