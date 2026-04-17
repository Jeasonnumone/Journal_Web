package cn.deru.backend.util;

public class UserContext {
    
    private static final ThreadLocal<Long> userId = new ThreadLocal<>();
    private static final ThreadLocal<String> username = new ThreadLocal<>();
    private static final ThreadLocal<String> role = new ThreadLocal<>();
    
    // 设置用户信息
    public static void setUserInfo(Long userId, String username, String role) {
        UserContext.userId.set(userId);
        UserContext.username.set(username);
        UserContext.role.set(role);
    }
    
    // 获取userId
    public static Long getUserId() {
        return userId.get();
    }
    
    // 获取username
    public static String getUsername() {
        return username.get();
    }
    
    // 获取role
    public static String getRole() {
        return role.get();
    }
    
    // 清除用户信息
    public static void clear() {
        userId.remove();
        username.remove();
        role.remove();
    }
}