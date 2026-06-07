package cn.deru.backend.controller.admin;

import cn.deru.backend.model.Result;
import cn.deru.backend.repository.JournalRepository;
import cn.deru.backend.repository.UserRepository;
import cn.deru.backend.mapper.CommentMapper;
import cn.deru.backend.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@CrossOrigin(origins = "*")
public class AdminStatsController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @GetMapping
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户统计
        long totalUsers = userRepository.selectCount(null);
        long adminCount = userRepository.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.deru.backend.model.User>()
                .eq(cn.deru.backend.model.User::getRole, "ADMIN")
        );
        long supportCount = userRepository.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.deru.backend.model.User>()
                .eq(cn.deru.backend.model.User::getRole, "SUPPORT")
        );
        
        // 期刊统计
        long totalJournals = journalRepository.selectCount(null);
        
        // 评论统计
        long totalComments = commentMapper.selectCount(null);
        
        // 帖子统计
        long totalPosts = postMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<cn.deru.backend.model.Post>()
                .eq(cn.deru.backend.model.Post::getIsDeleted, 0)
        );
        
        stats.put("totalUsers", totalUsers);
        stats.put("adminCount", adminCount);
        stats.put("supportCount", supportCount);
        stats.put("totalJournals", totalJournals);
        stats.put("totalComments", totalComments);
        stats.put("totalPosts", totalPosts);
        
        return Result.success(stats);
    }
}