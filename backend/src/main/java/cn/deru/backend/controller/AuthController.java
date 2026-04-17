package cn.deru.backend.controller;

import cn.deru.backend.dto.LoginRequest;
import cn.deru.backend.dto.LoginResponse;
import cn.deru.backend.dto.RegisterRequest;
import cn.deru.backend.model.Result;
import cn.deru.backend.model.User;
import cn.deru.backend.service.AuthService;
import cn.deru.backend.util.UserContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    // 注册
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return Result.success(user);
    }
    
    // 登录
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    // 获取当前登录用户
    @GetMapping("/me")
    public Result<User> getCurrentUser() {
        String username = UserContext.getUsername();
        if (username == null) {
            return Result.error(400, "未登录");
        }

        User user = authService.getCurrentUser(username);
        return Result.success(user);
    }
}