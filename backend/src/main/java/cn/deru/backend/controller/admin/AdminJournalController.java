package cn.deru.backend.controller.admin;

import cn.deru.backend.model.Journal;
import cn.deru.backend.model.JournalCategory;
import cn.deru.backend.model.PageResult;
import cn.deru.backend.model.Result;
import cn.deru.backend.repository.JournalCategoryRepository;
import cn.deru.backend.repository.JournalRepository;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/admin/journals")
@CrossOrigin(origins = "*")
public class AdminJournalController {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private JournalCategoryRepository journalCategoryRepository;

    @GetMapping
    public Result<IPage<Journal>> getJournals(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        Page<Journal> journalPage = new Page<>(page, pageSize);
        
        if (keyword != null && !keyword.isEmpty()) {
            Page<Journal> result = journalRepository.selectPage(journalPage,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Journal>()
                    .like(Journal::getTitle, keyword)
                    .orderByDesc(Journal::getCreatedAt)
            );
            return Result.success(result);
        }
        
        Page<Journal> result = journalRepository.selectPage(journalPage,
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Journal>()
                .orderByDesc(Journal::getCreatedAt)
        );
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> createJournal(@RequestBody Journal journal) {
        journal.setCreatedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        journalRepository.insert(journal);
        return Result.success(null);
    }

    @PutMapping("/{id}")
    public Result<Void> updateJournal(@PathVariable Long id, @RequestBody Journal journal) {
        Journal existing = journalRepository.selectById(id);
        if (existing == null) {
            return Result.error(4040, "期刊不存在");
        }
        journal.setId(id);
        journalRepository.updateById(journal);
        return Result.success(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteJournal(@PathVariable Long id) {
        Journal journal = journalRepository.selectById(id);
        if (journal == null) {
            return Result.error(4040, "期刊不存在");
        }
        journalRepository.deleteById(id);
        return Result.success(null);
    }

    @GetMapping("/categories")
    public Result<List<JournalCategory>> getCategories() {
        List<JournalCategory> categories = journalCategoryRepository.selectList(null);
        return Result.success(categories);
    }
}