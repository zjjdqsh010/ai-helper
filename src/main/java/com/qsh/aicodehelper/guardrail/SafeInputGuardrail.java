package com.qsh.aicodehelper.guardrail;

import org.springframework.stereotype.Component;

/**
 * 安全输入检查组件（安全护栏）
 *
 * 负责对用户输入进行安全验证和过滤，防止恶意输入和滥用
 * 实现输入长度限制、内容验证等安全措施
 *
 * 主要功能：
 * - 输入内容验证（空值、长度、格式）
 * - 防止过度请求（长度限制）
 * - 基础内容质量检查
 *
 * 技术关键点：
 * - @Component: 标记为Spring组件，可被自动扫描和注入
 * - 输入验证：防止空值和过短内容
 * - 长度限制：防止超大请求导致性能问题
 * - 可扩展性：为未来添加更多验证规则预留接口
 */
@Component
public class SafeInputGuardrail {

    /**
     * 验证用户输入的安全性
     *
     * 执行多层输入验证，确保输入内容安全且符合要求
     *
     * @param input 用户输入的内容
     * @return true表示输入安全，false表示输入不安全
     *
     * 验证规则：
     * 1. 非空检查：输入不能为null或空字符串
     * 2. 长度限制：输入长度不能超过10000字符（防止DoS攻击）
     * 3. 最小长度：输入至少需要3个字符（确保有实际内容）
     *
     * 技术关键点：
     * - trim(): 去除首尾空白字符后再验证
     * - length(): 检查字符长度，防止超大请求
     * - 可扩展：可添加更多验证规则（如敏感词过滤、格式验证等）
     */
    public boolean validateInput(String input) {
        // 基础输入验证：检查null和空字符串
        if (input == null || input.trim().isEmpty()) {
            return false;
        }

        // 检查消息长度：限制最大长度为10000字符
        // 防止超大请求导致的性能问题和潜在DoS攻击
        if (input.length() > 10000) {
            return false;
        }

        // 检查最小内容长度：至少需要3个字符
        // 确保用户输入有实际意义，过滤掉无意义的短输入
        if (input.trim().length() < 3) {
            return false;
        }

        // 所有验证通过，返回true表示输入安全
        return true;
    }
}