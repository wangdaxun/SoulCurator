package com.soulcurator.backend.repository.session;

import com.soulcurator.backend.model.session.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户会话数据仓库
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    
    /**
     * 根据会话ID查找用户会话
     */
    Optional<UserSession> findBySessionId(String sessionId);
    
    /**
     * 根据用户ID查找用户会话
     */
    List<UserSession> findByUserId(Long userId);
    
    /**
     * 根据入口类型查找用户会话
     */
    List<UserSession> findByGatewayType(String gatewayType);
    
    /**
     * 根据是否完成查找用户会话
     */
    List<UserSession> findByIsCompleted(Boolean isCompleted);
    
    /**
     * 查找所有活跃会话（未完成）
     */
    List<UserSession> findByIsCompletedFalse();
    
    /**
     * 查找所有已完成会话
     */
    List<UserSession> findByIsCompletedTrue();
    
    /**
     * 根据开始时间范围查找用户会话
     */
    List<UserSession> findByStartedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据最后活跃时间查找用户会话
     */
    List<UserSession> findByLastActiveAtAfter(LocalDateTime lastActiveAt);
    
    /**
     * 查找用户的最新会话
     */
    Optional<UserSession> findTopByUserIdOrderByStartedAtDesc(Long userId);
    
    /**
     * 统计会话数量
     */
    long count();
    
    /**
     * 统计指定入口类型的会话数量
     */
    long countByGatewayType(String gatewayType);
    
    /**
     * 统计活跃会话数量
     */
    long countByIsCompletedFalse();
    
    /**
     * 统计已完成会话数量
     */
    long countByIsCompletedTrue();
    
    /**
     * 更新会话最后活跃时间
     */
    @Query("UPDATE UserSession us SET us.lastActiveAt = :lastActiveAt WHERE us.id = :sessionId")
    void updateLastActiveAt(@Param("sessionId") Long sessionId, @Param("lastActiveAt") LocalDateTime lastActiveAt);
    
    /**
     * 标记会话为已完成
     */
    @Query("UPDATE UserSession us SET us.isCompleted = true, us.endedAt = :endedAt WHERE us.id = :sessionId")
    void markAsCompleted(@Param("sessionId") Long sessionId, @Param("endedAt") LocalDateTime endedAt);
    
    /**
     * 更新会话完成的问题数量
     */
    @Query("UPDATE UserSession us SET us.completedQuestions = :completedQuestions WHERE us.id = :sessionId")
    void updateCompletedQuestions(@Param("sessionId") Long sessionId, @Param("completedQuestions") Integer completedQuestions);
    
    /**
     * 检查会话是否存在
     */
    boolean existsBySessionId(String sessionId);
}