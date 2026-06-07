package cn.deru.backend.controller.admin;

import cn.deru.backend.dto.RecentCommentDTO;
import cn.deru.backend.mapper.CommentMapper;
import cn.deru.backend.model.Comment;
import cn.deru.backend.model.Result;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/admin/comments")
@CrossOrigin(origins = "*")
public class AdminCommentController {

    @Autowired
    private CommentMapper commentMapper;

    @GetMapping
    public Result<IPage<RecentCommentDTO>> getComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<RecentCommentDTO> commentPage = new Page<>(page, pageSize);
        IPage<RecentCommentDTO> result = commentMapper.selectAllCommentsAdmin(commentPage);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.error(4040, "评论不存在");
        }
        comment.setIsDeleted(1);
        comment.setUpdateTime(new Date());
        commentMapper.updateById(comment);
        return Result.success(null);
    }
}