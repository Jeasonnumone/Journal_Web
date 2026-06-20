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

import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    @Select("SELECT c.*, u.username, u.avatar as userAvatar " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.journal_id = #{journalId} " +
            "AND (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    IPage<CommentDTO> selectRootComments(Page<CommentDTO> page, @Param("journalId") Long journalId);
    
    /**
     * 传统分页查询回复（保留兼容）
     */
    @Select("SELECT c.*, u.username, u.avatar as userAvatar, ru.username as replyToUsername " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.root_id = #{rootId} " +
            "AND c.id != #{rootId} " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    IPage<CommentDTO> selectReplies(Page<CommentDTO> page, @Param("rootId") Long rootId);
    
    /**
     * 游标分页查询回复（优化深度分页）
     * 基于 lastId 查询下一页，避免 OFFSET 扫描
     * 
     * @param rootId 根评论 ID
     * @param lastId 上一页最后一条回复的 ID（首次查询传 null）
     * @param size 每页数量
     * @return 回复列表
     */
    @Select("<script>" +
            "SELECT c.*, u.username, u.avatar as userAvatar, ru.username as replyToUsername " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.root_id = #{rootId} " +
            "AND c.id != #{rootId} " +
            "AND c.is_deleted = 0 " +
            "<if test='lastId != null'>" +
            "AND c.id > #{lastId} " +
            "</if>" +
            "ORDER BY c.id ASC " +
            "LIMIT #{size}" +
            "</script>")
    List<CommentDTO> selectRepliesByCursor(@Param("rootId") Long rootId, @Param("lastId") Long lastId, @Param("size") Integer size);
    
    /**
     * 查询回复总数（用于前端判断是否还有更多）
     */
    @Select("SELECT COUNT(*) FROM comments WHERE root_id = #{rootId} AND id != #{rootId} AND is_deleted = 0")
    int countReplies(@Param("rootId") Long rootId);
    
    @Select("SELECT c.id, c.journal_id, c.user_id, c.content, c.create_time, " +
            "u.username, u.avatar, j.title as journal_title " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN journals j ON c.journal_id = j.id " +
            "WHERE (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC " +
            "LIMIT #{limit}")
    java.util.List<RecentCommentDTO> selectRecentComments(@Param("limit") Integer limit);
    
    @Select("SELECT c.id, c.journal_id, c.user_id, c.content, c.create_time, " +
            "u.username, u.avatar, j.title as journal_title " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN journals j ON c.journal_id = j.id " +
            "WHERE c.user_id = #{userId} " +
            "AND (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time DESC")
    IPage<RecentCommentDTO> selectUserComments(Page<RecentCommentDTO> page, @Param("userId") Long userId);

    /**
     * 管理后台查询所有评论（升序）
     */
    @Select("SELECT c.id, c.journal_id, c.user_id, c.content, c.create_time, c.is_deleted, " +
            "u.username, u.avatar, j.title as journal_title " +
            "FROM comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN journals j ON c.journal_id = j.id " +
            "ORDER BY c.create_time ASC")
    IPage<RecentCommentDTO> selectAllCommentsAdminAsc(Page<RecentCommentDTO> page);
}
