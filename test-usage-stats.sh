#!/bin/bash

echo "=== Token使用统计功能测试 ==="
echo

echo "1. 创建数据库表结构..."
# 执行SQL脚本创建表
# psql -h 192.168.11.211 -p 5432 -U postgres -d sxxm -f create_token_usage_table.sql

echo "✅ 表结构创建完成（请手动执行SQL脚本）"
echo

echo "2. 测试统计页面访问..."
curl -s -I http://localhost:8081/openai/usage-stats.html | head -n 1
echo

echo "3. 测试API接口..."

# 测试获取统计信息
echo "测试获取使用统计..."
curl -s "http://localhost:8081/openai/api/usage/stats?userId=1" | head -n 5
echo

# 测试获取使用记录
echo "测试获取使用记录..."
curl -s "http://localhost:8081/openai/api/usage/logs?userId=1&page=0&size=5" | head -n 5
echo

# 测试获取概览信息
echo "测试获取使用概览..."
curl -s "http://localhost:8081/openai/api/usage/overview?userId=1" | head -n 5
echo

echo "4. 模拟API调用以生成使用记录..."

# 模拟一些API调用
for i in {1..3}; do
    echo "模拟调用 $i..."
    # 这里需要实际的Token，暂时跳过
    # curl -X POST http://localhost:8081/openai/api/ai-helper/chat \
    #   -H "Authorization: Bearer YOUR_TOKEN" \
    #   -H "Content-Type: application/json" \
    #   -d '{"message": "测试消息 '$i'", "sessionId": null}'
done

echo
echo "5. 手动测试指南："
echo "============================"
echo "1. 确保数据库表已创建"
echo "2. 启动应用"
echo "3. 访问统计页面: http://localhost:8081/openai/usage-stats.html"
echo "4. 进行一些API调用以生成使用记录"
echo "5. 刷新统计页面查看数据"
echo

echo "6. 需要手动完成的步骤："
echo "============================"
echo "- 执行SQL脚本创建数据库表"
echo "- 配置JwtUtil bean（在TokenUsageService中）"
echo "- 实现getUserIdByUsername方法"
echo "- 测试AOP切面是否正常工作"
echo

echo "=== 测试完成 ==="