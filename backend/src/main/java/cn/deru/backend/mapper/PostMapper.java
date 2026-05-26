package cn.deru.backend.mapper;

import cn.deru.backend.dto.PostDTO;
import cn.deru.backend.model.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    
    /**
     * 分页查询最新帖子
     */
    @Select("SELECT p.*, u.username, u.avatar " +
            "FROM posts p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    com.baomidou.mybatisplus.core.metadata.IPage<PostDTO> selectRecentPosts(Page<PostDTO> page);
    
    /**
     * 查询帖子详情（关联用户名）
     */
    @Select("SELECT p.*, u.username, u.avatar " +
            "FROM posts p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.id = #{id} " +
            "AND p.is_deleted = 0")
    PostDTO selectPostDetail(@Param("id") Long id);
    
    /**
     * 增加浏览量
     */
    @Update("UPDATE posts SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    /**
     * 更新浏览量为指定值（用于定时任务同步 Redis 总量数据）
     */
    @Update("UPDATE posts SET view_count = #{count} WHERE id = #{id}")
    void updateViewCount(@Param("id") Long id, @Param("count") Integer count);
    
    /**
     * 查询用户的帖子
     */
    @Select("SELECT p.*, u.username, u.avatar " +
            "FROM posts p " +
            "LEFT JOIN user u ON p.user_id = u.id " +
            "WHERE p.user_id = #{userId} " +
            "AND p.is_deleted = 0 " +
            "ORDER BY p.create_time DESC")
    com.baomidou.mybatisplus.core.metadata.IPage<PostDTO> selectUserPosts(Page<PostDTO> page, @Param("userId") Long userId);
}
