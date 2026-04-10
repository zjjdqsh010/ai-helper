-- Token使用记录表
CREATE TABLE token_usage_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    token_id VARCHAR(255) NOT NULL,
    api_endpoint VARCHAR(255) NOT NULL,
    request_method VARCHAR(10) NOT NULL,
    request_params TEXT,
    request_body TEXT,
    response_status INTEGER,
    response_time INTEGER, -- 响应时间(毫秒)
    ip_address VARCHAR(45),
    user_agent TEXT,
    usage_purpose VARCHAR(100), -- 使用目的: chat, code_review, debug, optimize, explain
    cost_tokens INTEGER DEFAULT 0, -- 消耗的token数量
    cost_amount DECIMAL(10,4) DEFAULT 0.0000, -- 费用(元)
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT,

    -- 外键约束
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建索引以提高查询性能
CREATE INDEX idx_token_usage_user_id ON token_usage_log(user_id);
CREATE INDEX idx_token_usage_created_at ON token_usage_log(created_at);
CREATE INDEX idx_token_usage_purpose ON token_usage_log(usage_purpose);
CREATE INDEX idx_token_usage_endpoint ON token_usage_log(api_endpoint);

-- Token使用统计表（用于快速统计）
CREATE TABLE token_usage_stats (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    date DATE NOT NULL,
    usage_purpose VARCHAR(100) NOT NULL,
    request_count INTEGER DEFAULT 0,
    total_tokens INTEGER DEFAULT 0,
    total_cost DECIMAL(10,4) DEFAULT 0.0000,
    avg_response_time INTEGER DEFAULT 0,
    success_rate DECIMAL(5,2) DEFAULT 0.00,

    -- 唯一约束，确保每天每个用户的每种用途只有一条记录
    CONSTRAINT uk_user_date_purpose UNIQUE (user_id, date, usage_purpose),
    CONSTRAINT fk_stats_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建索引
CREATE INDEX idx_usage_stats_user_date ON token_usage_stats(user_id, date);
CREATE INDEX idx_usage_stats_purpose ON token_usage_stats(usage_purpose);

-- 插入示例数据
INSERT INTO token_usage_log (
    user_id, username, token_id, api_endpoint, request_method,
    usage_purpose, response_status, response_time, cost_tokens, cost_amount
) VALUES
(1, 'testuser', 'jwt-token-123', '/api/ai-helper/chat', 'POST', 'chat', 200, 2500, 150, 0.015),
(1, 'testuser', 'jwt-token-123', '/api/ai-helper/code-review', 'POST', 'code_review', 200, 3200, 280, 0.028),
(2, 'testuser2', 'jwt-token-456', '/api/ai-helper/debug', 'POST', 'debug', 200, 4100, 320, 0.032),
(1, 'testuser', 'jwt-token-123', '/api/ai-helper/optimize', 'POST', 'optimize', 200, 2800, 180, 0.018),
(2, 'testuser2', 'jwt-token-456', '/api/ai-helper/explain', 'POST', 'explain', 200, 2200, 120, 0.012);

-- 创建每日统计的物化视图（可选，用于快速查询）
CREATE MATERIALIZED VIEW daily_usage_summary AS
SELECT
    DATE(created_at) as usage_date,
    user_id,
    username,
    usage_purpose,
    COUNT(*) as request_count,
    SUM(cost_tokens) as total_tokens,
    SUM(cost_amount) as total_cost,
    AVG(response_time) as avg_response_time,
    ROUND(100.0 * SUM(CASE WHEN response_status = 200 THEN 1 ELSE 0 END) / COUNT(*), 2) as success_rate
FROM token_usage_log
WHERE created_at >= CURRENT_DATE - INTERVAL '30 days'
GROUP BY DATE(created_at), user_id, username, usage_purpose;

-- 创建索引以加速物化视图查询
CREATE INDEX idx_daily_summary_date ON daily_usage_summary(usage_date);
CREATE INDEX idx_daily_summary_user ON daily_usage_summary(user_id);

-- 刷新物化视图的函数
CREATE OR REPLACE FUNCTION refresh_daily_usage_summary()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY daily_usage_summary;
END;
$$ LANGUAGE plpgsql;

-- 创建自动刷新的定时任务（需要安装pg_cron扩展）
-- SELECT cron.schedule('refresh-daily-summary', '0 2 * * *', 'SELECT refresh_daily_usage_summary()');