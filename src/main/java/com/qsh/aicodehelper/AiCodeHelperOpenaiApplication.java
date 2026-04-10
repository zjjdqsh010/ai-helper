package com.qsh.aicodehelper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI代码助手主应用类
 *
 * 基于Spring Boot 3.2.0的AI编程助手应用
 * 集成OpenAI GPT模型和LongCat服务，提供代码审查、调试、优化等功能
 *
 * 技术关键点：
 * - @SpringBootApplication: Spring Boot自动配置注解，包含@Configuration、@EnableAutoConfiguration、@ComponentScan
 * - SpringApplication.run(): 启动Spring Boot应用的标准方法
 */
@SpringBootApplication
public class AiCodeHelperOpenaiApplication {

    /**
     * 应用主入口方法
     *
     * @param args 命令行参数
     *
     * 技术关键点：
     * - 标准的Java main方法，Spring Boot应用的启动入口
     * - SpringApplication.run()会创建应用上下文并启动内嵌的Web服务器（默认Tomcat）
     */
    public static void main(String[] args) {
        SpringApplication.run(AiCodeHelperOpenaiApplication.class, args);
    }
}