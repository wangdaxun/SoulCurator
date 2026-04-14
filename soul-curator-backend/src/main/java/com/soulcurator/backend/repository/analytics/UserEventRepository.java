package com.soulcurator.backend.repository.analytics;

import com.soulcurator.backend.model.analytics.UserEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户事件埋点数据访问接口
 */
@Repository
public interface UserEventRepository extends JpaRepository<UserEvent, Long> {
    
    /**
     * 根据会话ID查找事件
     */
    List<UserEvent> findBySessionId(String sessionId);
    
    /**
     * 根据会话ID和事件类型查找事件
     */
    List<UserEvent> findBySessionIdAndEventType(String sessionId, String eventType);
    
    /**
     * 根据用户ID查找事件
     */
    List<UserEvent> findByUserId(Long userId);
    
    /**
     * 统计指定时间段内的事件数量
     */
    @Query("SELECT COUNT(e) FROM UserEvent e WHERE e.createdAt BETWEEN :start AND :end")
    Long countByCreatedAtBetween(@Param("start") LocalDateTime start, 
                                 @Param("end") LocalDateTime end);
    
    /**
     * 按事件类型分组统计
     */
    @Query("SELECT e.eventType, COUNT(e) FROM UserEvent e WHERE e.createdAt BETWEEN :start AND :end GROUP BY e.eventType")
    List<Object[]> countByEventTypeGroup(@Param("start") LocalDateTime start, 
                                         @Param("end") LocalDateTime end);
    
    /**
     * 按设备类型分组统计
     */
    @Query("SELECT e.deviceType, COUNT(e) FROM UserEvent e WHERE e.createdAt BETWEEN :start AND :end GROUP BY e.deviceType")
    List<Object[]> countByDeviceTypeGroup(@Param("start") LocalDateTime start, 
                                          @Param("end") LocalDateTime end);
    
    /**
     * 查找指定会话的最新事件
     */
    @Query("SELECT e FROM UserEvent e WHERE e.sessionId = :sessionId ORDER BY e.createdAt DESC")
    List<UserEvent> findLatestBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 统计每个会话的事件数量
     */
    @Query("SELECT e.sessionId, COUNT(e) FROM UserEvent e WHERE e.createdAt BETWEEN :start AND :end GROUP BY e.sessionId")
    List<Object[]> countEventsPerSession(@Param("start") LocalDateTime start, 
                                         @Param("end") LocalDateTime end);
    
    /**
     * 查找指定事件名称的事件
     */
    List<UserEvent> findByEventName(String eventName);
    
    /**
     * 查找指定事件类型和名称的事件
     */
    List<UserEvent> findByEventTypeAndEventName(String eventType, String eventName);
    
    /**
     * 删除指定时间之前的事件（用于数据清理）
     */
    @Query("DELETE FROM UserEvent e WHERE e.createdAt < :beforeDate")
    void deleteEventsBefore(@Param("beforeDate") LocalDateTime beforeDate);
    
    /**
     * 获取事件类型分布（返回Map格式）
     */
    @Query("SELECT new map(e.eventType as type, COUNT(e) as count) FROM UserEvent e WHERE e.createdAt BETWEEN :start AND :end GROUP BY e.eventType")
    List<Map<String, Object>> getEventTypeDistribution(@Param("start") LocalDateTime start, 
                                                       @Param("end") LocalDateTime end);
    
    /**
     * 获取热门页面访问统计
     */
    @Query("SELECT e.pageUrl, COUNT(e) as visitCount FROM UserEvent e WHERE e.eventType = 'page_view' AND e.createdAt BETWEEN :start AND :end GROUP BY e.pageUrl ORDER BY visitCount DESC")
    List<Object[]> getPopularPages(@Param("start") LocalDateTime start, 
                                   @Param("end") LocalDateTime end);
    
    /**
     * 获取用户行为序列（按时间排序）
     */
    @Query("SELECT e FROM UserEvent e WHERE e.sessionId = :sessionId ORDER BY e.createdAt ASC")
    List<UserEvent> getUserBehaviorSequence(@Param("sessionId") String sessionId);
    
    /**
     * 检查会话是否完成（是否有画像生成事件）
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM UserEvent e WHERE e.sessionId = :sessionId AND e.eventType = 'portrait_generated'")
    boolean isSessionCompleted(@Param("sessionId") String sessionId);
    
    /**
     * 获取会话的平均完成时间
     */
    @Query("""
        SELECT AVG(EXTRACT(EPOCH FROM (portrait.createdAt - firstPage.createdAt))) 
        FROM UserEvent portrait
        JOIN UserEvent firstPage ON portrait.sessionId = firstPage.sessionId
        WHERE portrait.eventType = 'portrait_generated'
        AND firstPage.eventType = 'page_view'
        AND firstPage.eventName LIKE '%entered'
        AND portrait.createdAt BETWEEN :start AND :end
        AND firstPage.createdAt BETWEEN :start AND :end
    """)
    Double getAverageCompletionTime(@Param("start") LocalDateTime start, 
                                    @Param("end") LocalDateTime end);
}