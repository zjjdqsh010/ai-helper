@echo off

REM API测试脚本 (Windows版本)
REM 用于测试AI代码助手的所有API接口

set BASE_URL=http://localhost:8080

REM 检查服务是否可用
:check_service
echo 检查服务是否运行...
curl -s --head --fail "%BASE_URL%/actuator/health" > nul 2>&1
if %errorlevel% equ 0 (
    echo 服务运行正常
    goto :main
) else (
    echo 服务未运行，请先启动应用
    echo 使用命令: mvn spring-boot:run
    pause
    exit /b 1
)

:main
echo.
echo 开始API测试...
echo ================================
echo.

:test_chat
echo 测试聊天接口...
curl -s -X POST "%BASE_URL%/api/ai-helper/chat" ^
    -H "Content-Type: application/json" ^
    -d "{\"message\": \"你好，请帮我写一个Java方法计算阶乘\", \"sessionId\": \"test-session-001\"}"
echo.
echo.

:test_code_review
echo 测试代码审查接口...
curl -s -X POST "%BASE_URL%/api/ai-helper/code-review" ^
    -H "Content-Type: application/json" ^
    -d "{\"code\": \"public class Example { public void badMethod() { String s = null; s.length(); } }\", \"language\": \"java\"}"
echo.
echo.

:test_debug
echo 测试调试接口...
curl -s -X POST "%BASE_URL%/api/ai-helper/debug" ^
    -H "Content-Type: application/json" ^
    -d "{\"code\": \"public class Test { public static void main(String[] args) { String name = null; int len = name.length(); } }\", \"error\": \"NullPointerException at line 3\"}"
echo.
echo.

:test_optimize
echo 测试代码优化接口...
curl -s -X POST "%BASE_URL%/api/ai-helper/optimize" ^
    -H "Content-Type: application/json" ^
    -d "{\"code\": \"String result = \\\"\\\"; for(int i = 0; i < 1000; i++) { result += i; }\"}"
echo.
echo.

:test_explain
echo 测试代码解释接口...
curl -s -X POST "%BASE_URL%/api/ai-helper/explain" ^
    -H "Content-Type: application/json" ^
    -d "{\"code\": \"public class HelloWorld { public static void main(String[] args) { System.out.println(\\\\\"Hello, World!\\\\\"); } }\"}"
echo.
echo.

echo 所有测试完成！
pause