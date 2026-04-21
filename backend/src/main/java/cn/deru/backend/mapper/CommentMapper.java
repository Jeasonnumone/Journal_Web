package cn.deru.backend.mapper;

import cn.deru.backend.dto.CommentDTO;
import cn.deru.backend.model.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 分页查询一级评论（关联用户表，返回用户名）
     */
    @Select("SELECT c.*, u.username " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.journal_id = #{journalId} " +
            "AND (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    IPage<CommentDTO> selectRootComments(Page<CommentDTO> page, @Param("journalId") Long journalId);
    
    /**
     * 分页查询回复列表（关联用户表）
     */
    @Select("SELECT c.*, u.username, ru.username as replyToUsername " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.root_id = #{rootId} " +
            "AND c.id != #{rootId} " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    IPage<CommentDTO> selectReplies(Page<CommentDTO> page, @Param("rootId") Long rootId);
}
