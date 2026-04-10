#!/bin/bash

echo "=== 新登录注册页面测试 ==="
echo

echo "测试新创建的页面可访问性："
echo "================================"
echo

# 测试登录页面
echo "1. 测试登录页面..."
curl -s -I http://localhost:8081/openai/login.html | head -n 1
echo

# 测试注册页面
echo "2. 测试注册页面..."
curl -s -I http://localhost:8081/openai/register.html | head -n 1
echo

# 测试演示页面
echo "3. 测试演示页面..."
curl -s -I http://localhost:8081/openai/auth-demo.html | head -n 1
echo

# 测试主页面（应该需要登录）
echo "4. 测试主页面（未登录状态）..."
curl -s -I http://localhost:8081/openai/ | head -n 1
echo

echo "================================"
echo "页面访问测试完成！"
echo

echo "手动测试步骤："
echo "============================"
echo "1. 打开浏览器访问以下页面："
echo "   - 登录页面: http://localhost:8081/openai/login.html"
echo "   - 注册页面: http://localhost:8081/openai/register.html"
echo "   - 演示页面: http://localhost:8081/openai/auth-demo.html"
echo

echo "2. 测试注册流程："
echo "   - 访问注册页面"
echo "   - 填写注册信息"
echo "   - 注册成功后应该自动跳转到主页面"
echo

echo "3. 测试登录流程："
echo "   - 访问登录页面"
echo "   - 使用注册的账户登录"
echo "   - 登录成功后应该自动跳转到主页面"
echo

echo "4. 测试主页面访问："
echo "   - 直接访问 http://localhost:8081/openai/"
echo "   - 未登录时应该重定向到登录页面"
echo "   - 已登录时应该正常显示"
echo

echo "5. 测试退出功能："
echo "   - 在主页面点击退出登录"
echo "   - 应该返回到登录页面"
echo

echo "============================"
echo "请手动执行以上测试步骤"
echo
echo "快速API测试："

# 测试注册API
TIMESTAMP=$(date +%s)
REG_RESPONSE=$(curl -s -X POST http://localhost:8081/openai/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"newtestuser$TIMESTAMP\", \"email\": \"newtest$TIMESTAMP@example.com\", \"password\": \"test123\", \"nickname\": \"newtest$TIMESTAMP\"}")

if echo "$REG_RESPONSE" | grep -q "token"; then
    echo "✅ 注册API测试成功"
    TOKEN=$(echo "$REG_RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    echo "Token: $TOKEN"
else
    echo "❌ 注册API测试失败: $REG_RESPONSE"
fi

echo
echo "=== 测试完成 ==="