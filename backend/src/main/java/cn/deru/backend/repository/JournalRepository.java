package cn.deru.backend.repository;

import cn.deru.backend.model.Journal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface JournalRepository extends BaseMapper<Journal> {

    @Select("SELECT * FROM journals WHERE typeid = #{typeid}")
    List<Journal> findByTypeid(Integer typeid);

    /**
     * 模糊查询优化：使用前缀匹配 LIKE 'keyword%'
     * 前缀匹配可以利用 B+Tree 索引，避免全表扫描
     * 配合 idx_title 索引，查询效率提升显著
     */
    @Select("SELECT * FROM journals WHERE title LIKE CONCAT(#{keyword}, '%')")
    List<Journal> findByTitleStartingWith(String keyword);

    /**
     * 兼容旧方法名，内部调用优化后的前缀匹配
     */
    default List<Journal> findByTitleContainingIgnoreCase(String keyword) {
        return findByTitleStartingWith(keyword);
    }

    @Select("SELECT * FROM journals WHERE typeid = #{typeid} AND title LIKE CONCAT(#{keyword}, '%')")
    List<Journal> findByTypeidAndTitleStartingWith(@Param("typeid") Integer typeid, @Param("keyword") String keyword);

    /**
     * 兼容旧方法名
     */
    default List<Journal> findByTypeidAndTitleContainingIgnoreCase(@Param("typeid") Integer typeid, @Param("keyword") String keyword) {
        return findByTypeidAndTitleStartingWith(typeid, keyword);
    }

    @Select("SELECT * FROM journals LIMIT #{offset}, #{size}")
    List<Journal> findAllPageable(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals")
    long countAll();

    @Select("SELECT * FROM journals WHERE typeid = #{typeid} LIMIT #{offset}, #{size}")
    List<Journal> findByTypeidPageable(@Param("typeid") Integer typeid, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals WHERE typeid = #{typeid}")
    long countByTypeid(@Param("typeid") Integer typeid);

    /**
     * 分页查询优化：前缀匹配 + LIMIT
     */
    @Select("SELECT * FROM journals WHERE title LIKE CONCAT(#{keyword}, '%') LIMIT #{offset}, #{size}")
    List<Journal> findByTitleStartingWithPageable(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals WHERE title LIKE CONCAT(#{keyword}, '%')")
    long countByTitleStartingWith(@Param("keyword") String keyword);

    /**
     * 兼容旧方法名
     */
    default List<Journal> findByTitleContainingIgnoreCasePageable(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size) {
        return findByTitleStartingWithPageable(keyword, offset, size);
    }

    default long countByTitleContainingIgnoreCase(@Param("keyword") String keyword) {
        return countByTitleStartingWith(keyword);
    }

    /**
     * 分类+关键词分页查询优化
     */
    @Select("SELECT * FROM journals WHERE typeid = #{typeid} AND title LIKE CONCAT(#{keyword}, '%') LIMIT #{offset}, #{size}")
    List<Journal> findByTypeidAndTitleStartingWithPageable(@Param("typeid") Integer typeid, @Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals WHERE typeid = #{typeid} AND title LIKE CONCAT(#{keyword}, '%')")
    long countByTypeidAndTitleStartingWith(@Param("typeid") Integer typeid, @Param("keyword") String keyword);

    /**
     * 兼容旧方法名
     */
    default List<Journal> findByTypeidAndTitleContainingIgnoreCasePageable(@Param("typeid") Integer typeid, @Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size) {
        return findByTypeidAndTitleStartingWithPageable(typeid, keyword, offset, size);
    }

    default long countByTypeidAndTitleContainingIgnoreCase(@Param("typeid") Integer typeid, @Param("keyword") String keyword) {
        return countByTypeidAndTitleStartingWith(typeid, keyword);
    }

    @Select("SELECT DISTINCT typeid FROM journals")
    List<Integer> findAllTypeids();

    /**
     * 批量替换字段内容（使用 MySQL REPLACE 函数，单条 SQL 完成）
     * 注意：field 已在 Service 层做白名单校验，防止 SQL 注入
     */
    @Update("<script>" +
            "UPDATE journals SET ${field} = REPLACE(${field}, #{searchValue}, #{replaceValue}) " +
            "WHERE id IN " +
            "<foreach item='id' collection='ids' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND ${field} LIKE CONCAT('%', #{searchValue}, '%')" +
            "</script>")
    int batchReplaceField(@Param("field") String field,
                          @Param("searchValue") String searchValue,
                          @Param("replaceValue") String replaceValue,
                          @Param("ids") List<Long> ids);
}
