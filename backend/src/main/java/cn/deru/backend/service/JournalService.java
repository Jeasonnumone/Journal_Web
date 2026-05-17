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

@Service
public class JournalService {

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private JournalCategoryRepository journalCategoryRepository;

    public List<Journal> getAllJournals() {
        return journalRepository.selectList(null);
    }

    public List<Journal> getJournalsByTypeid(Integer typeid) {
        if (typeid == null || typeid == 0) {
            return journalRepository.selectList(null);
        }
        return journalRepository.findByTypeid(typeid);
    }

    public List<Journal> searchJournals(String keyword) {
        return journalRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public List<Journal> searchAndFilterJournals(String keyword, Integer typeid) {
        if (typeid == null || typeid == 0) {
            return journalRepository.findByTitleContainingIgnoreCase(keyword);
        }
        return journalRepository.findByTypeidAndTitleContainingIgnoreCase(typeid, keyword);
    }

    public Optional<Journal> getJournalById(Long id) {
        return Optional.ofNullable(journalRepository.selectById(id));
    }

    public List<JournalCategory> getAllCategories() {
        return journalCategoryRepository.selectList(null);
    }

    public PageResult<Journal> getJournalsPageable(String keyword, Integer typeid, int page, int size) {
        int offset = (page - 1) * size;
        List<Journal> records;
        long total;

        if (keyword != null && !keyword.isEmpty()) {
            if (typeid != null && typeid > 0) {
                records = journalRepository.findByTypeidAndTitleContainingIgnoreCasePageable(typeid, keyword, offset, size);
                total = journalRepository.countByTypeidAndTitleContainingIgnoreCase(typeid, keyword);
            } else {
                records = journalRepository.findByTitleContainingIgnoreCasePageable(keyword, offset, size);
                total = journalRepository.countByTitleContainingIgnoreCase(keyword);
            }
        } else if (typeid != null && typeid > 0) {
            records = journalRepository.findByTypeidPageable(typeid, offset, size);
            total = journalRepository.countByTypeid(typeid);
        } else {
            records = journalRepository.findAllPageable(offset, size);
            total = journalRepository.countAll();
        }

        return new PageResult<>(records, total, page, size);
    }
}
