package cn.deru.backend.controller;

import cn.deru.backend.annotation.RateLimit;
import cn.deru.backend.dto.LoginRequest;
import cn.deru.backend.dto.LoginResponse;
import cn.deru.backend.dto.RefreshTokenResponse;
import cn.deru.backend.dto.RegisterRequest;
import cn.deru.backend.dto.UserDTO;
import cn.deru.backend.dto.VerifyCodeRequest;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.AuthService;
import cn.deru.backend.util.UserContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    // 注册 - 限流：每个 IP 每分钟最多 5 次
    @PostMapping("/register")
    @RateLimit(key = "register", time = 60, count = 5, limitType = RateLimit.LimitType.IP, message = "注册请求过于频繁，请稍后再试")
    public Result<UserDTO> register(@RequestBody RegisterRequest request) {
        UserDTO user = authService.register(request);
        return Result.success(user);
    }
    
    // 登录 - 限流：每个 IP 每分钟最多 10 次
    @PostMapping("/login")
    @RateLimit(key = "login", time = 60, count = 10, limitType = RateLimit.LimitType.IP, message = "登录请求过于频繁，请稍后再试")
    public Result<LoginResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request);
        
        // 将 Refresh Token 设置到 HttpOnly Cookie
        Cookie refreshCookie = new Cookie("refreshToken", loginResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // 生产环境 HTTPS 必须为 true
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) refreshExpiration); // 从配置文件读取
        
        response.addCookie(refreshCookie);
        
        // 返回的响应中不包含 Refresh Token
        LoginResponse safeResponse = new LoginResponse();
        safeResponse.setAccessToken(loginResponse.getAccessToken());
        safeResponse.setUser(loginResponse.getUser());
        safeResponse.setAccessTokenExpiresIn(loginResponse.getAccessTokenExpiresIn());
        
        return Result.success(safeResponse);
    }
    
    // 刷新 Token（从 Cookie 中读取 Refresh Token）
    @PostMapping("/refresh")
    public Result<RefreshTokenResponse> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return Result.error(401, "Refresh token not found");
        }
        
        RefreshTokenResponse refreshResponse = authService.refreshToken(refreshToken);
        
        // 更新 Cookie 中的 Refresh Token
        Cookie refreshCookie = new Cookie("refreshToken", refreshResponse.getRefreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // 生产环境 HTTPS 必须为 true
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge((int) refreshExpiration); // 从配置文件读取
        
        response.addCookie(refreshCookie);
        
        return Result.success(refreshResponse);
    }
    
    // 退出登录 - 清除 Cookie 和 Redis 中的 Refresh Token
    @PostMapping("/logout")
    public Result<Void> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isEmpty()) {
            // 调用服务删除 Redis 中的 Refresh Token
            authService.logout(refreshToken);
        }
        
        // 清除 Cookie
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true); // 生产环境 HTTPS 必须为 true
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0); // 立即过期
        
        response.addCookie(refreshCookie);
        
        return Result.success(null);
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
    
    // 发送注册验证码 - 限流：每个 IP 每分钟最多 3 次（防止刷验证码）
    @PostMapping("/verify-code")
    @RateLimit(key = "verify-code", time = 60, count = 3, limitType = RateLimit.LimitType.IP, message = "验证码请求过于频繁，请稍后再试")
    public Result<Void> sendVerifyCode(@RequestBody VerifyCodeRequest request) {
        authService.sendVerifyCode(request.getEmail());
        return Result.success(null);
    }
}
