package com.qsh.aicodehelper.repository;

import com.qsh.aicodehelper.entity.TokenUsageLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TokenUsageLogRepository extends JpaRepository<TokenUsageLog, Long> {

    // 根据用户ID查询
    Page<TokenUsageLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 根据用户名查询
    Page<TokenUsageLog> findByUsernameOrderByCreatedAtDesc(String username, Pageable pageable);

    // 根据使用目的查询
    Page<TokenUsageLog> findByUsagePurposeOrderByCreatedAtDesc(String usagePurpose, Pageable pageable);

    // 根据用户ID和使用目的查询
    Page<TokenUsageLog> findByUserIdAndUsagePurposeOrderByCreatedAtDesc(Long userId, String usagePurpose, Pageable pageable);

    // 根据时间范围查询
    Page<TokenUsageLog> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 根据用户ID和时间范围查询
    Page<TokenUsageLog> findByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // 统计用户的总请求数
    @Query("SELECT COUNT(l) FROM TokenUsageLog l WHERE l.userId = :userId")
    long countByUserId(@Param("userId") Long userId);

    // 统计用户的总Token消耗
    @Query("SELECT SUM(l.costTokens) FROM TokenUsageLog l WHERE l.userId = :userId")
    Integer sumTokensByUserId(@Param("userId") Long userId);

    // 统计用户的总费用
    @Query("SELECT SUM(l.costAmount) FROM TokenUsageLog l WHERE l.userId = :userId")
    java.math.BigDecimal sumCostByUserId(@Param("userId") Long userId);

    // 获取用户最近的使用记录
    @Query("SELECT l FROM TokenUsageLog l WHERE l.userId = :userId ORDER BY l.createdAt DESC")
    List<TokenUsageLog> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);

    // 统计各使用目的的调用次数
    @Query("SELECT l.usagePurpose, COUNT(l) FROM TokenUsageLog l WHERE l.userId = :userId GROUP BY l.usagePurpose")
    List<Object[]> countByUsagePurposeAndUserId(@Param("userId") Long userId);

    // 获取指定时间范围内的使用统计
    @Query("SELECT l.usagePurpose, COUNT(l), SUM(l.costTokens), SUM(l.costAmount) " +
           "FROM TokenUsageLog l WHERE l.userId = :userId AND l.createdAt BETWEEN :start AND :end " +
           "GROUP BY l.usagePurpose")
    List<Object[]> getUsageStatsByPeriod(@Param("userId") Long userId,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    // 删除指定日期之前的记录（用于数据清理）
    void deleteByCreatedAtBefore(LocalDateTime date);
}