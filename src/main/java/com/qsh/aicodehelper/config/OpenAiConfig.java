package com.qsh.aicodehelper.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAI Chat Model 配置类
 *
 * 负责配置和创建OpenAI聊天模型实例
 * 支持OpenAI官方API和LongCat服务的集成
 *
 * 主要功能：
 * - 从配置文件读取OpenAI相关配置参数
 * - 创建并配置ChatModel Bean供其他组件使用
 * - 支持自定义API端点（用于LongCat集成）
 *
 * 技术关键点：
 * - @Configuration: 标记为Spring配置类
 * - @ConfigurationProperties: 绑定配置文件中的属性到Java对象
 * - @Data: Lombok注解，自动生成getter/setter等方法
 * - @Resource: 依赖注入ChatModelListener
 * - @Bean: 将方法返回值注册为Spring Bean
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.openai.chat-model")
@Data
public class OpenAiConfig {

    /**
     * AI模型名称，如 gpt-4-turbo, gpt-3.5-turbo
     * 通过配置文件langchain4j.openai.chat-model.model-name设置
     */
    private String modelName;

    /**
     * OpenAI API密钥
     * 通过配置文件langchain4j.openai.chat-model.api-key设置
     * 建议通过环境变量OPENAI_API_KEY传入
     */
    private String apiKey;

    /**
     * API基础URL
     * 支持自定义端点，用于LongCat服务集成
     * 通过配置文件langchain4j.openai.chat-model.base-url设置
     */
    private String baseUrl;

    /**
     * 温度参数，控制回答的随机性
     * 范围0.0-2.0，值越高回答越随机，值越低回答越确定
     * 通过配置文件langchain4j.openai.chat-model.temperature设置
     */
    private Double temperature;

    /**
     * 最大token数量限制
     * 控制API响应的最大长度
     * 通过配置文件langchain4j.openai.chat-model.max-tokens设置
     */
    private Integer maxTokens;

    /**
     * ChatModel监听器，用于监控和记录模型调用
     * 通过Spring依赖注入
     */
    @Resource
    private ChatModelListener chatModelListener;

    /**
     * 创建OpenAI Chat Model Bean
     *
     * @return 配置好的ChatModel实例
     *
     * 技术关键点：
     * - 使用OpenAiChatModel.builder()创建模型实例
     * - 支持空值处理，提供默认值
     * - 配置监听器用于监控模型调用
     */
    @Bean
    public ChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl) // LongCat endpoint
                .temperature(temperature != null ? temperature : 0.7)
                .maxTokens(maxTokens != null ? maxTokens : 1000)
                .build();
    }
}