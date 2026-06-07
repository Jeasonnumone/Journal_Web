package cn.deru.backend.controller.admin;

import cn.deru.backend.model.Result;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public Result<IPage<User>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<User> userPage = new Page<>(page, pageSize);
        
        if (keyword != null && !keyword.isEmpty()) {
            // 按用户名或邮箱搜索
            Page<User> result = userRepository.selectPage(userPage,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                    .like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword)
                    .orderByDesc(User::getCreateTime)
            );
            return Result.success(result);
        }
        
        Page<User> result = userRepository.selectPage(userPage,
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreateTime)
        );
        return Result.success(result);
    }

    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        User user = userRepository.selectById(id);
        if (user == null) {
            return Result.error(4040, "用户不存在");
        }
        String role = body.get("role");
        if (role == null || (!role.equals("USER") && !role.equals("SUPPORT") && !role.equals("ADMIN"))) {
            return Result.error(4000, "无效的角色");
        }
        user.setRole(role);
        user.setUpdateTime(java.time.LocalDateTime.now());
        userRepository.updateById(user);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        User user = userRepository.selectById(id);
        if (user == null) {
            return Result.error(4040, "用户不存在");
        }
        userRepository.deleteById(id);
        return Result.success(null);
    }
}