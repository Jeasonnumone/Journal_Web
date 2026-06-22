package cn.deru.backend.controller.admin;

import cn.deru.backend.dto.BatchReplaceRequest;
import cn.deru.backend.model.Journal;
import cn.deru.backend.model.JournalCategory;
import cn.deru.backend.model.Result;
import cn.deru.backend.repository.JournalCategoryRepository;
import cn.deru.backend.service.admin.AdminJournalService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/journals")
@CrossOrigin(origins = "*")
public class AdminJournalController {

    @Autowired
    private AdminJournalService adminJournalService;

    @Autowired
    private JournalCategoryRepository journalCategoryRepository;

    @GetMapping
    public Result<IPage<Journal>> getJournals(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword
    ) {
        IPage<Journal> result = adminJournalService.getJournals(page, pageSize, keyword);
        return Result.success(result);
    }

    @PostMapping
    public Result<Void> createJournal(@RequestBody Journal journal) {
        try {
            adminJournalService.createJournal(journal);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(4000, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Void> updateJournal(@PathVariable Long id, @RequestBody Journal journal) {
        try {
            adminJournalService.updateJournal(id, journal);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(4040, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteJournal(@PathVariable Long id) {
        try {
            adminJournalService.deleteJournal(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(4040, e.getMessage());
        }
    }

    @PostMapping("/batch-replace")
    public Result<Integer> batchReplace(@RequestBody BatchReplaceRequest request) {
        try {
            int count = adminJournalService.batchReplace(request);
            return Result.success(count);
        } catch (RuntimeException e) {
            return Result.error(4000, e.getMessage());
        }
    }

    @GetMapping("/categories")
    public Result<List<JournalCategory>> getCategories() {
        List<JournalCategory> categories = journalCategoryRepository.selectList(null);
        return Result.success(categories);
    }
}