# AI代码助手前端

一个现代化、简洁高级的AI代码助手前端界面，用于与后端API进行交互。

## 🎨 设计特色

- **现代化UI设计**：渐变背景、毛玻璃效果、流畅动画
- **响应式布局**：完美适配桌面端和移动端
- **直观的用户体验**：清晰的标签页导航、实时状态反馈
- **优雅的交互**：平滑过渡动画、加载状态指示

## 🚀 功能特性

### 核心功能
- **智能对话**：支持上下文记忆的编程问答
- **代码审查**：多语言代码质量分析
- **调试助手**：错误诊断和修复建议
- **代码优化**：性能优化和重构指导
- **代码解释**：详细的代码逻辑说明

### 用户体验
- **会话管理**：自动保存和恢复对话会话
- **状态监控**：实时显示API连接状态
- **错误处理**：友好的错误提示和重试机制
- **快捷键支持**：Ctrl+Enter快速发送消息

## 📋 快速开始

### 前提条件
- 后端服务已启动并运行在 `localhost:8081`
- 现代浏览器（Chrome、Firefox、Safari、Edge）

### 启动步骤

1. **启动后端服务**
   ```bash
   cd /path/to/ai-code-helper-openai
   mvn spring-boot:run
   ```

2. **启动前端**
   由于是纯静态文件，可以通过以下任一方式启动：

   **方式一：直接打开**
   ```bash
   # 直接双击 index.html 文件
   open frontend/index.html
   ```

   **方式二：使用Python HTTP服务器**
   ```bash
   cd frontend
   python -m http.server 3000
   # 然后访问 http://localhost:3000
   ```

   **方式三：使用Node.js http-server**
   ```bash
   npm install -g http-server
   cd frontend
   http-server -p 3000
   # 然后访问 http://localhost:3000
   ```

   **方式四：使用Live Server（VS Code插件）**
   - 安装Live Server插件
   - 右键点击index.html选择"Open with Live Server"

## 🔧 配置说明

### API端点配置
编辑 `script.js` 文件中的API基础URL：

```javascript
const API_BASE_URL = 'http://localhost:8081/openai';
```

根据您的后端配置调整URL：
- 如果后端端口不同，修改端口号
- 如果上下文路径不同，修改路径部分
- 如果是生产环境，修改为实际的域名

### 自定义样式
可以通过修改 `styles.css` 来自定义：
- 颜色主题（修改CSS变量）
- 字体和排版
- 响应式断点
- 动画效果

## 📱 界面说明

### 功能标签页
1. **智能对话** - 一般性编程问题咨询
2. **代码审查** - 代码质量分析和建议
3. **调试助手** - 错误诊断和修复
4. **代码优化** - 性能优化建议
5. **代码解释** - 代码逻辑详细说明

### 输入区域
- 支持多行文本输入
- 代码编辑器样式（等宽字体、语法高亮）
- 实时验证和提示

### 响应区域
- 格式化显示AI回复
- 支持代码块显示
- 错误状态提示
- 加载动画

## 🎯 使用示例

### 智能对话
```
用户输入：如何优化Java代码的性能？
AI回复：可以从以下几个方面优化...
```

### 代码审查
```
代码输入：public class Example { ... }
语言选择：Java
AI回复：这段代码存在以下问题...
```

### 调试助手
```
问题代码：String s = null; s.length();
错误信息：NullPointerException at line 1
AI回复：这个问题的原因是...
```

## 🔧 开发说明

### 文件结构
```
frontend/
├── index.html          # 主页面
├── styles.css          # 样式文件
├── script.js           # JavaScript逻辑
└── README.md          # 说明文档
```

### 技术栈
- **HTML5** - 语义化标签
- **CSS3** - Flexbox、Grid、动画
- **Vanilla JavaScript** - ES6+语法
- **Font Awesome** - 图标库
- **Google Fonts** - Inter字体

### 浏览器兼容性
- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## 🎨 自定义指南

### 修改主题颜色
编辑 `styles.css` 中的渐变背景：
```css
background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
```

### 添加新功能
1. 在 `index.html` 中添加新的标签按钮
2. 添加对应的内容面板
3. 在 `script.js` 中添加API调用函数
4. 更新样式文件

## 🚨 故障排除

### API连接失败
- 检查后端服务是否正常运行
- 确认API URL配置正确
- 检查浏览器控制台错误信息
- 验证CORS配置

### 页面显示异常
- 清除浏览器缓存
- 检查CSS文件是否正确加载
- 确认字体和图标资源可访问

### 功能不正常
- 查看浏览器开发者工具控制台
- 检查网络请求状态
- 验证输入数据格式

## 📄 许可证

MIT License - 详见项目根目录LICENSE文件