package cn.deru.backend.service;

import cn.deru.backend.dto.PostDTO;
import cn.deru.backend.dto.PostRequest;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.mapper.PostMapper;
import cn.deru.backend.model.Post;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class PostService {
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private PostViewCacheService postViewCacheService;
    
    /**
     * 合并 Redis 中的浏览量数据到帖子列表（Redis 存储的是总量）
     */
    private void mergeRedisViewCount(List<PostDTO> posts) {
        if (posts == null || posts.isEmpty()) {
            return;
        }
        
        // 提取所有帖子 ID
        Set<Long> postIds = new HashSet<>();
        for (PostDTO post : posts) {
            postIds.add(post.getId());
        }
        
        // 批量获取 Redis 中的浏览量（总量）
        Map<Long, Long> redisViewCounts = postViewCacheService.getMultiViewCount(postIds);
        
        // 直接使用 Redis 的总量（如果 Redis 中有数据）
        for (PostDTO post : posts) {
            Long redisCount = redisViewCounts.get(post.getId());
            if (redisCount != null && redisCount > 0) {
                post.setViewCount(redisCount.intValue());
            }
        }
    }
    
    /**
     * 发表帖子
     */
    @Transactional
    public void createPost(PostRequest request, Long userId) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setViewCount(0);
        post.setIsDeleted(0);
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        
        postMapper.insert(post);
    }
    
    /**
     * 获取最新帖子
     */
    public IPage<PostDTO> getRecentPosts(Integer page, Integer pageSize) {
        Page<PostDTO> postPage = new Page<>(page, pageSize);
        IPage<PostDTO> result = postMapper.selectRecentPosts(postPage);
        
        // 合并 Redis 中的浏览量数据
        mergeRedisViewCount(result.getRecords());
        
        return result;
    }
    
    /**
     * 获取帖子详情
     */
    public PostDTO getPostById(Long id) {
        PostDTO post = postMapper.selectPostDetail(id);
        if (post == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "帖子不存在");
        }
        
        // 增加浏览量（更新到 Redis 缓存，存储的是总量）
        postViewCacheService.incrementViewCount(id);
        
        // 直接使用 Redis 中的总量
        Long redisCount = postViewCacheService.getViewCount(id);
        if (redisCount != null && redisCount > 0) {
            post.setViewCount(redisCount.intValue());
        }
        
        return post;
    }
    
    /**
     * 获取用户的帖子
     */
    public IPage<PostDTO> getUserPosts(Long userId, Integer page, Integer pageSize) {
        Page<PostDTO> postPage = new Page<>(page, pageSize);
        IPage<PostDTO> result = postMapper.selectUserPosts(postPage, userId);
        
        // 合并 Redis 中的浏览量数据
        mergeRedisViewCount(result.getRecords());
        
        return result;
    }
    
    /**
     * 修改帖子
     */
    @Transactional
    public void updatePost(Long postId, PostRequest request, Long currentUserId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "帖子不存在");
        }
        
        if (!post.getUserId().equals(currentUserId)) {
            throw new BusinessException(BusinessCode.FORBIDDEN, "无权修改他人帖子");
        }
        
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setUpdateTime(new Date());
        
        postMapper.updateById(post);
    }
    
    /**
     * 删除帖子（软删除）
     */
    @Transactional
    public void deletePost(Long postId, Long currentUserId) {
        Post post = postMapper.selectById(postId);
        if (post == null || post.getIsDeleted() == 1) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "帖子不存在");
        }
        
        if (!post.getUserId().equals(currentUserId)) {
            throw new BusinessException(BusinessCode.FORBIDDEN, "无权删除他人帖子");
        }
        
        post.setIsDeleted(1);
        post.setUpdateTime(new Date());
        
        postMapper.updateById(post);
    }
}
