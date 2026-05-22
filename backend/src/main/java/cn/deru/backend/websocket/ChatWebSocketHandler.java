package cn.deru.backend.websocket;

import cn.deru.backend.dto.ChatMessageDTO;
import cn.deru.backend.mapper.ChatConversationMapper;
import cn.deru.backend.mapper.ChatMessageMapper;
import cn.deru.backend.model.ChatConversation;
import cn.deru.backend.model.ChatMessage;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ChatConversationMapper conversationMapper;
    private final ChatMessageMapper messageMapper;
    private final UserRepository userRepository;

    private static final Map<Long, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ChatConversationMapper conversationMapper,
                                ChatMessageMapper messageMapper,
                                UserRepository userRepository) {
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
            userSessions.put(userId, session);
            log.info("WebSocket 连接建立: userId={}", userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
        Long senderId = getUserId(session);
        if (senderId == null) {
            return;
        }

        JsonNode json = objectMapper.readTree(textMessage.getPayload());
        String type = json.get("type").asText();

        if ("chat".equals(type)) {
            Long conversationId = json.get("conversationId").asLong();
            String content = json.get("content").asText();
            handleChatMessage(senderId, conversationId, content);
        }
    }

    private void handleChatMessage(Long senderId, Long conversationId, String content) {
        ChatConversation conversation = conversationMapper.selectById(conversationId);
        if (conversation == null) {
            return;
        }

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversationId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setIsRead(0);
        message.setCreatedAt(LocalDateTime.now());
        messageMapper.insert(message);

        conversation.setLastMessage(content);
        conversation.setLastTime(LocalDateTime.now());

        Long receiverId;
        boolean isUserSender = senderId.equals(conversation.getUserId());
        if (isUserSender) {
            conversation.setUnreadAdmin(conversation.getUnreadAdmin() + 1);
            receiverId = conversation.getAdminId();
        } else {
            conversation.setUnreadUser(conversation.getUnreadUser() + 1);
            receiverId = conversation.getUserId();
        }
        conversationMapper.updateById(conversation);

        User sender = userRepository.selectById(senderId);
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(conversationId);
        dto.setSenderId(senderId);
        dto.setSenderName(sender != null ? sender.getUsername() : "未知");
        dto.setSenderAvatar(sender != null ? sender.getAvatar() : null);
        dto.setContent(content);
        dto.setIsRead(0);
        dto.setCreatedAt(message.getCreatedAt());

        sendMessageToUser(senderId, dto);
        sendMessageToUser(receiverId, dto);
    }

    private void sendMessageToUser(Long userId, ChatMessageDTO dto) {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(dto);
                session.sendMessage(new TextMessage(json));
            } catch (Exception e) {
                log.error("发送 WebSocket 消息失败: userId={}", userId, e);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserId(session);
        if (userId != null) {
            userSessions.remove(userId);
            log.info("WebSocket 连接关闭: userId={}", userId);
        }
    }

    private Long getUserId(WebSocketSession session) {
        Object userId = session.getAttributes().get("userId");
        return userId != null ? (Long) userId : null;
    }
}
