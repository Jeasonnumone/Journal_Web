package cn.deru.backend.controller.admin;

import cn.deru.backend.dto.PostDTO;
import cn.deru.backend.mapper.PostMapper;
import cn.deru.backend.model.Post;
import cn.deru.backend.model.Result;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/admin/posts")
@CrossOrigin(origins = "*")
public class AdminPostController {

    @Autowired
    private PostMapper postMapper;

    @GetMapping
    public Result<IPage<PostDTO>> getPosts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        Page<PostDTO> postPage = new Page<>(page, pageSize);
        IPage<PostDTO> result = postMapper.selectAllPostsAdmin(postPage);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deletePost(@PathVariable Long id) {
        Post post = postMapper.selectById(id);
        if (post == null || post.getIsDeleted() == 1) {
            return Result.error(4040, "帖子不存在");
        }
        post.setIsDeleted(1);
        post.setUpdateTime(new Date());
        postMapper.updateById(post);
        return Result.success(null);
    }
}