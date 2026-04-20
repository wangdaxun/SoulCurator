package com.soulcurator.backend.service.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soulcurator.backend.dto.event.UserEventRequest;
import com.soulcurator.backend.dto.event.UserEventResponse;
import com.soulcurator.backend.model.event.UserEvent;
import com.soulcurator.backend.repository.event.UserEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户事件服务
 * 将埋点数据存储到user_events表
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventService {
    
    private final UserEventRepository userEventRepository;
    private final ObjectMapper objectMapper;
    
    /**
     * 记录用户事件
     */
    @Transactional
    public UserEventResponse recordEvent(UserEventRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 验证请求
            validateRequest(request);
            
            // 提取和补充数据
            extractAndEnrichData(request);
            
            // 创建事件实体
            UserEvent event = createEventFromRequest(request);
            
            // 保存事件
            UserEvent savedEvent = userEventRepository.save(event);
            
            long processingTime = System.currentTimeMillis() - startTime;
            
            log.info("用户事件记录成功: eventId={}, eventType={}, eventName={}, sessionId={}, processingTime={}ms", 
                    savedEvent.getId(), savedEvent.getEventType(), savedEvent.getEventName(), 
                    savedEvent.getSessionId(), processingTime);
            
            return UserEventResponse.success(savedEvent.getId(), processingTime);
            
        } catch (Exception e) {
            log.error("记录用户事件失败", e);
            long processingTime = System.currentTimeMillis() - startTime;
            
            // 异步记录失败事件（避免事务冲突）
            try {
                recordFailedEventAsync(request, e.getMessage(), processingTime);
            } catch (Exception ex) {
                log.error("异步记录失败事件也失败了", ex);
            }
            
            return UserEventResponse.error("记录事件失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量记录事件
     */
    @Transactional
    public UserEventResponse recordEvents(UserEventRequest[] requests) {
        long startTime = System.currentTimeMillis();
        int successCount = 0;
        int failCount = 0;
        
        for (UserEventRequest request : requests) {
            try {
                recordEvent(request);
                successCount++;
            } catch (Exception e) {
                log.error("批量记录事件失败: {}", e.getMessage());
                failCount++;
            }
        }
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        log.info("批量事件记录完成: 成功={}, 失败={}, 总耗时={}ms", successCount, failCount, processingTime);
        
        UserEventResponse response = new UserEventResponse();
        response.setSuccess(true);
        response.setMessage(String.format("批量事件记录完成: 成功%d个, 失败%d个", successCount, failCount));
        response.setProcessingTimeMs(processingTime);
        
        return response;
    }
    
    /**
     * 验证请求
     */
    private void validateRequest(UserEventRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("请求不能为空");
        }
        
        if (request.getEventType() == null || request.getEventType().trim().isEmpty()) {
            throw new IllegalArgumentException("事件类型不能为空");
        }
        
        if (request.getEventName() == null || request.getEventName().trim().isEmpty()) {
            throw new IllegalArgumentException("事件名称不能为空");
        }
        
        if (request.getEventData() == null) {
            throw new IllegalArgumentException("事件数据不能为空");
        }
        
        // 验证长度限制
        if (request.getEventType().length() > 50) {
            throw new IllegalArgumentException("事件类型长度不能超过50个字符");
        }
        
        if (request.getEventName().length() > 100) {
            throw new IllegalArgumentException("事件名称长度不能超过100个字符");
        }
    }
    
    /**
     * 提取和补充数据
     */
    private void extractAndEnrichData(UserEventRequest request) {
        // 从eventData中提取sessionId
        String sessionId = null;
        if (request.getEventData() != null) {
            Object sessionIdObj = request.getEventData().get("sessionId");
            if (sessionIdObj != null) {
                sessionId = sessionIdObj.toString();
                request.setSessionId(sessionId);
            }
            
            // 从eventData中提取userId（如果有）
            Object userIdObj = request.getEventData().get("userId");
            if (userIdObj != null) {
                try {
                    if (userIdObj instanceof Number) {
                        request.setUserId(((Number) userIdObj).longValue());
                    } else {
                        request.setUserId(Long.parseLong(userIdObj.toString()));
                    }
                } catch (NumberFormatException e) {
                    log.warn("无法解析userId: {}", userIdObj);
                }
            }
        }
        
        // 确保sessionId不为空
        if (sessionId == null || sessionId.trim().isEmpty()) {
            // 生成一个随机会话ID
            sessionId = "session_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8);
            request.setSessionId(sessionId);
            
            // 如果eventData存在，更新它
            if (request.getEventData() != null) {
                request.getEventData().put("sessionId", sessionId);
            }
        }
        
        // 构建pageUrl
        if (request.getPagePath() != null) {
            StringBuilder pageUrl = new StringBuilder(request.getPagePath());
            
            // 如果有query参数，添加到URL
            if (request.getEventData() != null) {
                Object queryObj = request.getEventData().get("query");
                if (queryObj instanceof Map && !((Map<?, ?>) queryObj).isEmpty()) {
                    pageUrl.append("?");
                    // 简化处理，实际应该正确编码query参数
                    ((Map<?, ?>) queryObj).forEach((k, v) -> {
                        pageUrl.append(k).append("=").append(v).append("&");
                    });
                    // 移除最后一个&
                    pageUrl.setLength(pageUrl.length() - 1);
                }
            }
            
            request.setPageUrl(pageUrl.toString());
        }
        
        // 解析设备类型
        if (request.getUserAgent() != null && !request.getUserAgent().isEmpty()) {
            request.setDeviceType(parseDeviceType(request.getUserAgent()));
        }
    }
    
    /**
     * 从请求创建事件实体
     */
    private UserEvent createEventFromRequest(UserEventRequest request) throws JsonProcessingException {
        UserEvent event = new UserEvent();
        
        // 设置基本字段
        event.setUserId(request.getUserId());
        event.setSessionId(request.getSessionId() != null ? request.getSessionId() : "unknown_session");
        event.setEventType(request.getEventType() != null ? request.getEventType() : "unknown");
        event.setEventName(request.getEventName() != null ? request.getEventName() : "unknown_event");
        event.setPageUrl(request.getPageUrl() != null ? request.getPageUrl() : "/unknown");
        event.setUserAgent(request.getUserAgent() != null ? request.getUserAgent() : "unknown");
        event.setIpAddress(request.getIpAddress() != null ? request.getIpAddress() : "0.0.0.0");
        event.setScreenResolution(request.getScreenResolution() != null ? request.getScreenResolution() : "unknown");
        event.setDeviceType(request.getDeviceType() != null ? request.getDeviceType() : "unknown");
        
        // 构建完整的eventData，包含所有原始数据
        Map<String, Object> fullEventData = request.getEventData();
        if (fullEventData == null) {
            fullEventData = new java.util.HashMap<>();
        }
        
        // 添加额外的元数据
        fullEventData.put("pageTitle", request.getPageTitle() != null ? request.getPageTitle() : "unknown");
        fullEventData.put("language", request.getLanguage() != null ? request.getLanguage() : "unknown");
        fullEventData.put("timezone", request.getTimezone() != null ? request.getTimezone() : "unknown");
        fullEventData.put("clientTimestamp", request.getTimestamp() != null ? request.getTimestamp() : System.currentTimeMillis());
        fullEventData.put("serverTimestamp", System.currentTimeMillis());
        
        // 确保eventData是有效的JSON字符串
        event.setEventData(objectMapper.writeValueAsString(fullEventData));
        
        return event;
    }
    
    /**
     * 解析设备类型
     */
    private String parseDeviceType(String userAgent) {
        String ua = userAgent.toLowerCase();
        
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "mobile";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "tablet";
        } else {
            return "desktop";
        }
    }
    
    /**
     * 异步记录失败事件（避免事务冲突）
     */
    private void recordFailedEventAsync(UserEventRequest request, String errorMessage, long processingTime) {
        try {
            // 在新线程中记录失败事件
            new Thread(() -> {
                try {
                    recordFailedEventInNewTransaction(request, errorMessage, processingTime);
                } catch (Exception e) {
                    log.error("在新事务中记录失败事件失败", e);
                }
            }).start();
        } catch (Exception e) {
            log.error("启动异步记录失败事件线程失败", e);
        }
    }
    
    /**
     * 在新事务中记录失败事件
     */
    @Transactional
    public void recordFailedEventInNewTransaction(UserEventRequest request, String errorMessage, long processingTime) throws JsonProcessingException {
        UserEvent failedEvent = new UserEvent();
        
        failedEvent.setSessionId(request != null && request.getSessionId() != null ? request.getSessionId() : "unknown");
        failedEvent.setEventType("system_error");
        failedEvent.setEventName("event_record_failed");
        failedEvent.setUserId(request != null ? request.getUserId() : null);
        
        // 设置基本字段
        failedEvent.setPageUrl("/error");
        failedEvent.setUserAgent(request != null ? request.getUserAgent() : "unknown");
        failedEvent.setIpAddress("0.0.0.0");
        failedEvent.setDeviceType("unknown");
        failedEvent.setScreenResolution("unknown");
        
        // 记录原始请求数据
        Map<String, Object> errorData = new java.util.HashMap<>();
        
        if (request != null && request.getEventData() != null) {
            errorData.putAll(request.getEventData());
        }
        
        // 添加错误信息
        errorData.put("error", errorMessage);
        errorData.put("processingTimeMs", processingTime);
        errorData.put("failedAt", System.currentTimeMillis());
        
        // 添加请求基本信息
        if (request != null) {
            errorData.put("originalRequest", Map.of(
                "eventType", request.getEventType() != null ? request.getEventType() : "unknown",
                "eventName", request.getEventName() != null ? request.getEventName() : "unknown",
                "pagePath", request.getPagePath() != null ? request.getPagePath() : "/unknown"
            ));
        }
        
        // 确保生成有效的JSON字符串
        failedEvent.setEventData(objectMapper.writeValueAsString(errorData));
        
        userEventRepository.save(failedEvent);
        log.info("失败事件记录成功: sessionId={}, error={}", failedEvent.getSessionId(), errorMessage);
    }
    
    /**
     * 获取服务健康状态
     */
    public Map<String, Object> getHealthStatus() {
        try {
            long totalEvents = userEventRepository.count();
            long todayEvents = userEventRepository.countByTimeRange(
                LocalDateTime.now().withHour(0).withMinute(0).withSecond(0),
                LocalDateTime.now()
            );
            
            return Map.of(
                "status", "healthy",
                "timestamp", LocalDateTime.now(),
                "totalEvents", totalEvents,
                "todayEvents", todayEvents,
                "database", "connected"
            );
        } catch (Exception e) {
            log.error("获取健康状态失败", e);
            return Map.of(
                "status", "unhealthy",
                "timestamp", LocalDateTime.now(),
                "error", e.getMessage(),
                "database", "error"
            );
        }
    }
    
    /**
     * 获取服务信息
     */
    public Map<String, Object> getServiceInfo() {
        return Map.of(
            "service", "SoulCurator User Event Service",
            "version", "1.0.0",
            "description", "灵魂策展人用户事件埋点服务",
            "storage", "user_events table",
            "endpoints", Map.of(
                "POST /api/v1/analytics/events", "记录用户事件",
                "GET /api/v1/analytics/health", "健康检查",
                "GET /api/v1/analytics/info", "服务信息"
            ),
            "timestamp", LocalDateTime.now()
        );
    }
}