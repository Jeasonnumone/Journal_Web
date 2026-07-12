package cn.deru.backend.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("post_comments")
public class PostComment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long postId;

    private Long userId;

    private Long rootId;

    private Long parentId;

    private Long replyToUserId;

    private String content;

    private Integer isDeleted;

    private Integer replyCount;

    private Date createTime;

    private Date updateTime;
}
