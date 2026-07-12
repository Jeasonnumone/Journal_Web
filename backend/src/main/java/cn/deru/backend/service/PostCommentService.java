package cn.deru.backend.service;

import cn.deru.backend.dto.CursorPageDTO;
import cn.deru.backend.dto.PostCommentDTO;
import cn.deru.backend.dto.PostCommentRequest;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.mapper.PostCommentMapper;
import cn.deru.backend.model.PostComment;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class PostCommentService {

    @Autowired
    private PostCommentMapper postCommentMapper;

    @Autowired
    private UserRepository userRepository;

    /**
     * 分页查询帖子的一级评论
     */
    public IPage<PostCommentDTO> getRootComments(Long postId, Integer page, Integer pageSize) {
        Page<PostCommentDTO> commentPage = new Page<>(page, pageSize);
        return postCommentMapper.selectRootComments(commentPage, postId);
    }

    /**
     * 传统分页查询回复列表
     */
    public IPage<PostCommentDTO> getReplies(Long rootId, Integer page, Integer pageSize) {
        Page<PostCommentDTO> commentPage = new Page<>(page, pageSize);
        return postCommentMapper.selectReplies(commentPage, rootId);
    }

    /**
     * 游标分页查询回复列表
     */
    public CursorPageDTO<PostCommentDTO> getRepliesByCursor(Long rootId, Long lastId, Integer size) {
        List<PostCommentDTO> replies = postCommentMapper.selectRepliesByCursor(rootId, lastId, size);

        Long nextCursor = null;
        boolean hasMore = false;

        if (!replies.isEmpty()) {
            nextCursor = replies.get(replies.size() - 1).getId();
            int total = postCommentMapper.countReplies(rootId);
            hasMore = replies.size() < total;
        }

        return CursorPageDTO.of(replies, nextCursor, hasMore);
    }

    /**
     * 发表评论/回复评论
     */
    @Transactional
    public void createComment(PostCommentRequest request, Long currentUserId) {
        User user = userRepository.selectById(currentUserId);
        if (user == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "用户不存在");
        }

        PostComment comment = new PostComment();
        comment.setPostId(request.getPostId());
        comment.setUserId(currentUserId);
        comment.setContent(request.getContent());
        comment.setIsDeleted(0);
        comment.setReplyCount(0);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());

        if (request.getParentId() == null) {
            // 一级评论
            comment.setRootId(null);
            comment.setParentId(null);
            comment.setReplyToUserId(null);
        } else {
            // 回复评论
            comment.setParentId(request.getParentId());
            comment.setRootId(request.getRootId());
            comment.setReplyToUserId(request.getReplyToUserId());
        }

        postCommentMapper.insert(comment);

        // 一级评论：设置 root_id 为自身 ID
        if (request.getParentId() == null) {
            comment.setRootId(comment.getId());
            postCommentMapper.updateById(comment);
        } else {
            // 回复：更新根评论的 reply_count
            PostComment rootComment = postCommentMapper.selectById(request.getRootId());
            if (rootComment != null) {
                rootComment.setReplyCount(rootComment.getReplyCount() + 1);
                postCommentMapper.updateById(rootComment);
            }
        }
    }

    /**
     * 删除评论（软删除）
     */
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        PostComment comment = postCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "评论不存在");
        }

        User user = userRepository.selectById(currentUserId);
        if (!comment.getUserId().equals(currentUserId) && !"ADMIN".equals(user.getRole())) {
            throw new BusinessException(BusinessCode.FORBIDDEN, "无权删除他人评论");
        }

        comment.setIsDeleted(1);
        postCommentMapper.updateById(comment);

        // 回复评论：更新根评论的 reply_count
        if (comment.getParentId() != null) {
            PostComment rootComment = postCommentMapper.selectById(comment.getRootId());
            if (rootComment != null) {
                rootComment.setReplyCount(Math.max(rootComment.getReplyCount() - 1, 0));
                postCommentMapper.updateById(rootComment);
            }
        }
    }
}
