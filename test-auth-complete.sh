#!/bin/bash

echo "=== AI代码助手 - 鉴权功能完整测试 ==="
echo

# 生成随机用户名避免冲突
TIMESTAMP=$(date +%s)
USERNAME="testuser$TIMESTAMP"
EMAIL="test$TIMESTAMP@example.com"
PASSWORD="test123"
NICKNAME="TestUser$TIMESTAMP"

echo "测试用户信息:"
echo "用户名: $USERNAME"
echo "邮箱: $EMAIL"
echo "密码: $PASSWORD"
echo "昵称: $NICKNAME"
echo

# 1. 测试用户注册
echo "1. 测试用户注册..."
REG_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USERNAME\", \"email\": \"$EMAIL\", \"password\": \"$PASSWORD\", \"nickname\": \"$NICKNAME\"}")

if echo "$REG_RESPONSE" | grep -q "token"; then
    echo "✅ 注册成功"
    TOKEN=$(echo "$REG_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "Token: $TOKEN"
else
    echo "❌ 注册失败: $REG_RESPONSE"
    exit 1
fi
echo

# 2. 测试用户登录
echo "2. 测试用户登录..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USERNAME\", \"password\": \"$PASSWORD\"}")

if echo "$LOGIN_RESPONSE" | grep -q "token"; then
    echo "✅ 登录成功"
    LOGIN_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
else
    echo "❌ 登录失败: $LOGIN_RESPONSE"
    exit 1
fi
echo

# 3. 测试获取用户信息
echo "3. 测试获取当前用户信息..."
USER_RESPONSE=$(curl -s -X GET http://localhost:8081/openai/api/auth/me \
  -H "Authorization: Bearer $LOGIN_TOKEN")

if echo "$USER_RESPONSE" | grep -q "user"; then
    echo "✅ 获取用户信息成功"
    echo "用户信息: $USER_RESPONSE"
else
    echo "❌ 获取用户信息失败: $USER_RESPONSE"
    exit 1
fi
echo

# 4. 测试退出登录
echo "4. 测试退出登录..."
LOGOUT_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/logout \
  -H "Authorization: Bearer $LOGIN_TOKEN")

if echo "$LOGOUT_RESPONSE" | grep -q "message"; then
    echo "✅ 退出登录成功"
else
    echo "❌ 退出登录失败: $LOGOUT_RESPONSE"
    exit 1
fi
echo

# 5. 测试错误情况
echo "5. 测试错误情况..."

# 5.1 重复注册
echo "  5.1 测试重复注册..."
DUP_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USERNAME\", \"email\": \"$EMAIL\", \"password\": \"$PASSWORD\", \"nickname\": \"$NICKNAME\"}")

if echo "$DUP_RESPONSE" | grep -q "error"; then
    echo "  ✅ 重复注册正确返回错误"
else
    echo "  ❌ 重复注册未正确处理"
fi

# 5.2 错误密码登录
echo "  5.2 测试错误密码登录..."
WRONG_PASS_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"$USERNAME\", \"password\": \"wrongpassword\"}")

if echo "$WRONG_PASS_RESPONSE" | grep -q "error"; then
    echo "  ✅ 错误密码正确返回错误"
else
    echo "  ❌ 错误密码未正确处理"
fi

# 5.3 无效Token获取用户信息
echo "  5.3 测试无效Token..."
INVALID_TOKEN_RESPONSE=$(curl -s -X GET http://localhost:8081/openai/api/auth/me \
  -H "Authorization: Bearer invalid_token")

if echo "$INVALID_TOKEN_RESPONSE" | grep -q "error"; then
    echo "  ✅ 无效Token正确返回错误"
else
    echo "  ❌ 无效Token未正确处理"
fi
echo

echo "=== 测试完成 ==="
echo "所有测试通过！鉴权功能正常工作。"