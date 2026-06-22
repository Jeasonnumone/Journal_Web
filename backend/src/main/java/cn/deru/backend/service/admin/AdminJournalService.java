package cn.deru.backend.service.admin;

import cn.deru.backend.dto.BatchReplaceRequest;
import cn.deru.backend.exception.BusinessCode;
import cn.deru.backend.exception.BusinessException;
import cn.deru.backend.model.Journal;
import cn.deru.backend.repository.JournalRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

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
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "期刊不存在");
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
            throw new BusinessException(BusinessCode.RESOURCE_NOT_FOUND, "期刊不存在");
        }
        journalRepository.deleteById(id);
    }

    /**
     * 批量替换期刊字段内容
     * 使用 MySQL REPLACE 函数，单条 SQL 完成批量替换
     */
    public int batchReplace(BatchReplaceRequest request) {
        List<Long> ids = request.getIds();
        String field = request.getField();
        String searchValue = request.getSearchValue();
        String replaceValue = request.getReplaceValue();

        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(BusinessCode.BAD_REQUEST, "请选择要替换的期刊");
        }
        if (field == null || field.isEmpty()) {
            throw new BusinessException(BusinessCode.BAD_REQUEST, "请选择替换字段");
        }
        if (searchValue == null || searchValue.isEmpty()) {
            throw new BusinessException(BusinessCode.BAD_REQUEST, "请输入查找内容");
        }

        // 允许替换的字段白名单（防止 SQL 注入，因为 field 使用了 ${} 插值）
        List<String> allowedFields = Arrays.asList("label", "organizer", "department", "introduction");
        if (!allowedFields.contains(field)) {
            throw new BusinessException(BusinessCode.BAD_REQUEST, "不允许替换该字段");
        }

        return journalRepository.batchReplaceField(field, searchValue, replaceValue, ids);
    }
}