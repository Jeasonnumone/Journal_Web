package cn.deru.backend.service;

import cn.deru.backend.model.Journal;
import cn.deru.backend.model.JournalCategory;
import cn.deru.backend.model.PageResult;
import cn.deru.backend.repository.JournalCategoryRepository;
import cn.deru.backend.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private JournalCategoryRepository journalCategoryRepository;

    // 获取所有期刊
    public List<Journal> getAllJournals() {
        return journalRepository.selectList(null);
    }

    // 根据分类获取期刊
    public List<Journal> getJournalsByCategory(String categoryName) {
        if ("全部".equals(categoryName)) {
            return journalRepository.selectList(null);
        }
        JournalCategory category = journalCategoryRepository.findByName(categoryName);
        if (category != null) {
            return journalRepository.findByCategoryId(category.getId());
        }
        return new ArrayList<>();
    }

    // 搜索期刊
    public List<Journal> searchJournals(String keyword) {
        return journalRepository.findByTitleContainingIgnoreCase(keyword);
    }

    // 搜索并按分类筛选期刊
    public List<Journal> searchAndFilterJournals(String keyword, String categoryName) {
        if ("全部".equals(categoryName)) {
            return journalRepository.findByTitleContainingIgnoreCase(keyword);
        }
        JournalCategory category = journalCategoryRepository.findByName(categoryName);
        if (category != null) {
            return journalRepository.findByCategoryIdAndTitleContainingIgnoreCase(category.getId(), keyword);
        }
        return new ArrayList<>();
    }

    // 根据ID获取期刊
    public Optional<Journal> getJournalById(Long id) {
        return Optional.ofNullable(journalRepository.selectById(id));
    }

    // 获取所有分类
    public List<String> getAllCategories() {
        List<JournalCategory> categories = journalCategoryRepository.selectList(null);
        List<String> categoryNames = categories.stream()
                .map(JournalCategory::getName)
                .collect(Collectors.toList());
        categoryNames.add(0, "全部");
        return categoryNames;
    }

    // ==================== 分页查询方法 ====================

    // 分页查询期刊
    public PageResult<Journal> getJournalsPageable(String keyword, String categoryName, int page, int size) {
        int offset = (page - 1) * size;
        List<Journal> records;
        long total;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryName != null && !categoryName.isEmpty() && !"全部".equals(categoryName)) {
                JournalCategory category = journalCategoryRepository.findByName(categoryName);
                if (category != null) {
                    records = journalRepository.findByCategoryIdAndTitleContainingIgnoreCasePageable(category.getId(), keyword, offset, size);
                    total = journalRepository.countByCategoryIdAndTitleContainingIgnoreCase(category.getId(), keyword);
                } else {
                    records = new ArrayList<>();
                    total = 0;
                }
            } else {
                records = journalRepository.findByTitleContainingIgnoreCasePageable(keyword, offset, size);
                total = journalRepository.countByTitleContainingIgnoreCase(keyword);
            }
        } else if (categoryName != null && !categoryName.isEmpty() && !"全部".equals(categoryName)) {
            JournalCategory category = journalCategoryRepository.findByName(categoryName);
            if (category != null) {
                records = journalRepository.findByCategoryIdPageable(category.getId(), offset, size);
                total = journalRepository.countByCategoryId(category.getId());
            } else {
                records = new ArrayList<>();
                total = 0;
            }
        } else {
            records = journalRepository.findAllPageable(offset, size);
            total = journalRepository.countAll();
        }

        return new PageResult<>(records, total, page, size);
    }
}
