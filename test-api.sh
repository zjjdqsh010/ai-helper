#!/bin/bash

# API测试脚本
# 用于测试AI代码助手的所有API接口

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 基础URL
BASE_URL="http://localhost:8080"

# 检查服务是否可用
check_service() {
    echo "检查服务是否运行..."
    if curl -s --head --fail "$BASE_URL/actuator/health" > /dev/null 2>&1; then
        echo -e "${GREEN}服务运行正常${NC}"
        return 0
    else
        echo -e "${RED}服务未运行，请先启动应用${NC}"
        echo "使用命令: mvn spring-boot:run"
        exit 1
    fi
}

# 测试聊天接口
test_chat() {
    echo "测试聊天接口..."
    curl -s -X POST "$BASE_URL/api/ai-helper/chat" \
        -H "Content-Type: application/json" \
        -d '{
            "message": "你好，请帮我写一个Java方法计算阶乘",
            "sessionId": "test-session-001"
        }' | jq .
    echo ""
}

# 测试代码审查接口
test_code_review() {
    echo "测试代码审查接口..."
    curl -s -X POST "$BASE_URL/api/ai-helper/code-review" \
        -H "Content-Type: application/json" \
        -d '{
            "code": "public class Example { public void badMethod() { String s = null; s.length(); } }",
            "language": "java"
        }' | jq .
    echo ""
}

# 测试调试接口
test_debug() {
    echo "测试调试接口..."
    curl -s -X POST "$BASE_URL/api/ai-helper/debug" \
        -H "Content-Type: application/json" \
        -d '{
            "code": "public class Test { public static void main(String[] args) { String name = null; int len = name.length(); } }",
            "error": "NullPointerException at line 3"
        }' | jq .
    echo ""
}

# 测试优化接口
test_optimize() {
    echo "测试代码优化接口..."
    curl -s -X POST "$BASE_URL/api/ai-helper/optimize" \
        -H "Content-Type: application/json" \
        -d '{
            "code": "String result = \"\"; for(int i = 0; i < 1000; i++) { result += i; }"
        }' | jq .
    echo ""
}

# 测试解释接口
test_explain() {
    echo "测试代码解释接口..."
    curl -s -X POST "$BASE_URL/api/ai-helper/explain" \
        -H "Content-Type: application/json" \
        -d '{
            "code": "public class HelloWorld { public static void main(String[] args) { System.out.println(\"Hello, World!\"); } }"
        }' | jq .
    echo ""
}

# 运行所有测试
run_all_tests() {
    check_service

    echo "开始API测试..."
    echo "================================"

    test_chat
    test_code_review
    test_debug
    test_optimize
    test_explain

    echo -e "${GREEN}所有测试完成！${NC}"
}

# 显示帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  --all           运行所有测试 (默认)"
    echo "  --chat          仅测试聊天接口"
    echo "  --code-review   仅测试代码审查接口"
    echo "  --debug         仅测试调试接口"
    echo "  --optimize      仅测试优化接口"
    echo "  --explain       仅测试解释接口"
    echo "  --help          显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0              # 运行所有测试"
    echo "  $0 --chat       # 仅测试聊天接口"
}

# 主程序
case "${1:---all}" in
    --all)
        run_all_tests
        ;;
    --chat)
        check_service && test_chat
        ;;
    --code-review)
        check_service && test_code_review
        ;;
    --debug)
        check_service && test_debug
        ;;
    --optimize)
        check_service && test_optimize
        ;;
    --explain)
        check_service && test_explain
        ;;
    --help|*)
        show_help
        ;;
esac