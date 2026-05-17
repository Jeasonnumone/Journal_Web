package cn.deru.backend.repository;

import cn.deru.backend.model.Journal;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface JournalRepository extends BaseMapper<Journal> {

    @Select("SELECT * FROM journals WHERE typeid = #{typeid}")
    List<Journal> findByTypeid(Integer typeid);

    @Select("SELECT * FROM journals WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    List<Journal> findByTitleContainingIgnoreCase(String keyword);

    @Select("SELECT * FROM journals WHERE typeid = #{typeid} AND title LIKE CONCAT('%', #{keyword}, '%')")
    List<Journal> findByTypeidAndTitleContainingIgnoreCase(@Param("typeid") Integer typeid, @Param("keyword") String keyword);

    @Select("SELECT * FROM journals LIMIT #{offset}, #{size}")
    List<Journal> findAllPageable(@Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals")
    long countAll();

    @Select("SELECT * FROM journals WHERE typeid = #{typeid} LIMIT #{offset}, #{size}")
    List<Journal> findByTypeidPageable(@Param("typeid") Integer typeid, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals WHERE typeid = #{typeid}")
    long countByTypeid(@Param("typeid") Integer typeid);

    @Select("SELECT * FROM journals WHERE title LIKE CONCAT('%', #{keyword}, '%') LIMIT #{offset}, #{size}")
    List<Journal> findByTitleContainingIgnoreCasePageable(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals WHERE title LIKE CONCAT('%', #{keyword}, '%')")
    long countByTitleContainingIgnoreCase(@Param("keyword") String keyword);

    @Select("SELECT * FROM journals WHERE typeid = #{typeid} AND title LIKE CONCAT('%', #{keyword}, '%') LIMIT #{offset}, #{size}")
    List<Journal> findByTypeidAndTitleContainingIgnoreCasePageable(@Param("typeid") Integer typeid, @Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);

    @Select("SELECT COUNT(*) FROM journals WHERE typeid = #{typeid} AND title LIKE CONCAT('%', #{keyword}, '%')")
    long countByTypeidAndTitleContainingIgnoreCase(@Param("typeid") Integer typeid, @Param("keyword") String keyword);

    @Select("SELECT DISTINCT typeid FROM journals")
    List<Integer> findAllTypeids();
}
