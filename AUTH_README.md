# 用户认证模块使用说明

## 功能概述

本项目已集成完整的用户认证模块，包含以下功能：

- ✅ 用户注册
- ✅ 用户登录
- ✅ 用户退出
- ✅ 获取当前用户信息
- ✅ JWT Token认证
- ✅ Spring Security集成
- ✅ PostgreSQL数据库支持

## 数据库配置

### 创建用户表

执行以下SQL脚本在PostgreSQL中创建用户表：

```sql
-- 运行SQL脚本
psql -h 192.168.11.211 -p 5432 -U postgres -d sxxm -f create_user_table.sql
```

### 数据库连接配置

已在 `application.yml` 中配置：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://192.168.11.211:5432/sxxm
    username: postgres
    password: wckj123
```

## API接口说明

### 1. 用户注册

**请求:**
```http
POST /openai/api/auth/register
Content-Type: application/json

{
  "username": "用户名",
  "email": "邮箱地址",
  "password": "密码（至少6位）",
  "nickname": "昵称（可选）"
}
```

**成功响应:**
```json
{
  "token": "JWT_TOKEN",
  "user": {
    "id": 1,
    "username": "用户名",
    "email": "邮箱地址",
    "nickname": "昵称",
    "createdAt": "2024-04-09T10:00:00"
  }
}
```

### 2. 用户登录

**请求:**
```http
POST /openai/api/auth/login
Content-Type: application/json

{
  "username": "用户名",
  "password": "密码"
}
```

**成功响应:**
```json
{
  "token": "JWT_TOKEN",
  "user": {
    "id": 1,
    "username": "用户名",
    "email": "邮箱地址",
    "nickname": "昵称",
    "lastLoginAt": "2024-04-09T10:00:00"
  }
}
```

### 3. 获取当前用户信息

**请求:**
```http
GET /openai/api/auth/me
Authorization: Bearer JWT_TOKEN
```

**成功响应:**
```json
{
  "user": {
    "id": 1,
    "username": "用户名",
    "email": "邮箱地址",
    "nickname": "昵称",
    "lastLoginAt": "2024-04-09T10:00:00",
    "status": 1
  }
}
```

### 4. 用户退出

**请求:**
```http
POST /openai/api/auth/logout
Authorization: Bearer JWT_TOKEN
```

**成功响应:**
```json
{
  "message": "Logged out successfully"
}
```

## 前端演示页面

访问地址：`http://localhost:8081/openai/auth-demo.html`

该页面提供了完整的注册、登录、退出和用户信息查询功能演示。

## 安全配置

### JWT配置

```yaml
jwt:
  secret: your-secret-key-here-1234567890-1234567890
  expiration: 86400000  # 24小时（毫秒）
```

### 安全规则

- `/api/auth/**` - 允许匿名访问（注册、登录等）
- `/api/**` - 需要认证（携带有效JWT Token）
- 其他路径 - 允许匿名访问

## 项目结构

```
src/main/java/com/qsh/aicodehelper/
├── config/
│   └── SecurityConfig.java          # Spring Security配置
├── controller/
│   └── AuthController.java          # 认证控制器
├── entity/
│   └── User.java                    # 用户实体
├── filter/
│   └── JwtAuthenticationFilter.java # JWT认证过滤器
├── repository/
│   └── UserRepository.java          # 用户仓库
├── service/
│   ├── AuthService.java             # 认证服务
│   └── CustomUserDetailsService.java # 用户详情服务
└── util/
    └── JwtUtil.java                 # JWT工具类
```

## 快速开始

### 1. 编译项目

```bash
mvn clean compile
```

### 2. 运行应用

```bash
mvn spring-boot:run
```

### 3. 访问演示页面

打开浏览器访问：`http://localhost:8081/openai/auth-demo.html`

### 4. 测试API

#### 注册用户
```bash
curl -X POST http://localhost:8081/openai/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "test123",
    "nickname": "测试用户"
  }'
```

#### 用户登录
```bash
curl -X POST http://localhost:8081/openai/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'
```

#### 获取用户信息
```bash
curl -X GET http://localhost:8081/openai/api/auth/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 常见问题

### 1. 数据库连接失败

检查 `application.yml` 中的数据库配置是否正确：
- IP地址: 192.168.11.211
- 端口: 5432
- 数据库名: sxxm
- 用户名: postgres
- 密码: wckj123

### 2. JWT Token无效

- 确认Token未过期（默认24小时）
- 确认Authorization头格式正确：`Bearer YOUR_TOKEN`
- 检查JWT密钥配置

### 3. 用户已存在

- 注册时确保用户名和邮箱唯一
- 可先尝试登录，如果用户存在

## 扩展建议

### 1. 密码强度验证

可在注册接口中添加密码复杂度验证：
- 至少8位
- 包含大小写字母
- 包含数字
- 包含特殊字符

### 2. 邮箱验证

添加邮箱验证功能：
- 发送验证邮件
- 邮箱激活

### 3. 密码重置

添加密码重置功能：
- 通过邮箱重置密码
- 安全问题重置

### 4. Token刷新

实现Token刷新机制：
- 短期Access Token
- 长期Refresh Token

### 5. 用户权限管理

扩展用户角色和权限：
- 管理员角色
- 普通用户角色
- 权限控制

## 注意事项

1. **生产环境安全**：
   - 修改默认JWT密钥
   - 使用HTTPS
   - 配置CORS策略

2. **数据库备份**：
   - 定期备份用户数据
   - 重要操作日志记录

3. **性能优化**：
   - 添加用户查询缓存
   - 优化数据库索引

4. **监控告警**：
   - 登录失败监控
   - 异常访问检测

5. **API调用注意事项**：
   - 使用英文或ASCII字符避免JSON编码问题
   - 确保请求头包含正确的Content-Type: application/json
   - Token格式：Bearer YOUR_TOKEN