package com.qsh.aicodehelper.config;

import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.chat.listener.ChatModelRequestContext;
import dev.langchain4j.model.chat.listener.ChatModelResponseContext;
import dev.langchain4j.model.chat.listener.ChatModelErrorContext;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ChatModel监听器实现类
 *
 * 监控和记录ChatModel的交互过程，用于调试、性能监控和错误追踪
 * 实现ChatModelListener接口，监听请求、响应和错误事件
 *
 * 主要功能：
 * - 记录AI模型请求的开始时间
 * - 记录AI模型响应的详细信息
 * - 捕获和记录AI模型调用过程中的错误
 *
 * 技术关键点：
 * - @Component: 标记为Spring组件，自动注册到应用上下文
 * - SLF4J: 使用日志门面接口，支持多种日志实现
 * - 事件监听：实现观察者模式，监控模型调用生命周期
 * - 非侵入式：通过监听器模式，不修改核心业务逻辑
 */
@Component
public class ChatModelListenerImpl implements ChatModelListener {

    /**
     * SLF4J日志记录器
     * 用于记录ChatModel的各种事件和状态
     */
    private static final Logger log = LoggerFactory.getLogger(ChatModelListenerImpl.class);

    /**
     * 处理ChatModel请求开始事件
     *
     * 在AI模型处理请求之前调用，可用于：
     * - 记录请求开始时间
     * - 监控请求频率
     * - 性能分析
     *
     * @param requestContext 请求上下文，包含请求参数、时间戳等信息
     *
     * 技术关键点：
     * - ChatModelRequestContext: 封装请求相关信息
     * - log.info: 记录信息级别日志
     */
    @Override
    public void onRequest(ChatModelRequestContext requestContext) {
        log.info("ChatModel request started: {}", requestContext);
    }

    /**
     * 处理ChatModel响应完成事件
     *
     * 在AI模型返回响应后调用，可用于：
     * - 记录响应时间和结果
     * - 监控响应质量
     * - 性能指标收集
     *
     * @param responseContext 响应上下文，包含响应内容、耗时等信息
     *
     * 技术关键点：
     * - ChatModelResponseContext: 封装响应相关信息
     * - 响应分析：可提取token使用量、响应时间等指标
     */
    @Override
    public void onResponse(ChatModelResponseContext responseContext) {
        log.info("ChatModel response received: {}", responseContext);
    }

    /**
     * 处理ChatModel错误事件
     *
     * 在AI模型调用发生错误时调用，可用于：
     * - 错误监控和报警
     * - 失败请求分析
     * - 系统稳定性监控
     *
     * @param errorContext 错误上下文，包含错误类型、异常信息等
     *
     * 技术关键点：
     * - ChatModelErrorContext: 封装错误相关信息
     * - log.error: 记录错误级别日志
     * - 错误追踪：可集成APM工具进行错误分析
     */
    @Override
    public void onError(ChatModelErrorContext errorContext) {
        log.error("ChatModel error occurred: {}", errorContext);
    }
}