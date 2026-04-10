#!/bin/bash

echo "=== 测试index.html鉴权功能 ==="
echo

# 清除任何现有的token
unset AUTH_TOKEN

echo "1. 测试未登录状态访问index.html..."
# 模拟浏览器访问，不带任何认证信息
curl -s -I http://localhost:8081/openai/ | head -n 1
echo

# 注册一个测试用户
echo "2. 注册测试用户..."
REG_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "integration_test", "email": "integration@test.com", "password": "test123", "nickname": "集成测试"}')

echo "注册响应: $REG_RESPONSE"

# 提取token
TOKEN=$(echo "$REG_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ ! -z "$TOKEN" ]; then
    echo "✅ 成功获取Token"
    echo "Token: $TOKEN"
    echo

    # 测试带token访问
    echo "3. 测试带Token访问index.html..."
    HTTP_RESPONSE=$(curl -s -I -H "Authorization: Bearer $TOKEN" http://localhost:8081/openai/)
    STATUS_CODE=$(echo "$HTTP_RESPONSE" | head -n 1 | grep -o '200\|401\|403')

    if [ "$STATUS_CODE" = "200" ]; then
        echo "✅ 带Token访问成功 (HTTP 200)"
    else
        echo "❌ 带Token访问失败 (HTTP $STATUS_CODE)"
        echo "响应头:"
        echo "$HTTP_RESPONSE"
    fi
else
    echo "❌ 注册失败，无法获取Token"
    echo "响应: $REG_RESPONSE"
fi

echo
echo "=== 测试完成 ==="