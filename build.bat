@echo off
setlocal

REM 设置 Maven 路径
set MAVEN_HOME=F:\software\apache-maven-3.9.12
set PATH=%MAVEN_HOME%\bin;%PATH%

REM 检查 Maven 版本
mvn --version
if errorlevel 1 (
    echo Maven not found or failed to execute
    exit /b 1
)

REM 清理并构建项目
echo Building AI Code Helper with OpenAI...
mvn clean package

if errorlevel 1 (
    echo Build failed!
    exit /b 1
)

echo Build completed successfully!
echo JAR file created at: target\ai-code-helper-openai-0.0.1-SNAPSHOT.jar

REM 可选：运行应用
echo To run the application:
echo java -jar target\ai-code-helper-openai-0.0.1-SNAPSHOT.jar

endlocal