package cn.deru.backend.controller;

import cn.deru.backend.model.Journal;
import cn.deru.backend.model.Result;
import cn.deru.backend.service.JournalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/journals")
@Api(tags = "期刊管理")
public class JournalController {

    @Autowired
    private JournalService journalService;

    // 获取所有期刊
    @GetMapping
    @ApiOperation(value = "获取期刊列表", notes = "根据关键词和分类筛选期刊")
    public Result<List<Journal>> getAllJournals(
            @ApiParam(name = "keyword", value = "搜索关键词", required = false) @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @ApiParam(name = "category", value = "分类筛选", required = false, defaultValue = "全部") @RequestParam(value = "category", defaultValue = "全部") String category) {
        List<Journal> journals;
        if (!keyword.isEmpty()) {
            journals = journalService.searchAndFilterJournals(keyword, category);
        } else {
            journals = journalService.getJournalsByCategory(category);
        }
        return Result.success(journals);
    }

    // 获取期刊详情
    @GetMapping("/{id}")
    @ApiOperation(value = "获取期刊详情", notes = "根据ID获取期刊详细信息")
    public Result<Journal> getJournalById(@ApiParam(name = "id", value = "期刊ID", required = true) @PathVariable Long id) {
        return journalService.getJournalById(id)
                .map(Result::success)
                .orElse(Result.error(404, "期刊不存在"));
    }

    // 获取所有分类
    @GetMapping("/categories")
    @ApiOperation(value = "获取所有分类", notes = "获取系统中所有的期刊分类")
    public Result<List<String>> getAllCategories() {
        List<String> categories = journalService.getAllCategories();
        return Result.success(categories);
    }
}
