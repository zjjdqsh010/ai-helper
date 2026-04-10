package com.qsh.aicodehelper.repository;

import com.qsh.aicodehelper.entity.TokenUsageStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenUsageStatsRepository extends JpaRepository<TokenUsageStats, Long> {

    // 根据用户ID查询
    List<TokenUsageStats> findByUserIdOrderByDateDesc(Long userId);

    // 根据用户ID和日期查询
    Optional<TokenUsageStats> findByUserIdAndDateAndUsagePurpose(Long userId, LocalDate date, String usagePurpose);

    // 根据用户ID和日期范围查询
    List<TokenUsageStats> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate start, LocalDate end);

    // 根据使用目的查询
    List<TokenUsageStats> findByUsagePurposeOrderByDateDesc(String usagePurpose);

    // 获取用户最新的统计数据
    List<TokenUsageStats> findTopByUserIdOrderByDateDesc(Long userId);

    // 统计用户的总请求数
    @Query("SELECT SUM(s.requestCount) FROM TokenUsageStats s WHERE s.userId = :userId")
    Long sumRequestCountByUserId(@Param("userId") Long userId);

    // 统计用户的总Token消耗
    @Query("SELECT SUM(s.totalTokens) FROM TokenUsageStats s WHERE s.userId = :userId")
    Integer sumTotalTokensByUserId(@Param("userId") Long userId);

    // 统计用户的总费用
    @Query("SELECT SUM(s.totalCost) FROM TokenUsageStats s WHERE s.userId = :userId")
    java.math.BigDecimal sumTotalCostByUserId(@Param("userId") Long userId);

    // 获取指定日期范围内的统计
    @Query("SELECT s FROM TokenUsageStats s WHERE s.userId = :userId AND s.date BETWEEN :start AND :end ORDER BY s.date DESC")
    List<TokenUsageStats> findByUserIdAndDateRange(@Param("userId") Long userId,
                                                  @Param("start") LocalDate start,
                                                  @Param("end") LocalDate end);

    // 删除指定日期之前的统计数据
    void deleteByDateBefore(LocalDate date);
}