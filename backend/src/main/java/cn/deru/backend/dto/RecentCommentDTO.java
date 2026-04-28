package cn.deru.backend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class RecentCommentDTO {
    
    private Long id;
    
    private Long journalId;
    
    private String journalTitle;
    
    private Long userId;
    
    private String username;
    
    private String content;
    
    private Date createTime;
}
