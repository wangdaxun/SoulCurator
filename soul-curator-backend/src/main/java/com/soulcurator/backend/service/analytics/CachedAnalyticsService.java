package com.soulcurator.backend.service.analytics;

import com.soulcurator.backend.dto.analytics.EventRequest;
import com.soulcurator.backend.dto.analytics.EventResponse;
import com.soulcurator.backend.model.analytics.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 带缓存的分析服务
 * 包装AnalyticsServiceImpl，添加缓存层
 */
@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class CachedAnalyticsService implements AnalyticsService {
    
    private final AnalyticsServiceImpl delegate;
    
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "eventStats", allEntries = true),
            @CacheEvict(value = "popularPages", allEntries = true),
            @CacheEvict(value = "analyticsHealth", allEntries = true),
            @CacheEvict(value = "deviceStats", allEntries = true),
            @CacheEvict(cacheNames = "userSessions", key = "#result?.sessionId", condition = "#result?.success == true")
        }
    )
    public EventResponse recordEvent(EventRequest request) {
        return delegate.recordEvent(request);
    }
    
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "eventStats", allEntries = true),
            @CacheEvict(value = "popularPages", allEntries = true),
            @CacheEvict(value = "analyticsHealth", allEntries = true),
            @CacheEvict(value = "deviceStats", allEntries = true)
        }
    )
    public List<EventResponse> recordEvents(List<EventRequest> requests) {
        return delegate.recordEvents(requests);
    }
    
    @Override
    @Cacheable(value = "userSessions", key = "#eventId", unless = "#result == null")
    public UserEvent getEventById(Long eventId) {
        return delegate.getEventById(eventId);
    }
    
    @Override
    @Cacheable(value = "userSessions", key = "'session:' + #sessionId", unless = "#result == null or #result.isEmpty()")
    public List<UserEvent> getEventsBySessionId(String sessionId) {
        return delegate.getEventsBySessionId(sessionId);
    }
    
    @Override
    @Cacheable(value = "userSessions", key = "'user:' + #userId", unless = "#result == null or #result.isEmpty()")
    public List<UserEvent> getEventsByUserId(Long userId) {
        return delegate.getEventsByUserId(userId);
    }
    
    @Override
    @Cacheable(value = "eventStats", key = "'type:' + #eventType + ':' + #startTime.toString() + ':' + #endTime.toString()", 
               unless = "#result == null or #result.isEmpty()")
    public List<UserEvent> getEventsByType(String eventType, LocalDateTime startTime, LocalDateTime endTime) {
        return delegate.getEventsByType(eventType, startTime, endTime);
    }
    
    @Override
    @Cacheable(value = "eventStats", key = "'count:' + #startTime.toString() + ':' + #endTime.toString()", unless = "#result == null")
    public Long countEvents(LocalDateTime startTime, LocalDateTime endTime) {
        return delegate.countEvents(startTime, endTime);
    }
    
    @Override
    @Cacheable(value = "eventStats", key = "'types:' + #startTime.toString() + ':' + #endTime.toString()", 
               unless = "#result == null or #result.isEmpty()")
    public Map<String, Long> countEventsByType(LocalDateTime startTime, LocalDateTime endTime) {
        return delegate.countEventsByType(startTime, endTime);
    }
    
    @Override
    @Cacheable(value = "deviceStats", key = "#startTime.toString() + ':' + #endTime.toString()", 
               unless = "#result == null or #result.isEmpty()")
    public Map<String, Long> countEventsByDeviceType(LocalDateTime startTime, LocalDateTime endTime) {
        return delegate.countEventsByDeviceType(startTime, endTime);
    }
    
    @Override
    @Cacheable(value = "popularPages", key = "#startTime.toString() + ':' + #endTime.toString() + ':' + #limit", 
               unless = "#result == null or #result.isEmpty()")
    public Map<String, Long> getPopularPages(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        return delegate.getPopularPages(startTime, endTime, limit);
    }
    
    @Override
    @Cacheable(value = "userSessions", key = "'sequence:' + #sessionId", unless = "#result == null or #result.isEmpty()")
    public List<UserEvent> getUserBehaviorSequence(String sessionId) {
        return delegate.getUserBehaviorSequence(sessionId);
    }
    
    @Override
    @Cacheable(value = "userSessions", key = "'completed:' + #sessionId")
    public boolean isSessionCompleted(String sessionId) {
        return delegate.isSessionCompleted(sessionId);
    }
    
    @Override
    @Cacheable(value = "eventStats", key = "'avgCompletion:' + #startTime.toString() + ':' + #endTime.toString()", 
               unless = "#result == null")
    public Double getAverageCompletionTime(LocalDateTime startTime, LocalDateTime endTime) {
        return delegate.getAverageCompletionTime(startTime, endTime);
    }
    
    @Override
    @Cacheable(value = "eventStats", key = "'activeSessions:' + #timeWindow", unless = "#result == null")
    public Long getActiveSessionsCount(int timeWindow) {
        return delegate.getActiveSessionsCount(timeWindow);
    }
    
    @Override
    @Cacheable(value = "retentionRates", key = "#startDate.toString() + ':' + #endDate.toString()", 
               unless = "#result == null or #result.isEmpty()")
    public Map<String, Double> getUserRetentionRates(LocalDateTime startDate, LocalDateTime endDate) {
        return delegate.getUserRetentionRates(startDate, endDate);
    }
    
    @Override
    @Caching(
        evict = {
            @CacheEvict(value = "eventStats", allEntries = true),
            @CacheEvict(value = "popularPages", allEntries = true),
            @CacheEvict(value = "deviceStats", allEntries = true),
            @CacheEvict(value = "retentionRates", allEntries = true)
        }
    )
    public Long cleanupOldEvents(int daysToKeep) {
        return delegate.cleanupOldEvents(daysToKeep);
    }
    
    @Override
    public String exportEvents(LocalDateTime startTime, LocalDateTime endTime, String format) {
        // 导出操作通常不需要缓存，因为数据可能很大且实时性要求高
        return delegate.exportEvents(startTime, endTime, format);
    }
    
    @Override
    @Cacheable(value = "analyticsHealth", key = "'status'", unless = "#result == null")
    public Map<String, Object> getHealthStatus() {
        return delegate.getHealthStatus();
    }
    
    @Override
    @Cacheable(value = "eventStats", key = "'serviceStats'", unless = "#result == null")
    public Map<String, Object> getServiceStats() {
        return delegate.getServiceStats();
    }
    
    /**
     * 手动清除缓存的方法
     * 可以在配置变更、数据迁移等场景下调用
     */
    @Caching(
        evict = {
            @CacheEvict(value = "eventStats", allEntries = true),
            @CacheEvict(value = "popularPages", allEntries = true),
            @CacheEvict(value = "userSessions", allEntries = true),
            @CacheEvict(value = "analyticsHealth", allEntries = true),
            @CacheEvict(value = "deviceStats", allEntries = true),
            @CacheEvict(value = "retentionRates", allEntries = true)
        }
    )
    public void clearAllCaches() {
        log.info("清除所有分析服务缓存");
        // 这个方法通过@CacheEvict注解自动清除缓存
    }
    
    /**
     * 清除特定会话的缓存
     */
    @Caching(evict = {
        @CacheEvict(value = "userSessions", key = "'session:' + #sessionId"),
        @CacheEvict(value = "userSessions", key = "'sequence:' + #sessionId"),
        @CacheEvict(value = "userSessions", key = "'completed:' + #sessionId")
    })
    public void clearSessionCache(String sessionId) {
        log.debug("清除会话缓存: sessionId={}", sessionId);
        // 这个方法通过@CacheEvict注解自动清除特定会话的缓存
    }
    
    /**
     * 清除特定用户的缓存
     */
    @Caching(evict = {
        @CacheEvict(value = "userSessions", key = "'user:' + #userId")
    })
    public void clearUserCache(Long userId) {
        log.debug("清除用户缓存: userId={}", userId);
        // 这个方法通过@CacheEvict注解自动清除特定用户的缓存
    }
    
    /**
     * 清除时间范围相关的缓存
     */
    @Caching(
        evict = {
            @CacheEvict(value = "eventStats", key = "'count:' + #startTime.toString() + ':' + #endTime.toString()"),
            @CacheEvict(value = "eventStats", key = "'types:' + #startTime.toString() + ':' + #endTime.toString()"),
            @CacheEvict(value = "eventStats", key = "'avgCompletion:' + #startTime.toString() + ':' + #endTime.toString()"),
            @CacheEvict(value = "deviceStats", key = "#startTime.toString() + ':' + #endTime.toString()"),
            @CacheEvict(value = "popularPages", key = "#startTime.toString() + ':' + #endTime.toString() + ':*'"),
            @CacheEvict(value = "retentionRates", key = "#startTime.toString() + ':' + #endTime.toString()")
        }
    )
    public void clearTimeRangeCache(LocalDateTime startTime, LocalDateTime endTime) {
        log.debug("清除时间范围缓存: {} - {}", startTime, endTime);
        // 这个方法通过@CacheEvict注解自动清除特定时间范围的缓存
    }
}