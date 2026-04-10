package com.qsh.aicodehelper.service;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * AI代码助手服务实现类
 *
 * 实现AI编程助手的核心业务逻辑
 * 使用LangChain4j的AiServices框架简化AI集成
 *
 * 主要功能：
 * - 初始化AI服务配置
 * - 提供对话记忆管理
 * - 代理调用AI接口方法
 *
 * 技术关键点：
 * - @Service: 标记为Spring服务组件
 * - @RequiredArgsConstructor: Lombok注解，生成构造函数
 * - @PostConstruct: 在依赖注入完成后执行初始化
 * - AiServices: LangChain4j的AI服务构建器
 * - MessageWindowChatMemory: 消息窗口聊天记忆，限制最大消息数量
 */
@Service
@RequiredArgsConstructor
public class AiCodeHelperServiceImpl {

    /**
     * 聊天语言模型
     * 通过构造函数注入，实现依赖倒置原则
     */
    private final ChatModel chatLanguageModel;

    /**
     * AI代码助手服务代理实例
     * 由AiServices动态创建，封装了AI调用的底层逻辑
     */
    private AiCodeHelperService aiCodeHelperService;

    /**
     * 初始化方法
     *
     * 在Spring容器创建Bean后执行，配置AI服务
     *
     * 技术关键点：
     * - @PostConstruct: 确保依赖注入完成后执行
     * - AiServices.builder(): 创建AI服务代理
     * - chatMemoryProvider: 配置对话记忆提供者
     * - MessageWindowChatMemory: 实现消息窗口记忆，限制内存使用
     * - maxMessages(10): 限制每个会话最多保存10条消息
     */
    @PostConstruct
    public void init() {
        aiCodeHelperService = AiServices.builder(AiCodeHelperService.class)
                .chatModel(chatLanguageModel)  // 配置聊天模型
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)          // 设置记忆ID
                        .maxMessages(10)       // 限制最大消息数量，防止内存溢出
                        .build())
                .build();
    }

    /**
     * 处理智能对话请求
     *
     * @param message 用户消息
     * @param memoryId 会话ID
     * @return AI回复
     */
    public String chat(String message, String memoryId) {
        return aiCodeHelperService.chat(message, memoryId);
    }

    /**
     * 处理代码审查请求
     *
     * @param code 待审查的代码
     * @param language 编程语言
     * @return 审查结果
     */
    public String codeReview(String code, String language) {
        return aiCodeHelperService.codeReview(code, language);
    }

    /**
     * 处理调试请求
     *
     * @param code 问题代码
     * @param error 错误信息
     * @return 调试结果
     */
    public String debug(String code, String error) {
        return aiCodeHelperService.debug(code, error);
    }

    /**
     * 处理代码优化请求
     *
     * @param code 待优化的代码
     * @return 优化建议
     */
    public String optimize(String code) {
        return aiCodeHelperService.optimize(code);
    }

    /**
     * 处理代码解释请求
     *
     * @param code 待解释的代码
     * @return 代码解释
     */
    public String explain(String code) {
        return aiCodeHelperService.explain(code);
    }
}