package cn.deru.backend.controller;

import cn.deru.backend.dto.ChatConversationDTO;
import cn.deru.backend.dto.ChatMessageDTO;
import cn.deru.backend.model.ChatConversation;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.ChatService;
import cn.deru.backend.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/conversation")
    public Result<ChatConversation> createConversation() {
        Long userId = UserContext.getUserId();
        ChatConversation conversation = chatService.getOrCreateConversation(userId);
        return Result.success(conversation);
    }

    @GetMapping("/messages/{conversationId}")
    public Result<List<ChatMessageDTO>> getMessages(@PathVariable Long conversationId) {
        Long userId = UserContext.getUserId();
        List<ChatMessageDTO> messages = chatService.getMessages(conversationId, userId);
        return Result.success(messages);
    }

    @GetMapping("/conversations")
    public Result<List<ChatConversationDTO>> getConversations() {
        Long userId = UserContext.getUserId();
        List<ChatConversationDTO> conversations = chatService.getUserConversations(userId);
        return Result.success(conversations);
    }

    @GetMapping("/admin/conversations")
    public Result<List<ChatConversationDTO>> getAdminConversations() {
        List<ChatConversationDTO> conversations = chatService.getAllConversations();
        return Result.success(conversations);
    }
}
