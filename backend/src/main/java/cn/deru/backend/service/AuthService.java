package cn.deru.backend.service;

import cn.deru.backend.dto.LoginRequest;
import cn.deru.backend.dto.LoginResponse;
import cn.deru.backend.dto.RefreshTokenResponse;
import cn.deru.backend.dto.RegisterRequest;
import cn.deru.backend.dto.UserDTO;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import cn.deru.backend.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    // 注册
    public UserDTO register(RegisterRequest request) {
        // 检查用户名是否存在
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        
        userRepository.insert(user);
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
        
        // 构造响应
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        
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
        
        // 检查用户是否存在
//        User user = userRepository.findById(userId);
        User user = userRepository.selectById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // 生成新的 Access Token 和 Refresh Token
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());
        
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        
        return response;
    }
    
    // 获取当前登录用户
    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username);
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }
}
