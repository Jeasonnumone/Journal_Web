package cn.deru.backend.controller.admin;

import cn.deru.backend.model.Result;
import cn.deru.backend.service.admin.AdminStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
@CrossOrigin(origins = "*")
public class AdminStatsController {

    @Autowired
    private AdminStatsService adminStatsService;

    @GetMapping
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = adminStatsService.getStats();
        return Result.success(stats);
    }
}