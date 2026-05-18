package cn.deru.backend.controller;

import cn.deru.backend.dto.ChangePasswordRequest;
import cn.deru.backend.dto.UserDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.model.Result;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import cn.deru.backend.service.AuthService;
import cn.deru.backend.service.MinioService;
import cn.deru.backend.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private MinioService minioService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * 获取个人信息
     */
    @GetMapping("/profile")
    public Result<UserDTO> getProfile() {
        String username = UserContext.getUsername();
        if (username == null) {
            return Result.error(BusinessCode.UNAUTHORIZED.getCode(), BusinessCode.UNAUTHORIZED.getMessage());
        }
        
        UserDTO user = authService.getCurrentUser(username);
        return Result.success(user);
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<Void> changePassword(@RequestBody ChangePasswordRequest request) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(BusinessCode.UNAUTHORIZED.getCode(), BusinessCode.UNAUTHORIZED.getMessage());
        }
        
        authService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
        return Result.success(null);
    }
    
    /**
     * 上传头像
     */
    @PostMapping("/avatar")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            return Result.error(BusinessCode.UNAUTHORIZED.getCode(), BusinessCode.UNAUTHORIZED.getMessage());
        }
        
        if (file.isEmpty()) {
            return Result.error(400, "文件不能为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error(400, "只支持图片文件");
        }
        
        if (file.getSize() > 2 * 1024 * 1024) {
            return Result.error(400, "文件大小不能超过2MB");
        }
        
        try {
            User user = userRepository.selectById(userId);
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                minioService.deleteFile(user.getAvatar());
            }
            
            String avatarUrl = minioService.uploadFile(file, "avatars");
            
            user.setAvatar(avatarUrl);
            userRepository.updateById(user);
            
            return Result.success(avatarUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(500, "上传失败：" + e.getMessage());
        }
    }
}
