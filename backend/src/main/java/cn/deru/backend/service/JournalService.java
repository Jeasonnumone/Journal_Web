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

    public List<Journal> getAllJournals() {
        return journalRepository.selectList(null);
    }

    public List<Journal> getJournalsByCategory(String categoryName) {
        if ("全部".equals(categoryName)) {
            return journalRepository.selectList(null);
        }
        JournalCategory category = journalCategoryRepository.findByName(categoryName);
        if (category != null) {
            return journalRepository.findByTypeid(category.getTypeid());
        }
        return new ArrayList<>();
    }

    public List<Journal> searchJournals(String keyword) {
        return journalRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Journal> searchAndFilterJournals(String keyword, String categoryName) {
        if ("全部".equals(categoryName)) {
            return journalRepository.findByTitleContainingIgnoreCase(keyword);
        }
        JournalCategory category = journalCategoryRepository.findByName(categoryName);
        if (category != null) {
            return journalRepository.findByTypeidAndTitleContainingIgnoreCase(category.getTypeid(), keyword);
        }
        return new ArrayList<>();
    }

    public Optional<Journal> getJournalById(Long id) {
        return Optional.ofNullable(journalRepository.selectById(id));
    }

    public List<String> getAllCategories() {
        List<JournalCategory> categories = journalCategoryRepository.selectList(null);
        List<String> categoryNames = categories.stream()
                .map(JournalCategory::getName)
                .collect(Collectors.toList());
        categoryNames.add(0, "全部");
        return categoryNames;
    }

    public PageResult<Journal> getJournalsPageable(String keyword, String categoryName, int page, int size) {
        int offset = (page - 1) * size;
        List<Journal> records;
        long total;

        if (keyword != null && !keyword.isEmpty()) {
            if (categoryName != null && !categoryName.isEmpty() && !"全部".equals(categoryName)) {
                JournalCategory category = journalCategoryRepository.findByName(categoryName);
                if (category != null) {
                    records = journalRepository.findByTypeidAndTitleContainingIgnoreCasePageable(category.getTypeid(), keyword, offset, size);
                    total = journalRepository.countByTypeidAndTitleContainingIgnoreCase(category.getTypeid(), keyword);
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
                records = journalRepository.findByTypeidPageable(category.getTypeid(), offset, size);
                total = journalRepository.countByTypeid(category.getTypeid());
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
