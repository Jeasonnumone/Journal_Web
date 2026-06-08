package cn.deru.backend.service.admin;

import cn.deru.backend.mapper.CommentMapper;
import cn.deru.backend.mapper.PostMapper;
import cn.deru.backend.model.Comment;
import cn.deru.backend.model.Post;
import cn.deru.backend.model.User;
import cn.deru.backend.repository.JournalRepository;
import cn.deru.backend.repository.UserRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdminStatsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    /**
     * 获取统计数据
     */
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long totalUsers = userRepository.selectCount(null);
        long adminCount = userRepository.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getRole, "ADMIN")
        );
        long supportCount = userRepository.selectCount(
            new LambdaQueryWrapper<User>().eq(User::getRole, "SUPPORT")
        );
        
        // 期刊统计
        long totalJournals = journalRepository.selectCount(null);
        
        // 评论统计
        long totalComments = commentMapper.selectCount(null);
        
        // 帖子统计
        long totalPosts = postMapper.selectCount(
            new LambdaQueryWrapper<Post>().eq(Post::getIsDeleted, 0)
        );
        
        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("supportCount", supportCount);
        stats.put("totalJournals", totalJournals);
        stats.put("totalComments", totalComments);
        stats.put("totalPosts", totalPosts);
        
        return stats;
    }
}