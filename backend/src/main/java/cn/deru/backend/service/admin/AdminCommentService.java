package cn.deru.backend.service.admin;

import cn.deru.backend.dto.RecentCommentDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.mapper.CommentMapper;
import cn.deru.backend.model.Comment;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AdminCommentService {

    @Autowired
    private CommentMapper commentMapper;

    /**
     * 分页查询所有评论（升序）
     */
    public IPage<RecentCommentDTO> getComments(Integer page, Integer pageSize) {
        Page<RecentCommentDTO> commentPage = new Page<>(page, pageSize);
        return commentMapper.selectAllCommentsAdminAsc(commentPage);
    }

    /**
     * 删除评论（软删除）
     */
    public void deleteComment(Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "评论不存在");
        }
        comment.setIsDeleted(1);
        comment.setUpdateTime(new Date());
        commentMapper.updateById(comment);
    }
}