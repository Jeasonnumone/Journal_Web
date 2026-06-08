package cn.deru.backend.controller.admin;

import cn.deru.backend.dto.PostDTO;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.admin.AdminPostService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/posts")
@CrossOrigin(origins = "*")
public class AdminPostController {

    @Autowired
    private AdminPostService adminPostService;

    @GetMapping
    public Result<IPage<PostDTO>> getPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<PostDTO> result = adminPostService.getPosts(page, pageSize);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        try {
            adminPostService.deletePost(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(4040, e.getMessage());
        }
    }
}