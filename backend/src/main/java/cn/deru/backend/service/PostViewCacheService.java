package cn.deru.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class PostViewCacheService {
    
    private static final String VIEW_COUNT_KEY_PREFIX = "post:view:";
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    /**
     * 增加帖子浏览量（直接存储总量）
     */
    public void incrementViewCount(Long postId) {
        String key = VIEW_COUNT_KEY_PREFIX + postId;
        Long newCount = redisTemplate.opsForValue().increment(key);
        
        // 如果是第一次访问，设置过期时间（7 天自动清理）
        if (newCount == 1) {
            redisTemplate.expire(key, 7, TimeUnit.DAYS);
        }
    }
    
    /**
     * 获取帖子的浏览量（从 Redis，返回总量）
     */
    public Long getViewCount(Long postId) {
        String key = VIEW_COUNT_KEY_PREFIX + postId;
        String value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value) : 0;
    }
    
    /**
     * 批量获取多个帖子的浏览量（从 Redis）
     */
    public Map<Long, Long> getMultiViewCount(Set<Long> postIds) {
        Map<Long, Long> result = new HashMap<>();
        for (Long postId : postIds) {
            String key = VIEW_COUNT_KEY_PREFIX + postId;
            String value = redisTemplate.opsForValue().get(key);
            result.put(postId, value != null ? Long.parseLong(value) : 0);
        }
        return result;
    }
    
    /**
     * 获取所有帖子的浏览量数据
     */
    public Map<String, String> getAllViewCountData() {
        String pattern = VIEW_COUNT_KEY_PREFIX + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        
        if (keys == null || keys.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, String> result = new HashMap<>();
        for (String key : keys) {
            String value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }
    
    /**
     * 删除帖子的浏览量缓存
     */
    public void removeViewCount(Long postId) {
        String key = VIEW_COUNT_KEY_PREFIX + postId;
        redisTemplate.delete(key);
    }
}
