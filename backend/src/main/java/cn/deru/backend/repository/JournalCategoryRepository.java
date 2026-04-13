package cn.deru.backend.repository;

import cn.deru.backend.model.JournalCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface JournalCategoryRepository extends BaseMapper<JournalCategory> {

    // 根据分类名称查询分类
    @Select("SELECT * FROM journal_category WHERE name = #{name}")
    JournalCategory findByName(String name);
}
