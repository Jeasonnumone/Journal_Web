package cn.deru.backend.service;

import cn.deru.backend.dto.LoginRequest;
import cn.deru.backend.dto.LoginResponse;
import cn.deru.backend.dto.RegisterRequest;
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
    public User register(RegisterRequest request) {
        // 检查用户名是否存在
        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new RuntimeException("Username already exists");
        }
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("USER");
        
        userRepository.insert(user);
        return user;
    }
    
    // 登录
    public LoginResponse login(LoginRequest request) {
        // 检查用户是否存在
        User user = userRepository.findByUsername(request.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        // 生成token，存储用户的id、username、role
        final String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        // 构造响应
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        
        LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo();
        userInfo.setId(user.getId());
        userInfo.setUsername(user.getUsername());
        userInfo.setEmail(user.getEmail());
        userInfo.setRole(user.getRole());
        
        response.setUser(userInfo);
        return response;
    }
    
    // 获取当前登录用户
    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username);
    }
}