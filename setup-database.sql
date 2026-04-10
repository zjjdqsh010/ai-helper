-- 创建Token使用记录表的SQL脚本
-- 请在PostgreSQL中执行此脚本

-- Token使用记录表
CREATE TABLE IF NOT EXISTS token_usage_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(50) NOT NULL,
    token_id VARCHAR(255) NOT NULL,
    api_endpoint VARCHAR(255) NOT NULL,
    request_method VARCHAR(10) NOT NULL,
    request_params TEXT,
    request_body TEXT,
    response_status INTEGER,
    response_time INTEGER,
    ip_address VARCHAR(45),
    user_agent TEXT,
    usage_purpose VARCHAR(100),
    cost_tokens INTEGER DEFAULT 0,
    cost_amount DECIMAL(10,4) DEFAULT 0.0000,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    error_message TEXT
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_token_usage_user_id ON token_usage_log(user_id);
CREATE INDEX IF NOT EXISTS idx_token_usage_created_at ON token_usage_log(created_at);
CREATE INDEX IF NOT EXISTS idx_token_usage_purpose ON token_usage_log(usage_purpose);
CREATE INDEX IF NOT EXISTS idx_token_usage_endpoint ON token_usage_log(api_endpoint);

-- Token使用统计表
CREATE TABLE IF NOT EXISTS token_usage_stats (
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
    CONSTRAINT uk_user_date_purpose UNIQUE (user_id, date, usage_purpose)
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_usage_stats_user_date ON token_usage_stats(user_id, date);
CREATE INDEX IF NOT EXISTS idx_usage_stats_purpose ON token_usage_stats(usage_purpose);

-- 插入一些测试数据
INSERT INTO token_usage_log (
    user_id, username, token_id, api_endpoint, request_method,
    usage_purpose, response_status, response_time, cost_tokens, cost_amount
) VALUES
(1, 'testuser', 'jwt-token-123', '/api/ai-helper/chat', 'POST', 'chat', 200, 2500, 150, 0.015),
(1, 'testuser', 'jwt-token-123', '/api/ai-helper/code-review', 'POST', 'code_review', 200, 3200, 280, 0.028),
(1, 'testuser', 'jwt-token-123', '/api/ai-helper/debug', 'POST', 'debug', 200, 4100, 320, 0.032);

SELECT 'Token使用记录表创建成功！' as message;