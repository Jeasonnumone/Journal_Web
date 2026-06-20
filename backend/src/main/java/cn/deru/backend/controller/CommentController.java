package cn.deru.backend.controller;

import cn.deru.backend.annotation.RateLimit;
import cn.deru.backend.dto.CommentDTO;
import cn.deru.backend.dto.CommentRequest;
import cn.deru.backend.dto.CursorPageDTO;
import cn.deru.backend.dto.RecentCommentDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.service.CommentService;
import cn.deru.backend.service.MinioService;
import cn.deru.backend.model.Result;
import cn.deru.backend.util.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private MinioService minioService;
    
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
     * 传统分页查询回复列表（保留兼容）
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
     * 游标分页查询回复列表（优化深度分页）
     * 
     * @param rootId 根评论 ID
     * @param cursor 上一页最后一条回复的 ID（首次查询传 null）
     * @param size 每页数量
     * @return 游标分页结果
     */
    @GetMapping("/{rootId}/replies/cursor")
    public Result<CursorPageDTO<CommentDTO>> getRepliesByCursor(
        @PathVariable Long rootId,
        @RequestParam(required = false) Long cursor,
        @RequestParam(defaultValue = "20") Integer size
    ) {
        CursorPageDTO<CommentDTO> replies = commentService.getRepliesByCursor(rootId, cursor, size);
        return Result.success(replies);
    }
    
    /**
     * 查询最新评论（跨期刊）
     */
    @GetMapping("/recent")
    public Result<java.util.List<RecentCommentDTO>> getRecentComments(
        @RequestParam(defaultValue = "10") Integer limit
    ) {
        java.util.List<RecentCommentDTO> comments = commentService.getRecentComments(limit);
        return Result.success(comments);
    }
    
    /**
     * 查询我的评论
     */
    @GetMapping("/user")
    public Result<IPage<RecentCommentDTO>> getUserComments(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(4010, "请先登录");
        }
        
        IPage<RecentCommentDTO> comments = commentService.getUserComments(userId, page, pageSize);
        return Result.success(comments);
    }
    
    /**
     * 发表评论/回复评论 - 限流：每个用户每分钟最多 10 次
     */
    @PostMapping
    @RateLimit(key = "comment-create", time = 60, count = 10, limitType = RateLimit.LimitType.USER, message = "评论过于频繁，请稍后再试")
    public Result<Void> createComment(
        @RequestBody CommentRequest request,
        HttpServletRequest httpRequest
    ) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        commentService.createComment(request, currentUserId);
        return Result.success(null);
    }
    
    /**
     * 上传评论图片，返回图片URL - 限流：每个用户每分钟最多 20 次
     */
    @PostMapping("/upload-image")
    @RateLimit(key = "image-upload", time = 60, count = 20, limitType = RateLimit.LimitType.USER, message = "图片上传过于频繁，请稍后再试")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(4010, "请先登录");
        }
        
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "只支持图片文件");
        }
        
        if (file.getSize() > 5 * 1024 * 1024) {
            return Result.error(400, "图片大小不能超过5MB");
        }
        
        try {
            String imageUrl = minioService.uploadFile(file, "comments");
            return Result.success(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "上传失败：" + e.getMessage());
        }
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
            throw new BusinessException(BusinessCode.UNAUTHORIZED, "用户未登录");
        }
        return userId;
    }
}
