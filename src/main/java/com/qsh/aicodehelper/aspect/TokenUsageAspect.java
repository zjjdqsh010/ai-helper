package com.qsh.aicodehelper.aspect;

import com.qsh.aicodehelper.service.TokenUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenUsageAspect {

    private final TokenUsageService tokenUsageService;

    /**
     * 拦截所有AI助手API的调用
     */
    @Around("execution(* com.qsh.aicodehelper.controller.AiCodeHelperController.*(..))")
    public Object recordTokenUsage(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = null;
        try {
            // 获取HTTP请求
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                request = attributes.getRequest();
            }

            // 获取请求信息
            String requestURI = request != null ? request.getRequestURI() : "unknown";
            String method = request != null ? request.getMethod() : "UNKNOWN";
            String ipAddress = getClientIpAddress(request);
            String userAgent = request != null ? request.getHeader("User-Agent") : null;

            // 从请求头中获取Token
            String token = null;
            if (request != null) {
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            // 确定使用目的
            String usagePurpose = determineUsagePurpose(requestURI);

            // 获取请求参数和body
            Object[] args = joinPoint.getArgs();
            String requestParams = args.length > 0 ? args[0].toString() : null;

            // 记录使用开始
            Long logId = null;
            String username = "unknown";
            Long userId = 1L; // 默认用户ID，实际应该从认证信息中获取

            if (token != null) {
                try {
                    // 简单地从Token中提取用户名（简化版）
                    username = extractUsernameFromToken(token);
                    userId = 1L; // 暂时使用默认用户ID

                    com.qsh.aicodehelper.entity.TokenUsageLog usageLog = tokenUsageService.recordUsage(
                            userId, username, requestURI, method, requestParams, null,
                            usagePurpose, ipAddress, userAgent, null
                    );
                    if (usageLog != null) {
                        logId = usageLog.getId();
                    }
                } catch (Exception e) {
                    log.error("记录Token使用开始失败", e);
                }
            }

            // 执行实际的业务方法
            long startTime = System.currentTimeMillis();
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            int responseTime = (int) (endTime - startTime);

            // 更新使用记录
            if (logId != null) {
                try {
                    // 估算Token消耗
                    int estimatedTokens = estimateTokens(result);

                    // 创建使用记录并保存
                    com.qsh.aicodehelper.entity.TokenUsageLog usageLog = new com.qsh.aicodehelper.entity.TokenUsageLog(
                            userId, username, token, requestURI, method, usagePurpose
                    );
                    usageLog.setRequestParams(requestParams);
                    usageLog.setIpAddress(ipAddress);
                    usageLog.setUserAgent(userAgent);
                    usageLog.updateResponseInfo(200, responseTime, estimatedTokens,
                            new java.math.BigDecimal(estimatedTokens * 0.0001)); // 简单费用计算

                    tokenUsageService.saveUsageLog(usageLog);

                    log.info("API调用完成: {} - 响应时间: {}ms, 估算Token: {}",
                            requestURI, responseTime, estimatedTokens);
                } catch (Exception e) {
                    log.error("更新Token使用记录失败", e);
                }
            }

            return result;

        } catch (Throwable throwable) {
            // 处理异常情况
            log.error("Token使用记录切面异常", throwable);
            throw throwable;
        }
    }

    /**
     * 确定使用目的
     */
    private String determineUsagePurpose(String requestURI) {
        if (requestURI.contains("/chat")) {
            return "chat";
        } else if (requestURI.contains("/code-review")) {
            return "code_review";
        } else if (requestURI.contains("/debug")) {
            return "debug";
        } else if (requestURI.contains("/optimize")) {
            return "optimize";
        } else if (requestURI.contains("/explain")) {
            return "explain";
        }
        return "unknown";
    }

    /**
     * 估算Token消耗
     */
    private int estimateTokens(Object result) {
        if (result == null) {
            return 100;
        }

        try {
            String resultStr = result.toString();
            // 简单估算：每个字符约等于0.25个token
            return Math.max(50, (int) (resultStr.length() * 0.25));
        } catch (Exception e) {
            return 100; // 默认值
        }
    }

    /**
     * 从Token中提取用户名（简化版）
     */
    private String extractUsernameFromToken(String token) {
        try {
            // 简化的Token解析，实际应该使用JwtUtil
            // 这里假设Token中包含用户名信息
            if (token.length() > 20) {
                return "user_" + token.substring(10, 15);
            }
            return "anonymous";
        } catch (Exception e) {
            return "unknown";
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress != null ? ipAddress.split(",")[0] : "unknown";
    }
}