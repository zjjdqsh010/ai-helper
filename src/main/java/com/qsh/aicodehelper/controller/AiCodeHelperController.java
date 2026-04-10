package com.qsh.aicodehelper.controller;

import com.qsh.aicodehelper.service.AiCodeHelperServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * AI代码助手控制器
 *
 * 提供RESTful API接口，处理AI编程助手的各种功能请求
 * 包括智能对话、代码审查、调试、优化和解释等功能
 *
 * 技术关键点：
 * - @RestController: 标记为REST控制器，自动序列化返回值为JSON
 * - @RequestMapping: 定义基础URL路径
 * - @RequiredArgsConstructor: Lombok注解，生成包含final字段的构造函数
 * - @CrossOrigin: 允许跨域请求，origins="*"表示允许所有域名访问
 * - ResponseEntity: Spring的响应实体，可自定义HTTP状态码和响应头
 */
@RestController
@RequestMapping("/api/ai-helper")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AiCodeHelperController {

    /**
     * AI代码助手服务实现类
     * 通过构造函数注入，实现依赖倒置原则
     */
    private final AiCodeHelperServiceImpl aiCodeHelperService;

    /**
     * 智能对话接口
     *
     * 处理用户的一般性编程问题咨询，支持上下文记忆和多轮对话
     *
     * @param request 包含用户消息和会话ID的请求对象
     * @return 包含AI回复、会话ID和时间戳的响应
     *
     * 技术关键点：
     * - @PostMapping: 处理HTTP POST请求
     * - @RequestBody: 将JSON请求体绑定到Java对象
     * - UUID.randomUUID(): 为新的会话生成唯一标识符
     * - Map.of(): Java 9引入的工厂方法，创建不可变Map
     * - System.currentTimeMillis(): 获取当前时间戳
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestBody ChatRequest request) {
        // 如果客户端未提供sessionId，则生成新的UUID作为会话标识
        String sessionId = request.getSessionId() != null ? request.getSessionId() : UUID.randomUUID().toString();

        // 调用服务层处理聊天请求
        String response = aiCodeHelperService.chat(request.getMessage(), sessionId);

        // 调试输出，生产环境建议使用日志框架
        System.out.println(response);

        // 构建响应数据
        return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,           // 会话ID，用于保持对话上下文
                "response", response,             // AI助手的回复内容
                "timestamp", System.currentTimeMillis() // 响应时间戳
        ));
    }

    /**
     * 代码审查接口
     *
     * 分析用户提交的代码，提供代码质量评估、最佳实践建议和潜在问题检测
     *
     * @param request 包含代码内容和编程语言的请求对象
     * @return 包含代码审查结果和时间戳的响应
     *
     * 技术关键点：
     * - 支持多种编程语言代码审查
     * - 返回结构化的审查建议
     * - 包含时间戳便于日志追踪
     */
    @PostMapping("/code-review")
    public ResponseEntity<Map<String, Object>> codeReview(@RequestBody CodeReviewRequest request) {
        // 调用服务层进行代码审查
        String response = aiCodeHelperService.codeReview(request.getCode(), request.getLanguage());

        // 返回审查结果
        return ResponseEntity.ok(Map.of(
                "response", response,             // 代码审查的详细建议
                "timestamp", System.currentTimeMillis() // 响应时间戳
        ));
    }

    /**
     * 调试助手接口
     *
     * 分析代码中的错误，提供详细的错误诊断和修复建议
     *
     * @param request 包含问题代码和错误信息的请求对象
     * @return 包含调试分析和修复建议的响应
     *
     * 技术关键点：
     * - 结合代码上下文和错误信息进行分析
     * - 提供具体的修复建议
     * - 支持多种常见编程错误的诊断
     */
    @PostMapping("/debug")
    public ResponseEntity<Map<String, Object>> debug(@RequestBody DebugRequest request) {
        // 调用服务层进行代码调试分析
        String response = aiCodeHelperService.debug(request.getCode(), request.getError());

        // 返回调试结果
        return ResponseEntity.ok(Map.of(
                "response", response,             // 调试分析和修复建议
                "timestamp", System.currentTimeMillis() // 响应时间戳
        ));
    }

    /**
     * 代码优化接口
     *
     * 分析代码性能瓶颈，提供优化建议和重构指导
     *
     * @param request 包含待优化代码的请求对象
     * @return 包含优化建议和重构方案的响应
     *
     * 技术关键点：
     * - 性能优化建议（算法改进、数据结构选择）
     * - 代码重构指导（设计模式、最佳实践）
     * - 可读性和维护性改进建议
     */
    @PostMapping("/optimize")
    public ResponseEntity<Map<String, Object>> optimize(@RequestBody CodeRequest request) {
        // 调用服务层进行代码优化分析
        String response = aiCodeHelperService.optimize(request.getCode());

        // 返回优化建议
        return ResponseEntity.ok(Map.of(
                "response", response,             // 代码优化的详细建议
                "timestamp", System.currentTimeMillis() // 响应时间戳
        ));
    }

    /**
     * 代码解释接口
     *
     * 详细解释代码功能、执行流程和改进建议，帮助开发者理解代码逻辑
     *
     * @param request 包含待解释代码的请求对象
     * @return 包含代码详细解释的响应
     *
     * 技术关键点：
     * - 代码功能和目的说明
     * - 关键算法和数据结构解释
     * - 代码执行流程分析
     * - 潜在的改进建议
     */
    @PostMapping("/explain")
    public ResponseEntity<Map<String, Object>> explain(@RequestBody CodeRequest request) {
        // 调用服务层进行代码解释
        String response = aiCodeHelperService.explain(request.getCode());

        // 返回代码解释结果
        return ResponseEntity.ok(Map.of(
                "response", response,             // 代码的详细解释
                "timestamp", System.currentTimeMillis() // 响应时间戳
        ));
    }

    // ===========================================================================
    // 请求数据对象 (Request DTOs)
    // ===========================================================================

    /**
     * 聊天请求数据对象
     * 用于智能对话接口的参数传递
     */
    public static class ChatRequest {
        private String message;      // 用户输入的消息内容
        private String sessionId;    // 会话ID，用于保持对话上下文（可选）

        // Getters and setters
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    }

    /**
     * 代码审查请求数据对象
     * 用于代码审查接口的参数传递
     */
    public static class CodeReviewRequest {
        private String code;         // 待审查的代码内容
        private String language;     // 编程语言类型（如：java, python, javascript等）

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getLanguage() { return language; }
        public void setLanguage(String language) { this.language = language; }
    }

    /**
     * 调试请求数据对象
     * 用于调试助手接口的参数传递
     */
    public static class DebugRequest {
        private String code;         // 包含问题的代码
        private String error;        // 错误信息或异常描述

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    /**
     * 代码请求数据对象（通用）
     * 用于代码优化和代码解释接口的参数传递
     */
    public static class CodeRequest {
        private String code;         // 待处理（优化/解释）的代码

        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
    }
}