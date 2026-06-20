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

    /**
     * 查询全部聊天记录（保留兼容）
     */
    @Select("SELECT m.*, u.username as senderName, u.avatar as senderAvatar " +
            "FROM chat_message m " +
            "LEFT JOIN user u ON m.sender_id = u.id " +
            "WHERE m.conversation_id = #{conversationId} " +
            "ORDER BY m.created_at ASC")
    List<ChatMessageDTO> selectMessagesByConversationId(@Param("conversationId") Long conversationId);

    /**
     * 游标分页查询聊天记录（优化深度分页）
     * 基于 lastId 查询更早的消息（向上翻页）
     * 
     * @param conversationId 会话 ID
     * @param lastId 当前页最早一条消息的 ID（首次查询传 null，加载最新消息）
     * @param size 每页数量
     * @return 聊天记录列表（按时间升序）
     */
    @Select("<script>" +
            "SELECT m.*, u.username as senderName, u.avatar as senderAvatar " +
            "FROM chat_message m " +
            "LEFT JOIN user u ON m.sender_id = u.id " +
            "WHERE m.conversation_id = #{conversationId} " +
            "<if test='lastId != null'>" +
            "AND m.id &lt; #{lastId} " +
            "</if>" +
            "ORDER BY m.id DESC " +
            "LIMIT #{size}" +
            "</script>")
    List<ChatMessageDTO> selectMessagesByCursor(@Param("conversationId") Long conversationId, @Param("lastId") Long lastId, @Param("size") Integer size);

    /**
     * 查询会话消息总数
     */
    @Select("SELECT COUNT(*) FROM chat_message WHERE conversation_id = #{conversationId}")
    int countMessages(@Param("conversationId") Long conversationId);

    @Update("UPDATE chat_message SET is_read = 1 WHERE conversation_id = #{conversationId} AND sender_id != #{currentUserId}")
    void markAsRead(@Param("conversationId") Long conversationId, @Param("currentUserId") Long currentUserId);
}
