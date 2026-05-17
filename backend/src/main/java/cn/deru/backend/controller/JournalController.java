package cn.deru.backend.controller;

import cn.deru.backend.model.Journal;
import cn.deru.backend.model.JournalCategory;
import cn.deru.backend.model.PageResult;
import cn.deru.backend.service.JournalService;
import cn.deru.backend.model.Result;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @GetMapping
    public Result<PageResult<Journal>> getJournals(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer typeid,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        PageResult<Journal> pageResult = journalService.getJournalsPageable(keyword, typeid, page, size);
        return Result.success(pageResult);
    }

    @GetMapping("/categories")
    public Result<List<JournalCategory>> getCategories() {
        List<JournalCategory> categories = journalService.getAllCategories();
        return Result.success(categories);
    }

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
