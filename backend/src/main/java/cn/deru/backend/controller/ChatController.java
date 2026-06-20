package cn.deru.backend.controller;

import cn.deru.backend.dto.ChatConversationDTO;
import cn.deru.backend.dto.ChatMessageDTO;
import cn.deru.backend.dto.CursorPageDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
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

    /**
     * 查询全部聊天记录（保留兼容）
     */
    @GetMapping("/messages/{conversationId}")
    public Result<List<ChatMessageDTO>> getMessages(@PathVariable Long conversationId) {
        Long userId = UserContext.getUserId();
        List<ChatMessageDTO> messages = chatService.getMessages(conversationId, userId);
        return Result.success(messages);
    }

    /**
     * 游标分页查询聊天记录（优化深度分页）
     * 
     * @param conversationId 会话 ID
     * @param cursor 当前页最早一条消息的 ID（首次查询传 null，加载最新消息）
     * @param size 每页数量
     * @return 游标分页结果
     */
    @GetMapping("/messages/{conversationId}/cursor")
    public Result<CursorPageDTO<ChatMessageDTO>> getMessagesByCursor(
        @PathVariable Long conversationId,
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        Long userId = UserContext.getUserId();
        CursorPageDTO<ChatMessageDTO> messages = chatService.getMessagesByCursor(conversationId, cursor, size, userId);
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
        String role = UserContext.getRole();
        if (role == null || !role.equalsIgnoreCase("admin")) {
            throw new BusinessException(BusinessCode.FORBIDDEN, "无权访问");
        }
        Long adminId = UserContext.getUserId();
        List<ChatConversationDTO> conversations = chatService.getAdminConversations(adminId);
        return Result.success(conversations);
    }
}
