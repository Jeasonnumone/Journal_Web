package cn.deru.backend.mapper;

import cn.deru.backend.dto.PostCommentDTO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PostCommentMapper extends BaseMapper<cn.deru.backend.model.PostComment> {

    @Select("SELECT c.*, u.username, u.avatar as userAvatar " +
            "FROM post_comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "WHERE c.post_id = #{postId} " +
            "AND (c.parent_id IS NULL OR c.root_id = c.id) " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    IPage<PostCommentDTO> selectRootComments(Page<PostCommentDTO> page, @Param("postId") Long postId);

    @Select("SELECT c.*, u.username, u.avatar as userAvatar, ru.username as replyToUsername " +
            "FROM post_comments c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user ru ON c.reply_to_user_id = ru.id " +
            "WHERE c.root_id = #{rootId} " +
            "AND c.id != #{rootId} " +
            "AND c.is_deleted = 0 " +
            "ORDER BY c.create_time ASC")
    IPage<PostCommentDTO> selectReplies(Page<PostCommentDTO> page, @Param("rootId") Long rootId);

    @Select("<script>" +
            "SELECT c.*, u.username, u.avatar as userAvatar, ru.username as replyToUsername " +
            "FROM post_comments c " +
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
    List<PostCommentDTO> selectRepliesByCursor(@Param("rootId") Long rootId, @Param("lastId") Long lastId, @Param("size") Integer size);

    @Select("SELECT COUNT(*) FROM post_comments WHERE root_id = #{rootId} AND id != #{rootId} AND is_deleted = 0")
    int countReplies(@Param("rootId") Long rootId);
}
