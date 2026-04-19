package cn.deru.backend.controller;

import cn.deru.backend.dto.LoginRequest;
import cn.deru.backend.dto.LoginResponse;
import cn.deru.backend.dto.RefreshTokenRequest;
import cn.deru.backend.dto.RefreshTokenResponse;
import cn.deru.backend.dto.RegisterRequest;
import cn.deru.backend.dto.UserDTO;
import cn.deru.backend.model.Result;
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
    public Result<UserDTO> register(@RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return Result.success(user);
    }
    
    // 登录 - 返回 Access Token 和 Refresh Token
    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return Result.success(response);
    }
    
    // 刷新 Token
    @PostMapping("/refresh")
    public Result<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request.getRefreshToken());
        return Result.success(response);
    }
    
    // 获取当前登录用户
    @GetMapping("/me")
    public Result<UserDTO> getCurrentUser() {
        String username = UserContext.getUsername();
        if (username == null) {
            return Result.error(400, "未登录");
        }
        
        UserDTO user = authService.getCurrentUser(username);
        return Result.success(user);
    }
}
