package com.qsh.aicodehelper.service;

import com.qsh.aicodehelper.entity.TokenUsageLog;
import com.qsh.aicodehelper.entity.TokenUsageStats;
import com.qsh.aicodehelper.repository.TokenUsageLogRepository;
import com.qsh.aicodehelper.repository.TokenUsageStatsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TokenUsageService {

    private final TokenUsageLogRepository logRepository;
    private final TokenUsageStatsRepository statsRepository;

    // Token费用配置（每1000个token的费用，单位：元）
    private static final BigDecimal COST_PER_1K_TOKENS = new BigDecimal("0.1");

    public TokenUsageService(TokenUsageLogRepository logRepository,
                           TokenUsageStatsRepository statsRepository) {
        this.logRepository = logRepository;
        this.statsRepository = statsRepository;
    }

    /**
     * 获取用户的使用记录
     */
    public Page<TokenUsageLog> getUserUsageLogs(Long userId, Pageable pageable) {
        return logRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 获取用户的使用统计
     */
    public Map<String, Object> getUserUsageStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();

        // 基本统计
        stats.put("totalRequests", logRepository.countByUserId(userId));
        stats.put("totalTokens", logRepository.sumTokensByUserId(userId) != null ?
                                logRepository.sumTokensByUserId(userId) : 0);
        stats.put("totalCost", logRepository.sumCostByUserId(userId) != null ?
                              logRepository.sumCostByUserId(userId) : BigDecimal.ZERO);

        // 按使用目的统计
        var purposeStats = logRepository.countByUsagePurposeAndUserId(userId);
        Map<String, Long> purposeCount = new HashMap<>();
        for (Object[] stat : purposeStats) {
            purposeCount.put((String) stat[0], (Long) stat[1]);
        }
        stats.put("purposeStats", purposeCount);

        return stats;
    }

    /**
     * 获取用户最近的使用记录
     */
    public Page<TokenUsageLog> getRecentUsageLogs(Long userId, int page, int size) {
        return logRepository.findByUserIdOrderByCreatedAtDesc(userId,
                org.springframework.data.domain.PageRequest.of(page, size));
    }

    /**
     * 记录Token使用情况
     */
    @Transactional
    public TokenUsageLog recordUsage(Long userId, String username, String apiEndpoint, String requestMethod,
                                   String requestParams, String requestBody, String usagePurpose,
                                   String ipAddress, String userAgent, Integer actualCostTokens) {
        try {
            if (userId == null) {
                log.warn("用户ID不能为空");
                return null;
            }

            // 计算Token消耗和费用
            int costTokens = actualCostTokens != null ? actualCostTokens : 100; // 简化计算
            BigDecimal costAmount = new BigDecimal(costTokens * 0.0001);

            // 创建使用记录
            TokenUsageLog usageLog = new TokenUsageLog(userId, username, "token-" + System.currentTimeMillis(), apiEndpoint, requestMethod, usagePurpose);
            usageLog.setRequestParams(requestParams);
            usageLog.setRequestBody(requestBody != null && requestBody.length() > 65535 ?
                             requestBody.substring(0, 65535) : requestBody);
            usageLog.setIpAddress(ipAddress);
            usageLog.setUserAgent(userAgent);
            usageLog.setCostTokens(costTokens);
            usageLog.setCostAmount(costAmount);

            // 保存记录
            TokenUsageLog savedLog = logRepository.save(usageLog);

            log.info("Token使用记录已保存: 用户={}, 接口={}, 消耗token={}, 费用={}",
                    username, apiEndpoint, costTokens, costAmount);

            return savedLog;
        } catch (Exception e) {
            log.error("记录Token使用情况失败", e);
            return null;
        }
    }

    /**
     * 保存使用记录
     */
    @Transactional
    public void saveUsageLog(TokenUsageLog usageLog) {
        try {
            logRepository.save(usageLog);
            log.info("使用记录已保存: ID={}", usageLog.getId());
        } catch (Exception e) {
            log.error("保存使用记录失败", e);
        }
    }

    /**
     * 清理旧数据
     */
    @Transactional
    public void cleanupOldData(int daysToKeep) {
        var cutoffDate = java.time.LocalDateTime.now().minusDays(daysToKeep);
        var cutoffLocalDate = java.time.LocalDate.now().minusDays(daysToKeep);

        logRepository.deleteByCreatedAtBefore(cutoffDate);
        statsRepository.deleteByDateBefore(cutoffLocalDate);

        log.info("已清理{}天前的使用记录数据", daysToKeep);
    }
}