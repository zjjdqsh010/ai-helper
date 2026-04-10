package com.qsh.aicodehelper.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "token_usage_stats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsageStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "usage_purpose", nullable = false, length = 100)
    private String usagePurpose;

    @Column(name = "request_count", columnDefinition = "INTEGER DEFAULT 0")
    private Integer requestCount = 0;

    @Column(name = "total_tokens", columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalTokens = 0;

    @Column(name = "total_cost", precision = 10, scale = 4)
    private BigDecimal totalCost = BigDecimal.ZERO;

    @Column(name = "avg_response_time", columnDefinition = "INTEGER DEFAULT 0")
    private Integer avgResponseTime = 0;

    @Column(name = "success_rate", precision = 5, scale = 2)
    private BigDecimal successRate = BigDecimal.ZERO;

    // 构造函数
    public TokenUsageStats(Long userId, String username, LocalDate date, String usagePurpose) {
        this.userId = userId;
        this.username = username;
        this.date = date;
        this.usagePurpose = usagePurpose;
    }

    // 增加使用记录
    public void addUsage(Integer tokens, BigDecimal cost, Integer responseTime, boolean success) {
        this.requestCount++;
        this.totalTokens += tokens;
        this.totalCost = this.totalCost.add(cost);
        this.avgResponseTime = (this.avgResponseTime * (this.requestCount - 1) + responseTime) / this.requestCount;

        // 重新计算成功率
        long successCount = (long) (this.successRate.doubleValue() * (this.requestCount - 1) / 100);
        if (success) successCount++;
        this.successRate = BigDecimal.valueOf(successCount * 100.0 / this.requestCount);
    }

    // 获取使用目的显示名称
    public String getUsagePurposeDisplayName() {
        switch (usagePurpose) {
            case "chat": return "智能对话";
            case "code_review": return "代码审查";
            case "debug": return "调试助手";
            case "optimize": return "代码优化";
            case "explain": return "代码解释";
            default: return usagePurpose;
        }
    }

    // 获取平均响应时间显示
    public String getAvgResponseTimeDisplay() {
        if (avgResponseTime < 1000) {
            return avgResponseTime + "ms";
        } else {
            return String.format("%.1fs", avgResponseTime / 1000.0);
        }
    }
}