package cn.deru.backend.repository;

import cn.deru.backend.model.Journal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JournalRepository extends BaseMapper<Journal> {

    // 根据分类ID查询期刊
    @Select("SELECT * FROM journal WHERE category_id = #{categoryId}")
    List<Journal> findByCategoryId(Long categoryId);

    // 根据标题包含关键词查询期刊
    @Select("SELECT * FROM journal WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Journal> findByTitleContainingIgnoreCase(String keyword);

    // 根据分类ID和标题包含关键词查询期刊
    @Select("SELECT * FROM journal WHERE category_id = #{categoryId} AND title LIKE CONCAT('%', #{keyword}, '%')")
    List<Journal> findByCategoryIdAndTitleContainingIgnoreCase(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    // 获取所有期刊分类
    @Select("SELECT DISTINCT category_id FROM journal")
    List<Long> findAllCategoryIds();
}
