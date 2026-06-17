package cn.deru.backend.service.admin;

import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminUserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 分页查询用户（升序）
     */
    public IPage<User> getUsers(Integer page, Integer pageSize, String keyword) {
        Page<User> userPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or()
                   .like(User::getEmail, keyword);
        }
        
        wrapper.orderByAsc(User::getCreateTime);
        
        return userRepository.selectPage(userPage, wrapper);
    }

    /**
     * 更新用户角色
     */
    public void updateUserRole(Long id, String role) {
        User user = userRepository.selectById(id);
        if (user == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        if (!role.equals("USER") && !role.equals("SUPPORT") && !role.equals("ADMIN")) {
            throw new BusinessException(BusinessCode.BAD_REQUEST, "无效的角色");
        }
        user.setRole(role);
        user.setUpdateTime(LocalDateTime.now());
        userRepository.updateById(user);
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long id) {
        User user = userRepository.selectById(id);
        if (user == null) {
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "用户不存在");
        }
        userRepository.deleteById(id);
    }
}