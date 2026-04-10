#!/bin/bash

echo "=== AI代码助手鉴权功能验证 ==="
echo

# 检查应用是否运行
if ! curl -s --head http://localhost:8081/openai | head -n 1 | grep -q "200\|301\|302"; then
    echo "❌ 应用未运行，请先启动应用: mvn spring-boot:run"
    exit 1
fi

echo "✅ 应用正在运行"

# 检查健康状态
HEALTH_CHECK=$(curl -s http://localhost:8081/openai/api/auth/register -X OPTIONS)
if [ $? -eq 0 ]; then
    echo "✅ API接口可访问"
else
    echo "❌ API接口无法访问"
    exit 1
fi

# 检查前端页面
PAGE_CHECK=$(curl -s -I http://localhost:8081/openai/auth-demo.html | head -n 1 | grep -c "200")
if [ "$PAGE_CHECK" -eq 1 ]; then
    echo "✅ 前端页面可访问"
else
    echo "❌ 前端页面无法访问"
    exit 1
fi

echo
echo "=== 基础验证完成 ==="
echo "现在可以运行完整测试: ./test-auth-complete.sh"
echo "或访问前端页面: http://localhost:8081/openai/auth-demo.html"