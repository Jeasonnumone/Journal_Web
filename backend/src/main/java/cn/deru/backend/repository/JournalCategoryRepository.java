package cn.deru.backend.repository;

import cn.deru.backend.model.JournalCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface JournalCategoryRepository extends BaseMapper<JournalCategory> {

    @Select("SELECT * FROM journal_categories WHERE name = #{name}")
    JournalCategory findByName(String name);

    @Select("SELECT * FROM journal_categories WHERE typeid = #{typeid}")
    JournalCategory findByTypeid(Integer typeid);
}
