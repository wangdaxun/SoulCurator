package com.soulcurator.backend.repository.event;

import com.soulcurator.backend.model.event.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户事件埋点数据仓库
 */
@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
    
    /**
     * 根据会话ID查找事件记录
     */
    List<UserEvent> findBySessionId(String sessionId);
    
    /**
     * 根据用户ID查找事件记录
     */
    List<UserEvent> findByUserId(Long userId);
    
    /**
     * 根据事件类型查找事件记录
     */
    List<UserEvent> findByEventType(String eventType);
    
    /**
     * 根据会话ID和事件类型查找事件记录
     */
    List<UserEvent> findBySessionIdAndEventType(String sessionId, String eventType);
    
    /**
     * 根据时间范围查找事件记录
     */
    List<UserEvent> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 统计指定时间范围内的事件数量
     */
    @Query("SELECT COUNT(e) FROM UserEvent e WHERE e.createdAt BETWEEN :start AND :end")
    long countByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * 统计指定会话的事件数量
     */
    long countBySessionId(String sessionId);
    
    /**
     * 统计指定用户的事件数量
     */
    long countByUserId(Long userId);
    
    /**
     * 查找最近的事件记录
     */
    List<UserEvent> findTop10ByOrderByCreatedAtDesc();
    
    /**
     * 根据会话ID查找最新的事件记录
     */
    UserEvent findTopBySessionIdOrderByCreatedAtDesc(String sessionId);
}