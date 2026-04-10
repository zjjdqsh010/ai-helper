package com.qsh.aicodehelper.service;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * AI代码助手服务接口
 *
 * 定义AI编程助手的核心业务逻辑接口
 * 使用LangChain4j的AI Services功能，通过注解配置AI行为
 *
 * 主要功能：
 * - 智能对话：支持上下文记忆的多轮对话
 * - 代码审查：分析代码质量和最佳实践
 * - 调试助手：错误诊断和修复建议
 * - 代码优化：性能优化和重构指导
 * - 代码解释：详细解释代码逻辑
 *
 * 技术关键点：
 * - @SystemMessage: 定义AI的系统提示词，控制AI的行为和角色
 * - @UserMessage: 标记用户输入的消息
 * - @MemoryId: 用于对话记忆的会话标识
 * - @V: 引用变量值
 * - AiServices: LangChain4j的AI服务框架，简化AI集成
 */
public interface AiCodeHelperService {

    /**
     * 智能对话方法
     *
     * 处理用户的编程相关问题，支持上下文记忆和多轮对话
     *
     * @param userMessage 用户输入的消息
     * @param memoryId 会话ID，用于保持对话上下文
     * @return AI助手的回复
     *
     * 技术关键点：
     * - @SystemMessage定义AI角色为专业编程助手
     * - @MemoryId实现对话记忆功能
     * - 支持多轮对话和上下文理解
     */
    @SystemMessage("""
        你是一个专业的AI编程助手，可以帮助开发者解决编程问题、
        代码审查、调试、优化和解释代码。请提供清晰、准确、
        实用的建议和解决方案。
        """)
    String chat(@UserMessage String userMessage, @MemoryId String memoryId);

    /**
     * 代码审查方法
     *
     * 分析代码质量，提供改进建议和最佳实践指导
     *
     * @param code 待审查的代码
     * @param language 编程语言类型
     * @return 代码审查结果和建议
     *
     * 技术关键点：
     * - @V("language")引用language参数值
     * - 针对特定编程语言提供专业化建议
     * - 涵盖代码质量、安全性、性能等方面
     */
    @SystemMessage("""
        你是一个专业的AI编程助手。请根据用户的编程问题提供详细的解答，
        包括代码示例、最佳实践和可能的解决方案。
        """)
    String codeReview(@UserMessage String code, @V("language") String language);

    /**
     * 调试助手方法
     *
     * 分析代码错误，提供详细的诊断和修复建议
     *
     * @param code 包含问题的代码
     * @param error 错误信息
     * @return 调试分析和修复建议
     *
     * 技术关键点：
     * - 结合代码上下文和错误信息进行综合分析
     * - 提供具体的修复步骤和代码示例
     * - 预防类似问题的建议
     */
    @SystemMessage("""
        你是一个专业的调试助手。请帮助用户分析代码中的问题，
        提供详细的错误诊断和修复建议。
        """)
    String debug(@UserMessage String code, @V("error") String error);

    /**
     * 代码优化方法
     *
     * 分析代码性能瓶颈，提供优化建议和重构方案
     *
     * @param code 待优化的代码
     * @return 优化建议和重构指导
     *
     * 技术关键点：
     * - 性能优化（时间复杂度、空间复杂度）
     * - 代码重构（设计模式、最佳实践）
     * - 可读性和维护性改进
     */
    @SystemMessage("""
        你是一个代码优化专家。请分析提供的代码并提供性能优化建议，
        包括算法改进、代码重构和最佳实践。
        """)
    String optimize(@UserMessage String code);

    /**
     * 代码解释方法
     *
     * 详细解释代码功能、执行流程和改进建议
     *
     * @param code 待解释的代码
     * @return 代码的详细解释
     *
     * 技术关键点：
     * - 功能说明和目的解释
     * - 算法和数据结构分析
     * - 执行流程逐步解释
     * - 潜在改进建议
     */
    @SystemMessage("""
        你是一个代码解释专家。请详细解释提供的代码，包括：
        1. 代码的功能和目的
        2. 关键算法和数据结构
        3. 代码的执行流程
        4. 可能的改进建议
        """)
    String explain(@UserMessage String code);
}