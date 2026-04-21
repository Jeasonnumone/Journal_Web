package cn.deru.backend.controller;

import cn.deru.backend.dto.CommentDTO;
import cn.deru.backend.dto.CommentRequest;
import cn.deru.backend.service.CommentService;
import cn.deru.backend.model.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 分页查询期刊的一级评论（只展示根评论）
     */
    @GetMapping("/journal/{journalId}")
    public Result<IPage<CommentDTO>> getRootComments(
        @PathVariable Long journalId,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<CommentDTO> comments = commentService.getRootComments(journalId, page, pageSize);
        return Result.success(comments);
    }
    
    /**
     * 分页查询回复列表
     */
    @GetMapping("/{rootId}/replies")
    public Result<IPage<CommentDTO>> getReplies(
        @PathVariable Long rootId,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        IPage<CommentDTO> replies = commentService.getReplies(rootId, page, pageSize);
        return Result.success(replies);
    }
    
    /**
     * 发表评论/回复评论
     */
    @PostMapping
    public Result<Void> createComment(
        @RequestBody CommentRequest request,
        HttpServletRequest httpRequest
    ) {
        // 从用户上下文中获取当前登录用户 ID
        Long currentUserId = getUserIdFromRequest(httpRequest);
        commentService.createComment(request, currentUserId);
        return Result.success(null);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(
        @PathVariable Long commentId,
        HttpServletRequest httpRequest
    ) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        commentService.deleteComment(commentId, currentUserId);
        return Result.success(null);
    }
    
    /**
     * 从请求中获取用户 ID
     */
    private Long getUserIdFromRequest(HttpServletRequest httpRequest) {
        // 从 UserContext 中获取用户 ID
        // 注意：需要确保 JWT 拦截器已经设置了 UserContext
        Long userId = cn.deru.backend.util.UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }
        return userId;
    }
}
