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

@Service
public class PostService {
    
    @Autowired
    private PostMapper postMapper;
    
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
        return postMapper.selectRecentPosts(postPage);
    }
    
    /**
     * 获取帖子详情
     */
    public PostDTO getPostById(Long id) {
        PostDTO post = postMapper.selectPostDetail(id);
        if (post == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "帖子不存在");
        }
        
        // 增加浏览量
        postMapper.incrementViewCount(id);
        
        return post;
    }
    
    /**
     * 获取用户的帖子
     */
    public IPage<PostDTO> getUserPosts(Long userId, Integer page, Integer pageSize) {
        Page<PostDTO> postPage = new Page<>(page, pageSize);
        return postMapper.selectUserPosts(postPage, userId);
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
