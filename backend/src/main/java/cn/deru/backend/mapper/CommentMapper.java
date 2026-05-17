package cn.deru.backend.mapper;

import cn.deru.backend.dto.CommentDTO;
import cn.deru.backend.dto.RecentCommentDTO;
import cn.deru.backend.model.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    @Select("SELECT c.*, u.username " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.journal_id = #{journalId} " +
            "AND (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    IPage<CommentDTO> selectRootComments(Page<CommentDTO> page, @Param("journalId") Long journalId);
    
    @Select("SELECT c.*, u.username, ru.username as replyToUsername " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.root_id = #{rootId} " +
            "AND c.id != #{rootId} " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    IPage<CommentDTO> selectReplies(Page<CommentDTO> page, @Param("rootId") Long rootId);
    
    @Select("SELECT c.id, c.journal_id, c.user_id, c.content, c.create_time, " +
            "u.username, j.title as journal_title " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN journals j ON c.journal_id = j.id " +
            "WHERE (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{limit}")
    java.util.List<RecentCommentDTO> selectRecentComments(@Param("limit") Integer limit);
    
    @Select("SELECT c.id, c.journal_id, c.user_id, c.content, c.create_time, " +
            "u.username, j.title as journal_title " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN journals j ON c.journal_id = j.id " +
            "WHERE c.user_id = #{userId} " +
            "AND (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    IPage<RecentCommentDTO> selectUserComments(Page<RecentCommentDTO> page, @Param("userId") Long userId);
}
