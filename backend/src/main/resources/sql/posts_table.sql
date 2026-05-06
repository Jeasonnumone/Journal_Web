-- 创建帖子表
CREATE TABLE IF NOT EXISTS posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '帖子 ID',
    user_id BIGINT NOT NULL COMMENT '作者 ID',
    title VARCHAR(200) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '帖子内容',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_create (user_id, create_time),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户帖子表';
