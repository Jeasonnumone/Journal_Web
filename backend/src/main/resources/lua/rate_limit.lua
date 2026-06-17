-- 滑动窗口限流算法
-- KEYS[1]: 限流 key
-- ARGV[1]: 窗口大小（秒）
-- ARGV[2]: 最大请求数
-- ARGV[3]: 当前时间戳（毫秒）
-- 返回: 1 表示允许访问，0 表示被限流

local key = KEYS[1]
local window = tonumber(ARGV[1])
local maxCount = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local windowStart = now - window * 1000

-- 清除窗口外的记录
redis.call('ZREMRANGEBYSCORE', key, 0, windowStart)

-- 获取当前窗口内的请求数
local count = redis.call('ZCARD', key)

-- 如果请求数超过限制，返回 0
if count >= maxCount then
    return 0
end

-- 添加当前请求记录
redis.call('ZADD', key, now, now)

-- 设置过期时间
redis.call('EXPIRE', key, window)

return 1