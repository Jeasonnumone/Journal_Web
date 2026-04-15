package cn.deru.backend.controller;

import cn.deru.backend.model.Journal;
import cn.deru.backend.model.PageResult;
import cn.deru.backend.service.JournalService;
import cn.deru.backend.model.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/journals")
@CrossOrigin(origins = "*")
public class JournalController {
    private final JournalService journalService;

    public JournalController(JournalService journalService) {
        this.journalService = journalService;
    }

    // 获取所有期刊（分页）
    @GetMapping
    public Result<PageResult<Journal>> getJournals(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        PageResult<Journal> pageResult = journalService.getJournalsPageable(keyword, category, page, size);
        return Result.success(pageResult);
    }

    // 获取期刊分类
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        List<String> categories = journalService.getAllCategories();
        return Result.success(categories);
    }

    // 根据ID获取期刊
    @GetMapping("/{id}")
    public Result<Journal> getJournalById(@PathVariable Long id) {
        Optional<Journal> journalOptional = journalService.getJournalById(id);
        if (journalOptional.isPresent()) {
            return Result.success(journalOptional.get());
        } else {
            return Result.error(404, "期刊不存在");
        }
    }
}
