package cn.deru.backend.controller.admin;

import cn.deru.backend.model.Result;
import cn.deru.backend.model.User;
import cn.deru.backend.service.admin.AdminUserService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping
    public Result<IPage<User>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        IPage<User> result = adminUserService.getUsers(page, pageSize, keyword);
        return Result.success(result);
    }

    @PutMapping("/{id}/role")
    public Result<Void> updateUserRole(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        try {
            String role = body.get("role");
            adminUserService.updateUserRole(id, role);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(4000, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        try {
            adminUserService.deleteUser(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(4040, e.getMessage());
        }
    }
}