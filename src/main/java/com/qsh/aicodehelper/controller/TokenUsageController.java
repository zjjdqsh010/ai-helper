package com.qsh.aicodehelper.controller;

import com.qsh.aicodehelper.entity.TokenUsageLog;
import com.qsh.aicodehelper.service.TokenUsageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class TokenUsageController {

    private final TokenUsageService tokenUsageService;

    /**
     * 获取用户的使用记录
     */
    @GetMapping("/logs")
    public ResponseEntity<Page<TokenUsageLog>> getUserUsageLogs(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort) {

        try {
            Sort.Direction direction = Sort.Direction.DESC;
            String sortBy = "createdAt";

            if (sort.length >= 2 && "asc".equalsIgnoreCase(sort[1])) {
                direction = Sort.Direction.ASC;
            }
            if (sort.length >= 1) {
                sortBy = sort[0];
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<TokenUsageLog> logs = tokenUsageService.getUserUsageLogs(userId, pageable);

            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("获取用户使用记录失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取用户的使用统计
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserUsageStats(@RequestParam Long userId) {
        try {
            Map<String, Object> stats = tokenUsageService.getUserUsageStats(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取用户使用统计失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取最近的使用记录
     */
    @GetMapping("/recent")
    public ResponseEntity<Page<TokenUsageLog>> getRecentUsageLogs(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<TokenUsageLog> logs = tokenUsageService.getRecentUsageLogs(userId, page, size);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            log.error("获取最近使用记录失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 获取使用概览
     */
    @GetMapping("/overview")
    public ResponseEntity<Map<String, Object>> getUsageOverview(@RequestParam Long userId) {
        try {
            Map<String, Object> stats = tokenUsageService.getUserUsageStats(userId);

            // 添加一些额外的概览信息
            Map<String, Object> overview = Map.of(
                "totalRequests", stats.get("totalRequests"),
                "totalTokens", stats.get("totalTokens"),
                "totalCost", stats.get("totalCost"),
                "purposeStats", stats.get("purposeStats")
            );

            return ResponseEntity.ok(overview);
        } catch (Exception e) {
            log.error("获取使用概览失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}