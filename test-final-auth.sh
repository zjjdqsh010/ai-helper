#!/bin/bash

echo "=== 最终鉴权功能测试 ==="
echo

echo "注意：这是一个前端鉴权测试，需要手动验证"
echo "=========================================="
echo

echo "测试步骤："
echo "1. 打开浏览器访问: http://localhost:8081/openai/"
echo "   - 如果没有登录，应该自动跳转到登录页面"
echo "   - 如果已登录，应该正常显示主页面"
echo

echo "2. 访问登录页面: http://localhost:8081/openai/auth-demo.html"
echo "   - 使用已有账户登录或注册新账户"
echo "   - 登录成功后会自动跳转到主页面"
echo

echo "3. 登录后再次访问: http://localhost:8081/openai/"
echo "   - 应该能正常访问主页面"
echo "   - 右上角有退出登录按钮"
echo

echo "4. 点击退出登录"
echo "   - 应该返回到登录页面"
echo "   - 再次访问主页面会要求重新登录"
echo

echo "5. API接口测试："
echo "   - 未登录时访问API应该返回401错误"
echo "   - 登录后使用Token访问API应该正常工作"
echo

echo "=========================================="
echo "请手动执行以上测试步骤"
echo
echo "快速API测试（可选）："

# 测试未登录访问API
echo "测试未登录访问API:"
curl -s -I http://localhost:8081/openai/api/ai-helper/chat || echo "API访问失败（正常）"
echo

# 注册测试用户
echo "注册测试用户:"
REG_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "final_test_user", "email": "final@test.com", "password": "test123", "nickname": "最终测试"}')

echo "注册结果: $REG_RESPONSE"

TOKEN=$(echo "$REG_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ ! -z "$TOKEN" ]; then
    echo
echo "✅ 注册成功！"
echo "Token: $TOKEN"
echo
    echo "测试带Token的API访问:"

    # 测试带Token的API访问
    API_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/ai-helper/chat \
      -H "Content-Type: application/json" \
      -H "Authorization: Bearer $TOKEN" \
      -d '{"message": "你好，这是一个测试消息", "sessionId": null}')

    echo "API响应: $API_RESPONSE"

    if echo "$API_RESPONSE" | grep -q "error"; then
        echo "❌ API访问失败"
    else
        echo "✅ API访问成功"
    fi
else
    echo "❌ 注册失败"
fi

echo
echo "=== 测试完成 ==="
echo "请根据上述步骤手动测试前端鉴权功能"