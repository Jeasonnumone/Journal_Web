package cn.deru.backend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class PostDTO {
    
    private Long id;
    
    private Long userId;
    
    private String username;
    
    private String avatar;
    
    private String title;
    
    private String content;
    
    private Integer viewCount;
    
    private Integer isDeleted;
    
    private Date createTime;
    
    private Date updateTime;
}
