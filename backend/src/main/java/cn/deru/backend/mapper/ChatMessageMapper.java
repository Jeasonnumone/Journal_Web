package cn.deru.backend.mapper;

import cn.deru.backend.dto.ChatMessageDTO;
import cn.deru.backend.model.ChatMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {

    @Select("SELECT m.*, u.username as senderName, u.avatar as senderAvatar " +
            "FROM chat_message m " +
            "LEFT JOIN user u ON m.sender_id = u.id " +
            "WHERE m.conversation_id = #{conversationId} " +
            "ORDER BY m.created_at ASC")
    List<ChatMessageDTO> selectMessagesByConversationId(@Param("conversationId") Long conversationId);

    @Update("UPDATE chat_message SET is_read = 1 WHERE conversation_id = #{conversationId} AND sender_id != #{currentUserId}")
    void markAsRead(@Param("conversationId") Long conversationId, @Param("currentUserId") Long currentUserId);
}
