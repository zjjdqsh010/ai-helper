@echo off
setlocal

REM 设置环境变量（根据实际情况修改）
set OPENAI_API_KEY=your-api-key-here
set LONGCAT_BASE_URL=https://api.openai.com/v1
set OPENAI_MODEL=gpt-4-turbo
set OPENAI_TEMPERATURE=0.7
set OPENAI_MAX_TOKENS=2000

REM 运行应用
echo Starting AI Code Helper with OpenAI...
java -jar target\ai-code-helper-openai-0.0.1-SNAPSHOT.jar

if errorlevel 1 (
    echo Application failed to start!
    exit /b 1
)

endlocal