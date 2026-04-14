package com.soulcurator.backend.service.analytics;

import com.soulcurator.backend.dto.analytics.EventRequest;
import com.soulcurator.backend.dto.analytics.EventResponse;
import com.soulcurator.backend.model.analytics.UserEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 分析服务接口
 * 提供用户行为埋点和数据分析功能
 */
public interface AnalyticsService {
    
    /**
     * 记录用户事件
     * 
     * @param request 事件请求DTO
     * @return 事件响应DTO
     */
    EventResponse recordEvent(EventRequest request);
    
    /**
     * 批量记录用户事件
     * 
     * @param requests 事件请求DTO列表
     * @return 批量处理结果
     */
    List<EventResponse> recordEvents(List<EventRequest> requests);
    
    /**
     * 根据事件ID获取事件详情
     * 
     * @param eventId 事件ID
     * @return 用户事件实体
     */
    UserEvent getEventById(Long eventId);
    
    /**
     * 根据会话ID获取事件列表
     * 
     * @param sessionId 会话ID
     * @return 事件列表
     */
    List<UserEvent> getEventsBySessionId(String sessionId);
    
    /**
     * 根据用户ID获取事件列表
     * 
     * @param userId 用户ID
     * @return 事件列表
     */
    List<UserEvent> getEventsByUserId(Long userId);
    
    /**
     * 根据事件类型获取事件列表
     * 
     * @param eventType 事件类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件列表
     */
    List<UserEvent> getEventsByType(String eventType, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 统计指定时间段内的事件数量
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件数量
     */
    Long countEvents(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 按事件类型分组统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 事件类型统计结果
     */
    Map<String, Long> countEventsByType(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 按设备类型分组统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 设备类型统计结果
     */
    Map<String, Long> countEventsByDeviceType(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取热门页面访问统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 返回数量限制
     * @return 页面访问统计
     */
    Map<String, Long> getPopularPages(LocalDateTime startTime, LocalDateTime endTime, int limit);
    
    /**
     * 获取用户行为序列
     * 
     * @param sessionId 会话ID
     * @return 按时间排序的事件序列
     */
    List<UserEvent> getUserBehaviorSequence(String sessionId);
    
    /**
     * 检查会话是否完成（是否有画像生成事件）
     * 
     * @param sessionId 会话ID
     * @return 是否完成
     */
    boolean isSessionCompleted(String sessionId);
    
    /**
     * 获取会话的平均完成时间
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 平均完成时间（秒）
     */
    Double getAverageCompletionTime(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取活跃会话数量
     * 
     * @param timeWindow 时间窗口（分钟）
     * @return 活跃会话数量
     */
    Long getActiveSessionsCount(int timeWindow);
    
    /**
     * 获取用户留存率
     * 
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 留存率数据
     */
    Map<String, Double> getUserRetentionRates(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * 清理过期的事件数据
     * 
     * @param daysToKeep 保留天数
     * @return 清理的事件数量
     */
    Long cleanupOldEvents(int daysToKeep);
    
    /**
     * 导出事件数据
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式（csv, json）
     * @return 导出数据
     */
    String exportEvents(LocalDateTime startTime, LocalDateTime endTime, String format);
    
    /**
     * 获取服务健康状态
     * 
     * @return 健康状态信息
     */
    Map<String, Object> getHealthStatus();
    
    /**
     * 获取服务统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getServiceStats();
}