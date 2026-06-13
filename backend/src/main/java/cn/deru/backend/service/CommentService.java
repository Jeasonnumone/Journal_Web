package cn.deru.backend.service;

import cn.deru.backend.dto.CommentDTO;
import cn.deru.backend.dto.CommentRequest;
import cn.deru.backend.dto.RecentCommentDTO;
import cn.deru.backend.mapper.CommentMapper;
import cn.deru.backend.model.Comment;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 分页查询期刊的一级评论（只展示根评论）
     */
    public IPage<CommentDTO> getRootComments(Long journalId, Integer page, Integer pageSize) {
        Page<CommentDTO> commentPage = new Page<>(page, pageSize);
        return commentMapper.selectRootComments(commentPage, journalId);
    }
    
    /**
     * 分页查询回复列表
     */
    public IPage<CommentDTO> getReplies(Long rootId, Integer page, Integer pageSize) {
        Page<CommentDTO> commentPage = new Page<>(page, pageSize);
        return commentMapper.selectReplies(commentPage, rootId);
    }
    
    /**
     * 发表评论/回复评论
     */
    @Transactional
    public void createComment(CommentRequest request, Long currentUserId) {
        // 验证用户是否存在
        User user = userRepository.selectById(currentUserId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        Comment comment = new Comment();
        comment.setJournalId(request.getJournalId());
        comment.setUserId(currentUserId);
        comment.setContent(request.getContent());
        comment.setIsDeleted(0); // 默认未删除
        comment.setReplyCount(0);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        
        // 投稿体验字段
        comment.setReviewTime(request.getReviewTime());
        comment.setIsAccepted(request.getIsAccepted());
        comment.setPublishPeriod(request.getPublishPeriod());
        comment.setIsFirstPublish(request.getIsFirstPublish());
        comment.setReviewFee(request.getReviewFee());
        comment.setPageFee(request.getPageFee());
        comment.setPayment(request.getPayment());
        comment.setWordCount(request.getWordCount());
        comment.setEducation(request.getEducation());
        comment.setTitle(request.getTitle());
        comment.setHasProject(request.getHasProject());
        comment.setHasReply(request.getHasReply());
        comment.setPublishType(request.getPublishType());
        comment.setTopic(request.getTopic());
        comment.setImages(request.getImages());
        
        // 判断是一级评论还是回复
        if (request.getParentId() == null) {
            // 一级评论
            comment.setRootId(null); // 先保存，保存后设置为自身 ID
            comment.setParentId(null);
            comment.setReplyToUserId(null);
        } else {
            // 回复评论
            comment.setParentId(request.getParentId());
            comment.setRootId(request.getRootId());
            comment.setReplyToUserId(request.getReplyToUserId());
        }
        
        // 保存评论
        commentMapper.insert(comment);
        
        // 如果是一级评论，设置 root_id 为自身 ID
        if (request.getParentId() == null) {
            comment.setRootId(comment.getId());
            commentMapper.updateById(comment);
        } else {
            // 如果是回复，更新根评论的 reply_count
            Comment rootComment = commentMapper.selectById(request.getRootId());
            if (rootComment != null) {
                rootComment.setReplyCount(rootComment.getReplyCount() + 1);
                commentMapper.updateById(rootComment);
            }
        }
    }
    
    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 验证权限：只有评论作者或管理员可以删除
        User user = userRepository.selectById(currentUserId);
        if (!comment.getUserId().equals(currentUserId) && !"ADMIN".equals(user.getRole())) {
            throw new RuntimeException("无权删除他人评论");
        }
        
        // 软删除
        comment.setIsDeleted(1);
        commentMapper.updateById(comment);
        
        // 如果是回复评论，更新根评论的 reply_count
        if (comment.getParentId() != null) {
            Comment rootComment = commentMapper.selectById(comment.getRootId());
            if (rootComment != null) {
                rootComment.setReplyCount(Math.max(rootComment.getReplyCount() - 1, 0));
                commentMapper.updateById(rootComment);
            }
        }
    }
    
    /**
     * 查询最新评论（跨期刊）
     */
    public java.util.List<RecentCommentDTO> getRecentComments(Integer limit) {
        return commentMapper.selectRecentComments(limit);
    }
    
    /**
     * 查询用户的评论
     */
    public IPage<RecentCommentDTO> getUserComments(Long userId, Integer page, Integer pageSize) {
        Page<RecentCommentDTO> commentPage = new Page<>(page, pageSize);
        return commentMapper.selectUserComments(commentPage, userId);
    }
}
