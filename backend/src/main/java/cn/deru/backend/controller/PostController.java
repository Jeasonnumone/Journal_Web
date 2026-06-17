package cn.deru.backend.controller;

import cn.deru.backend.annotation.RateLimit;
import cn.deru.backend.dto.PostDTO;
import cn.deru.backend.dto.PostRequest;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.PostService;
import cn.deru.backend.util.UserContext;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    /**
     * 发表帖子 - 限流：每个用户每分钟最多 5 次
     */
    @PostMapping
    @RateLimit(key = "post-create", time = 60, count = 5, limitType = RateLimit.LimitType.USER, message = "发帖过于频繁，请稍后再试")
    public Result<Void> createPost(@RequestBody PostRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(4010, "发表帖子前, 请先登录");
        }
        
        postService.createPost(request, userId);
        return Result.success(null);
    }
    
    /**
     * 获取最新帖子
     */
    @GetMapping("/recent")
    public Result<IPage<PostDTO>> getRecentPosts(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<PostDTO> posts = postService.getRecentPosts(page, pageSize);
        return Result.success(posts);
    }
    
    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public Result<PostDTO> getPostDetail(@PathVariable Long id) {
        PostDTO post = postService.getPostById(id);
        return Result.success(post);
    }
    
    /**
     * 获取用户的帖子
     */
    @GetMapping("/user/{userId}")
    public Result<IPage<PostDTO>> getUserPosts(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<PostDTO> posts = postService.getUserPosts(userId, page, pageSize);
        return Result.success(posts);
    }
    
    /**
     * 修改帖子
     */
    @PutMapping("/{id}")
    public Result<Void> updatePost(@PathVariable Long id, @RequestBody PostRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(4010, "请先登录");
        }
        
        postService.updatePost(id, request, userId);
        return Result.success(null);
    }
    
    /**
     * 删除帖子
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(4010, "请先登录");
        }
        
        postService.deletePost(id, userId);
        return Result.success(null);
    }
}
