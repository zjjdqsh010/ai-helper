# AI Code Helper with OpenAI & LongCat

基于 OpenAI GPT 模型和 LongCat 服务的 AI 编程助手，提供代码审查、调试、优化和解释等功能。

## 技术栈

- **Java 17**
- **Spring Boot 3.2.0**
- **LangChain4j 1.1.0**
- **OpenAI API**
- **LongCat 集成**
- **Maven 3.9.12**

## 项目结构

```
src/main/java/com/qsh/aicodehelper/
├── AiCodeHelperOpenaiApplication.java    # 主应用类
├── config/
│   └── OpenAiConfig.java                 # OpenAI 配置
├── controller/
│   └── AiCodeHelperController.java       # REST API 控制器
├── service/
│   ├── AiCodeHelperService.java          # 服务接口
│   └── AiCodeHelperServiceImpl.java      # 服务实现
├── guardrail/
│   └── SafeInputGuardrail.java           # 输入安全检查
└── tools/
    └── CodeAnalysisTool.java             # 代码分析工具
```

## 功能特性

### 1. 智能对话
- 支持上下文记忆
- 多轮对话支持
- 编程相关问题解答

### 2. 代码审查
- 代码质量分析
- 最佳实践建议
- 潜在问题检测

### 3. 调试助手
- 错误分析
- 修复建议
- 问题诊断

### 4. 代码优化
- 性能优化建议
- 代码重构指导
- 算法改进

### 5. 代码解释
- 详细代码解析
- 执行流程说明
- 改进建议

## 配置说明

### 环境变量配置

```bash
# OpenAI API 配置
export OPENAI_API_KEY="your-openai-api-key"
export OPENAI_MODEL="gpt-4-turbo"
export OPENAI_TEMPERATURE="0.7"
export OPENAI_MAX_TOKENS="2000"

# LongCat 配置（可选）
export LONGCAT_BASE_URL="https://api.openai.com/v1"
```

### application.yml 配置

```yaml
langchain4j:
  openai:
    chat-model:
      api-key: ${OPENAI_API_KEY:your-api-key}
      model-name: ${OPENAI_MODEL:gpt-4-turbo}
      base-url: ${LONGCAT_BASE_URL:https://api.openai.com/v1}
      temperature: ${OPENAI_TEMPERATURE:0.7}
      max-tokens: ${OPENAI_MAX_TOKENS:2000}
```

## API 接口

### 1. 智能对话

```bash
POST /api/ai-helper/chat
Content-Type: application/json

{
  "message": "如何优化这个 Java 代码？",
  "sessionId": "optional-session-id"
}
```

### 2. 代码审查

```bash
POST /api/ai-helper/code-review
Content-Type: application/json

{
  "code": "public class Example { ... }",
  "language": "java"
}
```

### 3. 调试助手

```bash
POST /api/ai-helper/debug
Content-Type: application/json

{
  "code": "public class Example { ... }",
  "error": "NullPointerException at line 42"
}
```

### 4. 代码优化

```bash
POST /api/ai-helper/optimize
Content-Type: application/json

{
  "code": "public class Example { ... }"
}
```

### 5. 代码解释

```bash
POST /api/ai-helper/explain
Content-Type: application/json

{
  "code": "public class Example { ... }"
}
```

## 构建和运行

### 使用指定 Maven 构建

```bash
# 设置 Maven 路径
export MAVEN_HOME=F:/software/apache-maven-3.9.12
export PATH=$MAVEN_HOME/bin:$PATH

# 构建项目
mvn clean package

# 运行应用
java -jar target/ai-code-helper-openai-0.0.1-SNAPSHOT.jar
```

### 开发模式运行

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 安全特性

- 输入内容安全检查
- 敏感信息过滤
- 请求长度限制
- API 调用频率控制

## 性能优化

- 对话记忆管理
- 响应缓存
- 异步处理
- 连接池优化

## 部署说明

### Docker 部署

```bash
# 构建镜像
docker build -t ai-code-helper-openai .

# 运行容器
docker run -p 8080:8080 \
  -e OPENAI_API_KEY=your-api-key \
  -e LONGCAT_BASE_URL=https://api.openai.com/v1 \
  ai-code-helper-openai
```

### Kubernetes 部署

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ai-code-helper-openai
spec:
  replicas: 2
  selector:
    matchLabels:
      app: ai-code-helper-openai
  template:
    metadata:
      labels:
        app: ai-code-helper-openai
    spec:
      containers:
      - name: ai-code-helper
        image: ai-code-helper-openai:latest
        ports:
        - containerPort: 8080
        env:
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: ai-secrets
              key: openai-api-key
```

## 故障排除

### 常见问题

1. **API Key 配置错误**
   - 检查环境变量是否正确设置
   - 确认 API Key 是否有效

2. **连接超时**
   - 检查网络连接
   - 验证 base-url 配置
   - 调整超时设置

3. **内存不足**
   - 增加 JVM 堆内存
   - 优化对话记忆设置

### 日志查看

```bash
# 查看应用日志
tail -f logs/application.log

# 启用调试模式
java -jar app.jar --debug
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或联系项目维护者。