package cn.deru.backend.mapper;

import cn.deru.backend.dto.ChatConversationDTO;
import cn.deru.backend.model.ChatConversation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatConversationMapper extends BaseMapper<ChatConversation> {

    @Select("SELECT c.*, " +
            "u.username, u.avatar as userAvatar, " +
            "a.username as adminName, a.avatar as adminAvatar " +
            "FROM chat_conversation c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user a ON c.admin_id = a.id " +
            "WHERE c.admin_id = #{adminId} " +
            "ORDER BY c.last_time DESC")
    List<ChatConversationDTO> selectConversationByAdminId(@Param("adminId") Long adminId);

    @Select("SELECT c.*, " +
            "u.username, u.avatar as userAvatar, " +
            "a.username as adminName, a.avatar as adminAvatar " +
            "FROM chat_conversation c " +
            "LEFT JOIN user u ON c.user_id = u.id " +
            "LEFT JOIN user a ON c.admin_id = a.id " +
            "WHERE c.user_id = #{userId} " +
            "ORDER BY c.last_time DESC")
    List<ChatConversationDTO> selectConversationByUserId(@Param("userId") Long userId);

    @Update("UPDATE chat_conversation SET unread_admin = 0 WHERE id = #{id}")
    void resetUnreadAdmin(@Param("id") Long id);

    @Update("UPDATE chat_conversation SET unread_user = 0 WHERE id = #{id}")
    void resetUnreadUser(@Param("id") Long id);
}
