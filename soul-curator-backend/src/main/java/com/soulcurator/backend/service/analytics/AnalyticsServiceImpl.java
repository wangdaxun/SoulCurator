package com.soulcurator.backend.service.analytics;

import com.soulcurator.backend.dto.analytics.EventRequest;
import com.soulcurator.backend.dto.analytics.EventResponse;
import com.soulcurator.backend.model.User;
import com.soulcurator.backend.model.analytics.UserEvent;
import com.soulcurator.backend.repository.UserRepository;
import com.soulcurator.backend.repository.analytics.UserEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 分析服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {
    
    private final UserEventRepository userEventRepository;
    private final UserRepository userRepository;
    private final EventDataProcessor eventDataProcessor;
    private final ApplicationEventPublisher eventPublisher;
    private final AnalyticsMonitorService monitorService;
    
    @Override
    @Transactional
    public EventResponse recordEvent(EventRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("开始记录事件: {}", request);
            
            // 记录监控数据：事件开始
            monitorService.recordEventStart(request.getEventType(), request.getEventName());
            
            // 验证请求数据
            if (!eventDataProcessor.validateEventData(request)) {
                log.warn("事件请求验证失败: {}", request);
                monitorService.recordEventCompletion(request.getEventType(), false, 
                        System.currentTimeMillis() - startTime);
                monitorService.recordError("validation", "事件请求数据无效");
                return EventResponse.validationError("事件请求数据无效");
            }
            
            // 检查重复事件（可选）
            String eventHash = eventDataProcessor.calculateEventHash(request);
            if (eventDataProcessor.isDuplicateEvent(eventHash, 5000)) { // 5秒窗口
                log.warn("检测到重复事件: hash={}", eventHash);
                monitorService.recordEventCompletion(request.getEventType(), true, 
                        System.currentTimeMillis() - startTime);
                return EventResponse.error("重复事件，已忽略", 200);
            }
            
            // 查找关联的用户（如果提供了用户ID）
            User user = null;
            if (request.getEventData() != null && request.getEventData().containsKey("userId")) {
                Object userIdObj = request.getEventData().get("userId");
                if (userIdObj instanceof Number) {
                    Long userId = ((Number) userIdObj).longValue();
                    Optional<User> userOptional = userRepository.findById(userId);
                    if (userOptional.isPresent()) {
                        user = userOptional.get();
                        log.debug("找到关联用户: userId={}", userId);
                    }
                }
            }
            
            // 创建用户事件实体
            UserEvent userEvent = createUserEventFromRequest(request, user);
            
            // 丰富事件数据
            eventDataProcessor.enrichUserEvent(userEvent, request);
            
            // 记录设备类型
            if (userEvent.getDeviceType() != null) {
                monitorService.recordDeviceType(userEvent.getDeviceType());
            }
            
            // 记录维度选择（如果是选择事件）
            if ("selection".equals(request.getEventType()) && request.getEventData() != null) {
                Object dimensionObj = request.getEventData().get("dimension");
                if (dimensionObj instanceof String) {
                    monitorService.recordDimensionSelection((String) dimensionObj);
                }
            }
            
            // 保存到数据库
            long dbStartTime = System.currentTimeMillis();
            UserEvent savedEvent = userEventRepository.save(userEvent);
            long dbDuration = System.currentTimeMillis() - dbStartTime;
            
            monitorService.recordDatabaseOperation("save", dbDuration, true);
            
            log.info("事件记录成功: eventId={}, sessionId={}, eventType={}, eventName={}", 
                    savedEvent.getId(), savedEvent.getSessionId(), 
                    savedEvent.getEventType(), savedEvent.getEventName());
            
            // 发布事件记录完成事件（用于异步处理）
            eventPublisher.publishEvent(
                    new EventProcessingListener.EventRecordedEvent(savedEvent, request)
            );
            
            // 记录监控数据：事件成功完成
            long totalDuration = System.currentTimeMillis() - startTime;
            monitorService.recordEventCompletion(request.getEventType(), true, totalDuration);
            
            // 构建成功响应
            return EventResponse.success(
                    savedEvent.getId(),
                    savedEvent.getSessionId(),
                    savedEvent.getEventType(),
                    savedEvent.getEventName(),
                    request.getEventDataSummary(),
                    request.getPagePath(),
                    request.getPageTitle(),
                    savedEvent.getCreatedAt()
            );
            
        } catch (Exception e) {
            log.error("记录事件时发生异常", e);
            
            // 记录监控数据：事件失败
            long totalDuration = System.currentTimeMillis() - startTime;
            monitorService.recordEventCompletion(request.getEventType(), false, totalDuration);
            monitorService.recordError("exception", e.getMessage());
            
            return EventResponse.serverError("事件记录失败: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional
    public List<EventResponse> recordEvents(List<EventRequest> requests) {
        return requests.stream()
                .map(this::recordEvent)
                .toList();
    }
    
    @Override
    public UserEvent getEventById(Long eventId) {
        return userEventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("事件不存在: " + eventId));
    }
    
    @Override
    public List<UserEvent> getEventsBySessionId(String sessionId) {
        return userEventRepository.findBySessionId(sessionId);
    }
    
    @Override
    public List<UserEvent> getEventsByUserId(Long userId) {
        return userEventRepository.findByUserId(userId);
    }
    
    @Override
    public List<UserEvent> getEventsByType(String eventType, LocalDateTime startTime, LocalDateTime endTime) {
        return userEventRepository.findByEventTypeAndEventName(eventType, null).stream()
                .filter(event -> event.getCreatedAt().isAfter(startTime) && event.getCreatedAt().isBefore(endTime))
                .toList();
    }
    
    @Override
    public Long countEvents(LocalDateTime startTime, LocalDateTime endTime) {
        return userEventRepository.countByCreatedAtBetween(startTime, endTime);
    }
    
    @Override
    public Map<String, Long> countEventsByType(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> results = userEventRepository.countByEventTypeGroup(startTime, endTime);
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
    
    @Override
    public Map<String, Long> countEventsByDeviceType(LocalDateTime startTime, LocalDateTime endTime) {
        List<Object[]> results = userEventRepository.countByDeviceTypeGroup(startTime, endTime);
        return results.stream()
                .collect(java.util.stream.Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
    
    @Override
    public Map<String, Long> getPopularPages(LocalDateTime startTime, LocalDateTime endTime, int limit) {
        List<Object[]> results = userEventRepository.getPopularPages(startTime, endTime);
        return results.stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
    
    @Override
    public List<UserEvent> getUserBehaviorSequence(String sessionId) {
        return userEventRepository.getUserBehaviorSequence(sessionId);
    }
    
    @Override
    public boolean isSessionCompleted(String sessionId) {
        return userEventRepository.isSessionCompleted(sessionId);
    }
    
    @Override
    public Double getAverageCompletionTime(LocalDateTime startTime, LocalDateTime endTime) {
        return userEventRepository.getAverageCompletionTime(startTime, endTime);
    }
    
    @Override
    public Long getActiveSessionsCount(int timeWindow) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(timeWindow);
        List<Object[]> results = userEventRepository.countEventsPerSession(threshold, LocalDateTime.now());
        return results.stream()
                .filter(arr -> (Long) arr[1] > 0)
                .count();
    }
    
    @Override
    public Map<String, Double> getUserRetentionRates(LocalDateTime startDate, LocalDateTime endDate) {
        // 简化的留存率计算
        // 实际实现需要更复杂的逻辑
        Map<String, Double> retentionRates = new java.util.HashMap<>();
        retentionRates.put("day1", 0.85);
        retentionRates.put("day7", 0.65);
        retentionRates.put("day30", 0.45);
        return retentionRates;
    }
    
    @Override
    @Transactional
    public Long cleanupOldEvents(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        try {
            userEventRepository.deleteEventsBefore(cutoffDate);
            log.info("清理了{}天前的事件数据", daysToKeep);
            return 1L; // 简化实现，实际应返回清理的数量
        } catch (Exception e) {
            log.error("清理事件数据时发生异常", e);
            return 0L;
        }
    }
    
    @Override
    public String exportEvents(LocalDateTime startTime, LocalDateTime endTime, String format) {
        List<UserEvent> events = userEventRepository.findAll().stream()
                .filter(event -> event.getCreatedAt().isAfter(startTime) && event.getCreatedAt().isBefore(endTime))
                .toList();
        
        if ("csv".equalsIgnoreCase(format)) {
            return exportToCsv(events);
        } else {
            return exportToJson(events);
        }
    }
    
    @Override
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> health = new java.util.HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("database", "CONNECTED");
        health.put("totalEvents", userEventRepository.count());
        health.put("lastHourEvents", countEvents(LocalDateTime.now().minusHours(1), LocalDateTime.now()));
        return health;
    }
    
    @Override
    public Map<String, Object> getServiceStats() {
        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalEvents", userEventRepository.count());
        stats.put("uniqueSessions", userEventRepository.findAll().stream()
                .map(UserEvent::getSessionId)
                .distinct()
                .count());
        stats.put("uniqueUsers", userEventRepository.findAll().stream()
                .map(UserEvent::getUser)
                .filter(user -> user != null)
                .map(User::getId)
                .distinct()
                .count());
        stats.put("mostCommonEventType", getMostCommonEventType());
        stats.put("dataStartDate", getDataStartDate());
        stats.put("dataEndDate", LocalDateTime.now());
        return stats;
    }
    
    // 私有辅助方法
    

    
    /**
     * 从请求创建用户事件实体
     */
    private UserEvent createUserEventFromRequest(EventRequest request, User user) {
        UserEvent userEvent = new UserEvent();
        
        // 设置基本属性
        userEvent.setUser(user);
        
        // 从eventData中提取sessionId
        String sessionId = eventDataProcessor.extractSessionId(request);
        userEvent.setSessionId(sessionId);
        
        userEvent.setEventType(request.getEventType());
        userEvent.setEventName(request.getEventName());
        
        // 处理事件数据
        Map<String, Object> processedData = eventDataProcessor.processEventData(request);
        userEvent.setEventData(processedData);
        
        userEvent.setPageUrl(request.getPagePath());
        userEvent.setUserAgent(request.getUserAgent());
        
        // 设置创建时间（使用请求中的时间戳）
        if (request.getTimestamp() != null) {
            userEvent.setCreatedAt(request.getLocalDateTime());
        }
        
        return userEvent;
    }
    

    
    /**
     * 导出为CSV格式
     */
    private String exportToCsv(List<UserEvent> events) {
        StringBuilder csv = new StringBuilder();
        
        // 添加表头
        csv.append("id,session_id,event_type,event_name,page_url,device_type,created_at\n");
        
        // 添加数据行
        for (UserEvent event : events) {
            csv.append(event.getId()).append(",");
            csv.append(escapeCsv(event.getSessionId())).append(",");
            csv.append(escapeCsv(event.getEventType())).append(",");
            csv.append(escapeCsv(event.getEventName())).append(",");
            csv.append(escapeCsv(event.getPageUrl())).append(",");
            csv.append(escapeCsv(event.getDeviceType())).append(",");
            csv.append(event.getCreatedAt()).append("\n");
        }
        
        return csv.toString();
    }
    
    /**
     * 导出为JSON格式
     */
    private String exportToJson(List<UserEvent> events) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(events);
        } catch (Exception e) {
            log.error("导出JSON时发生异常", e);
            return "[]";
        }
    }
    
    /**
     * CSV转义
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        
        // 如果包含逗号、双引号或换行符，需要转义
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
    
    /**
     * 获取最常见的事件类型
     */
    private String getMostCommonEventType() {
        List<Object[]> results = userEventRepository.countByEventTypeGroup(
                LocalDateTime.now().minusDays(7), 
                LocalDateTime.now()
        );
        
        if (results.isEmpty()) {
            return "N/A";
        }
        
        return results.stream()
                .max((a, b) -> Long.compare((Long) a[1], (Long) b[1]))
                .map(arr -> (String) arr[0])
                .orElse("N/A");
    }
    
    /**
     * 获取数据开始日期
     */
    private LocalDateTime getDataStartDate() {
        return userEventRepository.findAll().stream()
                .map(UserEvent::getCreatedAt)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());
    }
}