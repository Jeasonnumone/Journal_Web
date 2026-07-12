package cn.deru.backend.controller;

import cn.deru.backend.annotation.RateLimit;
import cn.deru.backend.dto.CursorPageDTO;
import cn.deru.backend.dto.PostCommentDTO;
import cn.deru.backend.dto.PostCommentRequest;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.PostCommentService;
import cn.deru.backend.util.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post-comments")
@CrossOrigin(origins = "*")
public class PostCommentController {

    @Autowired
    private PostCommentService postCommentService;

    /**
     * 分页查询帖子的一级评论
     */
    @GetMapping("/post/{postId}")
    public Result<IPage<PostCommentDTO>> getRootComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<PostCommentDTO> comments = postCommentService.getRootComments(postId, page, pageSize);
        return Result.success(comments);
    }

    /**
     * 传统分页查询回复列表（保留兼容）
     */
    @GetMapping("/{rootId}/replies")
    public Result<IPage<PostCommentDTO>> getReplies(
            @PathVariable Long rootId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize
    ) {
        IPage<PostCommentDTO> replies = postCommentService.getReplies(rootId, page, pageSize);
        return Result.success(replies);
    }

    /**
     * 游标分页查询回复列表
     */
    @GetMapping("/{rootId}/replies/cursor")
    public Result<CursorPageDTO<PostCommentDTO>> getRepliesByCursor(
            @PathVariable Long rootId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") Integer size
    ) {
        CursorPageDTO<PostCommentDTO> replies = postCommentService.getRepliesByCursor(rootId, cursor, size);
        return Result.success(replies);
    }

    /**
     * 发表评论/回复 - 限流：每个用户每分钟最多 10 次
     */
    @PostMapping
    @RateLimit(key = "post-comment-create", time = 60, count = 10, limitType = RateLimit.LimitType.USER, message = "评论过于频繁，请稍后再试")
    public Result<Void> createComment(@RequestBody PostCommentRequest request) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(BusinessCode.UNAUTHORIZED, "用户未登录");
        }
        postCommentService.createComment(request, currentUserId);
        return Result.success(null);
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable Long commentId) {
        Long currentUserId = UserContext.getUserId();
        if (currentUserId == null) {
            throw new BusinessException(BusinessCode.UNAUTHORIZED, "用户未登录");
        }
        postCommentService.deleteComment(commentId, currentUserId);
        return Result.success(null);
    }
}
