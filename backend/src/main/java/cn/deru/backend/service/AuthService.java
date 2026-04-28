package cn.deru.backend.service;

import cn.deru.backend.dto.LoginRequest;
import cn.deru.backend.dto.LoginResponse;
import cn.deru.backend.dto.RefreshTokenResponse;
import cn.deru.backend.dto.RegisterRequest;
import cn.deru.backend.dto.UserDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import cn.deru.backend.util.JwtUtil;
import cn.deru.backend.util.VerifyCodeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    
    @Value("${verify.code.length:6}")
    private int codeLength;
    
    @Value("${verify.code.expire:300}")
    private int codeExpire;
    
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, StringRedisTemplate redisTemplate, EmailService emailService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.redisTemplate = redisTemplate;
        this.emailService = emailService;
    }
    
    // 注册
    public UserDTO register(RegisterRequest request) {
        // 验证邮箱验证码
        String redisKey = "verify_code:" + request.getEmail();
        String savedCode = redisTemplate.opsForValue().get(redisKey);
        
        if (savedCode == null) {
            throw new BusinessException(BusinessCode.VERIFY_CODE_EXPIRED);
        }
        
        if (!savedCode.equals(request.getVerifyCode())) {
            throw new BusinessException(BusinessCode.VERIFY_CODE_ERROR);
        }
        
        // 检查用户名是否存在
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new BusinessException(BusinessCode.USER_ALREADY_EXISTS);
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        
        userRepository.insert(user);
        
        // 删除已使用的验证码
        redisTemplate.delete(redisKey);
        
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
    
    // 登录 - 返回 Access Token 和 Refresh Token
    public LoginResponse login(LoginRequest request) {
        // 检查用户是否存在
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 生成 Access Token 和 Refresh Token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        // 将 Refresh Token 存储到 Redis
        String redisKey = "refresh_token:" + user.getId();
        redisTemplate.opsForValue().set(redisKey, refreshToken, jwtUtil.getRefreshExpiration(), TimeUnit.SECONDS);
        
        // 构造响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setAccessTokenExpiresIn(jwtUtil.getAccessExpiration()); // 设置 Access Token 过期时间
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());
        
        response.setUser(userInfo);
        return response;
    }
    
    // 刷新 Token
    public RefreshTokenResponse refreshToken(String refreshToken) {
        // 验证 Refresh Token
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        // 从 Refresh Token 中获取 userId
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        
        // 从 Redis 中获取存储的 Refresh Token
        String redisKey = "refresh_token:" + userId;
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);
        
        // 检查 Redis 中是否存在该 Refresh Token
        if (storedRefreshToken == null) {
            throw new RuntimeException("Refresh token not found in Redis");
        }
        
        // 验证传入的 Refresh Token 是否与 Redis 中存储的一致
        if (!storedRefreshToken.equals(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        // 检查用户是否存在
//        User user = userRepository.findById(userId);
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // 生成新的 Access Token 和 Refresh Token
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        // 更新 Redis 中的 Refresh Token
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, jwtUtil.getRefreshExpiration(), TimeUnit.SECONDS);
        
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        response.setAccessTokenExpiresIn(jwtUtil.getAccessExpiration());
        
        return response;
    }
    
    // 获取当前登录用户
    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username);
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
    
    // 退出登录 - 删除 Redis 中的 Refresh Token
    public void logout(String refreshToken) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            String redisKey = "refresh_token:" + userId;
            redisTemplate.delete(redisKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 发送验证码
    public void sendVerifyCode(String email) {
        // 检查邮箱是否已注册
        User user = userRepository.findByEmail(email);
        if (user != null) {
            throw new BusinessException(BusinessCode.EMAIL_ALREADY_EXISTS);
        }
        
        // 生成验证码
        String code = VerifyCodeUtil.generateCode(codeLength);

        // 发送邮件
        emailService.sendVerifyCode(email, code);
        
        // 存储到 Redis
        String redisKey = "verify_code:" + email;
        redisTemplate.opsForValue().set(redisKey, code, codeExpire, TimeUnit.SECONDS);

    }
}
