package cn.deru.backend.controller;

import cn.deru.backend.dto.ChangePasswordRequest;
import cn.deru.backend.dto.UserDTO;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.AuthService;
import cn.deru.backend.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private AuthService authService;
    
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
}
