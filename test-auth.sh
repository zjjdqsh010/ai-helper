#!/bin/bash

echo "=== 测试用户注册 ==="
curl -X POST http://localhost:8081/openai/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "test123",
    "nickname": "测试用户"
  }'
echo -e "\n"

echo "=== 测试用户登录 ==="
curl -X POST http://localhost:8081/openai/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'
echo -e "\n"

echo "=== 测试获取用户信息（需要手动替换TOKEN）==="
echo "curl -X GET http://localhost:8081/openai/api/auth/me -H 'Authorization: Bearer YOUR_TOKEN'"