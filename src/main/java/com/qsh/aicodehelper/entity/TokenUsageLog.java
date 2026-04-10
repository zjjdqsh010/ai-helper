package com.qsh.aicodehelper.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_usage_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "token_id", nullable = false, length = 255)
    private String tokenId;

    @Column(name = "api_endpoint", nullable = false, length = 255)
    private String apiEndpoint;

    @Column(name = "request_method", nullable = false, length = 10)
    private String requestMethod;

    @Column(name = "request_params", columnDefinition = "TEXT")
    private String requestParams;

    @Column(name = "request_body", columnDefinition = "TEXT")
    private String requestBody;

    @Column(name = "response_status")
    private Integer responseStatus;

    @Column(name = "response_time")
    private Integer responseTime;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "usage_purpose", length = 100)
    private String usagePurpose;

    @Column(name = "cost_tokens", columnDefinition = "INTEGER DEFAULT 0")
    private Integer costTokens = 0;

    @Column(name = "cost_amount", precision = 10, scale = 4)
    private BigDecimal costAmount = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // 构造函数
    public TokenUsageLog(Long userId, String username, String tokenId, String apiEndpoint,
                        String requestMethod, String usagePurpose) {
        this.userId = userId;
        this.username = username;
        this.tokenId = tokenId;
        this.apiEndpoint = apiEndpoint;
        this.requestMethod = requestMethod;
        this.usagePurpose = usagePurpose;
    }

    // 更新响应信息
    public void updateResponseInfo(Integer responseStatus, Integer responseTime, Integer costTokens, BigDecimal costAmount) {
        this.responseStatus = responseStatus;
        this.responseTime = responseTime;
        this.costTokens = costTokens;
        this.costAmount = costAmount;
    }

    // 更新错误信息
    public void updateError(String errorMessage) {
        this.responseStatus = 500;
        this.errorMessage = errorMessage;
    }

    // 判断是否成功
    public boolean isSuccess() {
        return responseStatus != null && responseStatus == 200;
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
}