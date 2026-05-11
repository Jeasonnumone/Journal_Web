package cn.deru.backend.task;

import cn.deru.backend.mapper.PostMapper;
import cn.deru.backend.service.PostViewCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class PostViewCountSyncTask {
    
    private static final Logger logger = LoggerFactory.getLogger(PostViewCountSyncTask.class);
    
    @Autowired
    private PostViewCacheService postViewCacheService;
    
    @Autowired
    private PostMapper postMapper;
    
    @Value("${task.view-count.sync-enabled:true}")
    private boolean syncEnabled;
    
    @Value("${task.view-count.batch-size:100}")
    private int batchSize;
    
    /**
     * 定时同步 Redis 中的浏览量到数据库
     * 每 5 分钟执行一次（将 Redis 总量覆盖到数据库）
     */
    @Scheduled(fixedRate = 300000)
    public void syncViewCountToDatabase() {
        if (!syncEnabled) {
            logger.debug("浏览量同步任务已禁用");
            return;
        }
        
        try {
            logger.info("开始同步帖子浏览量到数据库");
            
            // 获取所有 Redis 中的浏览量数据
            Map<String, String> viewCountData = postViewCacheService.getAllViewCountData();
            
            if (viewCountData == null || viewCountData.isEmpty()) {
                logger.debug("没有需要同步的浏览量数据");
                return;
            }
            
            int updatedCount = 0;
            
            // 批量更新数据库
            for (Map.Entry<String, String> entry : viewCountData.entrySet()) {
                String key = entry.getKey();
                String viewCountStr = entry.getValue();
                
                if (viewCountStr == null || viewCountStr.isEmpty()) {
                    continue;
                }
                
                // 从 key 中提取 postId: post:view:{postId}
                Long postId = extractPostIdFromKey(key);
                if (postId == null) {
                    continue;
                }
                
                int viewCount = Integer.parseInt(viewCountStr);
                
                if (viewCount > 0) {
                    // 更新数据库，直接覆盖为总量
                    postMapper.updateViewCount(postId, viewCount);
                    
                    updatedCount++;
                }
            }
            
            logger.info("浏览量同步完成，共同步 {} 个帖子", updatedCount);
            
        } catch (Exception e) {
            logger.error("同步浏览量失败", e);
        }
    }
    
    /**
     * 从 Redis key 中提取帖子 ID
     */
    private Long extractPostIdFromKey(String key) {
        try {
            String[] parts = key.split(":");
            if (parts.length >= 3) {
                return Long.parseLong(parts[2]);
            }
        } catch (Exception e) {
            logger.warn("从 key 提取 postId 失败：{}", key, e);
        }
        return null;
    }
    
    /**
     * 手动触发同步（用于测试或紧急情况）
     */
    public void manualSync() {
        logger.info("手动触发浏览量同步");
        syncViewCountToDatabase();
    }
}
