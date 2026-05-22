package cn.deru.backend.service;

import cn.deru.backend.dto.ChatConversationDTO;
import cn.deru.backend.dto.ChatMessageDTO;
import cn.deru.backend.mapper.ChatConversationMapper;
import cn.deru.backend.mapper.ChatMessageMapper;
import cn.deru.backend.model.ChatConversation;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
            throw new RuntimeException("暂无在线客服");
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

    public List<ChatConversationDTO> getUserConversations(Long userId) {
        return conversationMapper.selectConversationByUserId(userId);
    }

    public List<ChatConversationDTO> getAllConversations() {
        return conversationMapper.selectConversationList();
    }

    private User findAdmin() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.apply("LOWER(role) = 'admin'").last("LIMIT 1");
        return userRepository.selectOne(wrapper);
    }
}
