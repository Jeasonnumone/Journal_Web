package cn.deru.backend.controller.admin;

import cn.deru.backend.dto.RecentCommentDTO;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.admin.AdminCommentService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
@CrossOrigin(origins = "*")
public class AdminCommentController {

    @Autowired
    private AdminCommentService adminCommentService;

    @GetMapping
    public Result<IPage<RecentCommentDTO>> getComments(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<RecentCommentDTO> result = adminCommentService.getComments(page, pageSize);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        try {
            adminCommentService.deleteComment(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(4040, e.getMessage());
        }
    }
}