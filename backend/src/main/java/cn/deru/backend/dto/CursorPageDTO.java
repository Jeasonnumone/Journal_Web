package cn.deru.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * 游标分页响应 DTO
 * 用于评论回复、聊天记录等不需要跳页的场景
 */
@Data
public class CursorPageDTO<T> {
    
    /**
     * 数据列表
     */
    private List<T> data;
    
    /**
     * 下一页游标（最后一条数据的 ID）
     * null 表示没有更多数据
     */
    private Long nextCursor;
    
    /**
     * 是否还有更多数据
     */
    private Boolean hasMore;
    
    /**
     * 当前页数据量
     */
    private Integer size;
    
    /**
     * 总数（可选，用于显示总数）
     */
    private Integer total;
    
    /**
     * 创建游标分页响应
     */
    public static <T> CursorPageDTO<T> of(List<T> data, Long nextCursor, boolean hasMore) {
        CursorPageDTO<T> result = new CursorPageDTO<>();
        result.setData(data);
        result.setNextCursor(nextCursor);
        result.setHasMore(hasMore);
        result.setSize(data.size());
        return result;
    }
    
    /**
     * 创建带总数的游标分页响应
     */
    public static <T> CursorPageDTO<T> of(List<T> data, Long nextCursor, boolean hasMore, Integer total) {
        CursorPageDTO<T> result = of(data, nextCursor, hasMore);
        result.setTotal(total);
        return result;
    }
}