-- 创建评论表
CREATE TABLE IF NOT EXISTS comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论 ID',
    journal_id BIGINT NOT NULL COMMENT '期刊 ID',
    user_id BIGINT NOT NULL COMMENT '评论者 ID',
    
    -- 层级关系
    root_id BIGINT DEFAULT NULL COMMENT '根评论 ID（一级评论等于自身 ID）',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论 ID（一级评论为 NULL）',
    reply_to_user_id BIGINT DEFAULT NULL COMMENT '被回复的用户 ID',
    
    -- 评论内容
    content TEXT NOT NULL COMMENT '评论内容',
    
    -- 删除标记（0-未删除，1-已删除）
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    
    -- 统计字段（冗余设计，提升查询性能）
    reply_count INT DEFAULT 0 COMMENT '回复数量（缓存）',
    
    -- 时间戳
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引优化
    INDEX idx_journal_root (journal_id, root_id, create_time),
    INDEX idx_parent (parent_id),
    INDEX idx_reply_to_user (reply_to_user_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='期刊评论表';

-- 测试数据（可选）
-- INSERT INTO comments (journal_id, user_id, root_id, parent_id, reply_to_user_id, content, is_deleted, reply_count) VALUES
-- (1, 1, 1, NULL, NULL, '这本期刊真的很不错！', 0, 0),
-- (1, 2, 1, 1, 1, '同意楼主！', 0, 0),
-- (1, 3, 1, 1, 2, '确实如此', 0, 0),
-- (1, 4, 4, NULL, NULL, '我觉得一般般吧', 0, 0);
