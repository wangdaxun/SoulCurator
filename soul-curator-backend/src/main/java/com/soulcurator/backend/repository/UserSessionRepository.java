package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户会话数据访问接口
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    /**
     * 根据用户ID查找会话
     */
    List<UserSession> findByUserId(Long userId);
    
    /**
     * 根据会话令牌查找会话
     */
    Optional<UserSession> findBySessionToken(String sessionToken);
    
    /**
     * 查找活跃会话
     */
    List<UserSession> findByIsActiveTrue();
    
    /**
     * 查找用户的活跃会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.user.id = :userId AND s.isActive = true")
    List<UserSession> findActiveSessionsByUserId(@Param("userId") Long userId);
    
    /**
     * 查找已过期的会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.expiresAt < :now")
    List<UserSession> findExpiredSessions(@Param("now") LocalDateTime now);
    
    /**
     * 查找即将过期的会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.expiresAt BETWEEN :now AND :threshold")
    List<UserSession> findSessionsExpiringSoon(
            @Param("now") LocalDateTime now,
            @Param("threshold") LocalDateTime threshold);
    
    /**
     * 根据用户ID查找最新的会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.user.id = :userId ORDER BY s.createdAt DESC LIMIT 1")
    Optional<UserSession> findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户的会话数量
     */
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计活跃会话数量
     */
    @Query("SELECT COUNT(s) FROM UserSession s WHERE s.isActive = true")
    long countActiveSessions();
    
    /**
     * 查找指定时间范围内创建的会话
     */
    List<UserSession> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 查找指定时间范围内最后活跃的会话
     */
    List<UserSession> findByLastActiveAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据设备类型查找会话
     */
    List<UserSession> findByDeviceType(String deviceType);
    
    /**
     * 根据用户代理查找会话
     */
    @Query("SELECT s FROM UserSession s WHERE LOWER(s.userAgent) LIKE LOWER(CONCAT('%', :userAgent, '%'))")
    List<UserSession> findByUserAgentContaining(@Param("userAgent") String userAgent);
    
    /**
     * 查找长时间未活跃的会话
     */
    @Query("SELECT s FROM UserSession s WHERE s.lastActiveAt < :threshold AND s.isActive = true")
    List<UserSession> findInactiveSessions(@Param("threshold") LocalDateTime threshold);
    
    /**
     * 更新会话的最后活跃时间
     */
    @Query("UPDATE UserSession s SET s.lastActiveAt = :lastActiveAt WHERE s.id = :sessionId")
    void updateLastActiveAt(@Param("sessionId") Long sessionId, @Param("lastActiveAt") LocalDateTime lastActiveAt);
    
    /**
     * 使会话失效
     */
    @Query("UPDATE UserSession s SET s.isActive = false, s.expiresAt = :expiredAt WHERE s.id = :sessionId")
    void deactivateSession(@Param("sessionId") Long sessionId, @Param("expiredAt") LocalDateTime expiredAt);
    
    /**
     * 使用户的所有会话失效
     */
    @Query("UPDATE UserSession s SET s.isActive = false, s.expiresAt = :expiredAt WHERE s.user.id = :userId AND s.isActive = true")
    void deactivateAllSessionsByUser(@Param("userId") Long userId, @Param("expiredAt") LocalDateTime expiredAt);
    
    /**
     * 检查会话令牌是否存在
     */
    boolean existsBySessionToken(String sessionToken);
}