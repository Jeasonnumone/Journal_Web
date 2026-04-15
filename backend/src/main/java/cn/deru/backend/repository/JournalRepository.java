package cn.deru.backend.repository;

import cn.deru.backend.model.Journal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JournalRepository extends BaseMapper<Journal> {

    // 根据分类ID查询期刊（不分页）
    @Select("SELECT * FROM journal WHERE category_id = #{categoryId}")
    List<Journal> findByCategoryId(Long categoryId);

    // 根据标题包含关键词查询期刊（不分页）
    @Select("SELECT * FROM journal WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Journal> findByTitleContainingIgnoreCase(String keyword);

    // 根据分类ID和标题包含关键词查询期刊（不分页）
    @Select("SELECT * FROM journal WHERE category_id = #{categoryId} AND title LIKE CONCAT('%', #{keyword}, '%')")
    List<Journal> findByCategoryIdAndTitleContainingIgnoreCase(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    // 分页查询所有期刊
    @Select("SELECT * FROM journal LIMIT #{offset}, #{size}")
    List<Journal> findAllPageable(@Param("offset") int offset, @Param("size") int size);

    // 统计所有期刊数量
    @Select("SELECT COUNT(*) FROM journal")
    long countAll();

    // 分页查询指定分类的期刊
    @Select("SELECT * FROM journal WHERE category_id = #{categoryId} LIMIT #{offset}, #{size}")
    List<Journal> findByCategoryIdPageable(@Param("categoryId") Long categoryId, @Param("offset") int offset, @Param("size") int size);

    // 统计指定分类的期刊数量
    @Select("SELECT COUNT(*) FROM journal WHERE category_id = #{categoryId}")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    // 分页查询标题包含关键词的期刊
    @Select("SELECT * FROM journal WHERE title LIKE CONCAT('%', #{keyword}, '%') LIMIT #{offset}, #{size}")
    List<Journal> findByTitleContainingIgnoreCasePageable(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    // 统计标题包含关键词的期刊数量
    @Select("SELECT COUNT(*) FROM journal WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    long countByTitleContainingIgnoreCase(@Param("keyword") String keyword);

    // 分页查询指定分类且标题包含关键词的期刊
    @Select("SELECT * FROM journal WHERE category_id = #{categoryId} AND title LIKE CONCAT('%', #{keyword}, '%') LIMIT #{offset}, #{size}")
    List<Journal> findByCategoryIdAndTitleContainingIgnoreCasePageable(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    // 统计指定分类且标题包含关键词的期刊数量
    @Select("SELECT COUNT(*) FROM journal WHERE category_id = #{categoryId} AND title LIKE CONCAT('%', #{keyword}, '%')")
    long countByCategoryIdAndTitleContainingIgnoreCase(@Param("categoryId") Long categoryId, @Param("keyword") String keyword);

    // 获取所有期刊分类
    @Select("SELECT DISTINCT category_id FROM journal")
    List<Long> findAllCategoryIds();
}
