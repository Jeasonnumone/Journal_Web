package cn.deru.backend.service;

import cn.deru.backend.dto.ChatConversationDTO;
import cn.deru.backend.dto.ChatMessageDTO;
import cn.deru.backend.dto.CursorPageDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.mapper.ChatConversationMapper;
import cn.deru.backend.mapper.ChatMessageMapper;
import cn.deru.backend.model.ChatConversation;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class ChatService {

    private final ChatConversationMapper conversationMapper;
    private final ChatMessageMapper messageMapper;
    private final UserRepository userRepository;

    public ChatService(ChatConversationMapper conversationMapper,
                       ChatMessageMapper messageMapper,
                       UserRepository userRepository) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.userRepository = userRepository;
    }

    public ChatConversation getOrCreateConversation(Long userId) {
        QueryWrapper<ChatConversation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        ChatConversation conversation = conversationMapper.selectOne(wrapper);
        if (conversation != null) {
            return conversation;
        }

        User admin = findAdmin();
        if (admin == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "暂无在线客服");
        }

        conversation = new ChatConversation();
        conversation.setUserId(userId);
        conversation.setAdminId(admin.getId());
        conversation.setLastMessage("");
        conversation.setLastTime(LocalDateTime.now());
        conversation.setUnreadUser(0);
        conversation.setUnreadAdmin(0);
        conversation.setCreatedAt(LocalDateTime.now());
        conversationMapper.insert(conversation);

        return conversation;
    }

    /**
     * 查询全部聊天记录（保留兼容）
     */
    public List<ChatMessageDTO> getMessages(Long conversationId, Long currentUserId) {
        messageMapper.markAsRead(conversationId, currentUserId);

        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            if (currentUserId.equals(conversation.getUserId())) {
                conversationMapper.resetUnreadUser(conversationId);
            } else {
                conversationMapper.resetUnreadAdmin(conversationId);
            }
        }

        return messageMapper.selectMessagesByConversationId(conversationId);
    }

    /**
     * 游标分页查询聊天记录（优化深度分页）
     * 
     * @param conversationId 会话 ID
     * @param lastId 当前页最早一条消息的 ID（首次查询传 null，加载最新消息）
     * @param size 每页数量
     * @param currentUserId 当前用户 ID（用于标记已读）
     * @return 游标分页结果（消息按时间升序排列）
     */
    public CursorPageDTO<ChatMessageDTO> getMessagesByCursor(Long conversationId, Long lastId, Integer size, Long currentUserId) {
        // 标记已读
        messageMapper.markAsRead(conversationId, currentUserId);
        
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation != null) {
            if (currentUserId.equals(conversation.getUserId())) {
                conversationMapper.resetUnreadUser(conversationId);
            } else {
                conversationMapper.resetUnreadAdmin(conversationId);
            }
        }

        // 查询消息列表（按 ID DESC 查询，返回时反转顺序）
        List<ChatMessageDTO> messages = messageMapper.selectMessagesByCursor(conversationId, lastId, size);
        
        // 反转顺序，使消息按时间升序排列（最早的在上面）
        Collections.reverse(messages);
        
        // 计算下一页游标
        Long nextCursor = null;
        boolean hasMore = false;
        
        if (!messages.isEmpty()) {
            // 最早一条消息的 ID 作为下一页游标（用于加载更早的消息）
            nextCursor = messages.get(0).getId();
            
            // 查询总数判断是否还有更多
            int total = messageMapper.countMessages(conversationId);
            hasMore = messages.size() < total;
        }
        
        return CursorPageDTO.of(messages, nextCursor, hasMore);
    }

    public List<ChatConversationDTO> getUserConversations(Long userId) {
        return conversationMapper.selectConversationByUserId(userId);
    }

    public List<ChatConversationDTO> getAdminConversations(Long adminId) {
        return conversationMapper.selectConversationByAdminId(adminId);
    }

    private User findAdmin() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.apply("LOWER(role) = 'admin'").last("LIMIT 1");
        return userRepository.selectOne(wrapper);
    }
}
