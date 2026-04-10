package com.qsh.aicodehelper.repository;

import com.qsh.aicodehelper.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastLoginAt = :lastLoginAt WHERE u.id = :userId")
    void updateLastLoginTime(@Param("userId") Long userId, @Param("lastLoginAt") LocalDateTime lastLoginAt);
}