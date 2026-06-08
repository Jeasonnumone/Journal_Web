package cn.deru.backend.service.admin;

import cn.deru.backend.model.Journal;
import cn.deru.backend.repository.JournalRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
public class AdminJournalService {

    @Autowired
    private JournalRepository journalRepository;

    /**
     * 分页查询期刊（升序）
     */
    public IPage<Journal> getJournals(Integer page, Integer pageSize, String keyword) {
        Page<Journal> journalPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Journal> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Journal::getTitle, keyword);
        }
        
        wrapper.orderByAsc(Journal::getCreatedAt);
        
        return journalRepository.selectPage(journalPage, wrapper);
    }

    /**
     * 创建期刊
     */
    public void createJournal(Journal journal) {
        journal.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        journalRepository.insert(journal);
    }

    /**
     * 更新期刊
     */
    public void updateJournal(Long id, Journal journal) {
        Journal existing = journalRepository.selectById(id);
        if (existing == null) {
            throw new RuntimeException("期刊不存在");
        }
        journal.setId(id);
        journalRepository.updateById(journal);
    }

    /**
     * 删除期刊
     */
    public void deleteJournal(Long id) {
        Journal journal = journalRepository.selectById(id);
        if (journal == null) {
            throw new RuntimeException("期刊不存在");
        }
        journalRepository.deleteById(id);
    }
}