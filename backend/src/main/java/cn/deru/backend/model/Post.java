package cn.deru.backend.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("posts")
public class Post {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private String title;
    
    private String content;
    
    private Integer viewCount;
    
    private Integer isDeleted;
    
    private Date createTime;
    
    private Date updateTime;
}
