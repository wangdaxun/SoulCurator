package com.soulcurator.backend.service.analytics;

import com.soulcurator.backend.dto.analytics.EventRequest;
import com.soulcurator.backend.model.analytics.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * 事件数据处理器
 * 负责事件数据的处理、转换和验证
 */
@Component
@Slf4j
public class EventDataProcessor {
    
    /**
     * 处理事件数据，提取有用信息
     */
    public Map<String, Object> processEventData(EventRequest request) {
        Map<String, Object> eventData = request.getEventData();
        if (eventData == null) {
            return Map.of();
        }
        
        // 添加处理后的元数据
        Map<String, Object> processedData = new java.util.HashMap<>(eventData);
        
        // 提取会话信息
        extractSessionInfo(request, processedData);
        
        // 提取用户信息
        extractUserInfo(request, processedData);
        
        // 提取设备信息
        extractDeviceInfo(request, processedData);
        
        // 提取页面信息
        extractPageInfo(request, processedData);
        
        // 添加处理时间戳
        processedData.put("processedAt", System.currentTimeMillis());
        processedData.put("processorVersion", "1.0");
        
        return processedData;
    }
    
    /**
     * 验证事件数据
     */
    public boolean validateEventData(EventRequest request) {
        if (request == null) {
            log.warn("事件请求为空");
            return false;
        }
        
        // 验证基本字段
        if (request.getEventType() == null || request.getEventType().trim().isEmpty()) {
            log.warn("事件类型为空");
            return false;
        }
        
        if (request.getEventName() == null || request.getEventName().trim().isEmpty()) {
            log.warn("事件名称为空");
            return false;
        }
        
        if (request.getTimestamp() == null || request.getTimestamp() <= 0) {
            log.warn("时间戳无效: {}", request.getTimestamp());
            return false;
        }
        
        // 验证事件类型
        if (!isValidEventType(request.getEventType())) {
            log.warn("无效的事件类型: {}", request.getEventType());
            return false;
        }
        
        // 验证事件数据大小（防止过大）
        if (request.getEventData() != null) {
            try {
                String json = request.getEventDataJson();
                if (json.length() > 10000) { // 10KB限制
                    log.warn("事件数据过大: {} bytes", json.length());
                    return false;
                }
            } catch (Exception e) {
                log.warn("序列化事件数据失败", e);
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 丰富用户事件实体
     */
    public void enrichUserEvent(UserEvent userEvent, EventRequest request) {
        if (userEvent == null || request == null) {
            return;
        }
        
        // 设置设备类型
        String deviceType = detectDeviceType(request.getUserAgent());
        userEvent.setDeviceType(deviceType);
        
        // 设置屏幕分辨率
        if (request.getScreenResolution() != null) {
            userEvent.setScreenResolution(request.getScreenResolution());
        }
        
        // 处理IP地址（匿名化）
        if (request.getEventData() != null && request.getEventData().containsKey("ipAddress")) {
            Object ipObj = request.getEventData().get("ipAddress");
            if (ipObj instanceof String) {
                String anonymizedIp = anonymizeIpAddress((String) ipObj);
                userEvent.setIpAddress(anonymizedIp);
            }
        }
        
        // 添加地理位置信息（如果有）
        if (request.getEventData() != null) {
            Object countryObj = request.getEventData().get("country");
            Object cityObj = request.getEventData().get("city");
            
            // 可以在这里添加地理位置处理逻辑
            // 例如：userEvent.setCountry((String) countryObj);
            // 例如：userEvent.setCity((String) cityObj);
        }
    }
    
    /**
     * 生成会话ID
     */
    public String generateSessionId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
    
    /**
     * 从事件数据中提取会话ID
     */
    public String extractSessionId(EventRequest request) {
        // 优先使用EventRequest中的sessionId字段
        if (request.getSessionId() != null && !request.getSessionId().trim().isEmpty()) {
            return request.getSessionId();
        }

        // 其次尝试从eventData中提取
        if (request.getEventData() != null && request.getEventData().containsKey("sessionId")) {
            Object sessionIdObj = request.getEventData().get("sessionId");
            if (sessionIdObj instanceof String) {
                String sessionId = (String) sessionIdObj;
                if (!sessionId.trim().isEmpty()) {
                    return sessionId;
                }
            }
        }

        // 如果没有提供，生成一个新的
        return generateSessionId();
    }
    
    /**
     * 检测设备类型
     */
    private String detectDeviceType(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "mobile";
        } else if (ua.contains("tablet") || ua.contains("ipad")) {
            return "tablet";
        } else if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux")) {
            return "desktop";
        } else {
            return "other";
        }
    }
    
    /**
     * 匿名化IP地址
     */
    private String anonymizeIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return null;
        }
        
        try {
            // 简单的匿名化：保留前两个八位字节
            String[] parts = ipAddress.split("\\.");
            if (parts.length >= 2) {
                return parts[0] + "." + parts[1] + ".0.0";
            }
            return ipAddress;
        } catch (Exception e) {
            log.warn("匿名化IP地址失败: {}", ipAddress, e);
            return ipAddress;
        }
    }
    
    /**
     * 验证事件类型是否有效
     */
    private boolean isValidEventType(String eventType) {
        if (eventType == null) {
            return false;
        }
        
        String[] validTypes = {
            "page_view", "click", "selection", 
            "session_start", "session_end", 
            "error", "custom", "api_call",
            "question_view", "option_hover", "portrait_view",
            "recommendation_view", "feedback_submit",
            "portrait_generated", "recommendation_generated",
            "user_registered", "user_logged_in", "user_logged_out",
            "exploration_started", "exploration_completed",
            "work_viewed", "work_liked", "work_shared"
        };
        
        for (String validType : validTypes) {
            if (validType.equals(eventType)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 提取会话信息
     */
    private void extractSessionInfo(EventRequest request, Map<String, Object> processedData) {
        String sessionId = extractSessionId(request);
        processedData.put("sessionId", sessionId);
        processedData.put("sessionStartTime", System.currentTimeMillis());
    }
    
    /**
     * 提取用户信息
     */
    private void extractUserInfo(EventRequest request, Map<String, Object> processedData) {
        if (request.getEventData() != null) {
            Object userId = request.getEventData().get("userId");
            if (userId != null) {
                processedData.put("userId", userId);
            }
            
            Object userType = request.getEventData().get("userType");
            if (userType != null) {
                processedData.put("userType", userType);
            }
        }
    }
    
    /**
     * 提取设备信息
     */
    private void extractDeviceInfo(EventRequest request, Map<String, Object> processedData) {
        processedData.put("userAgent", request.getUserAgent());
        processedData.put("screenResolution", request.getScreenResolution());
        processedData.put("language", request.getLanguage());
        processedData.put("timezone", request.getTimezone());
        
        // 检测浏览器信息
        String browser = detectBrowser(request.getUserAgent());
        String os = detectOperatingSystem(request.getUserAgent());
        
        processedData.put("browser", browser);
        processedData.put("operatingSystem", os);
    }
    
    /**
     * 提取页面信息
     */
    private void extractPageInfo(EventRequest request, Map<String, Object> processedData) {
        processedData.put("pagePath", request.getPagePath());
        processedData.put("pageTitle", request.getPageTitle());
        
        // 提取页面参数（如果有）
        if (request.getPagePath() != null && request.getPagePath().contains("?")) {
            String queryString = request.getPagePath().split("\\?")[1];
            // 可以在这里解析查询参数
            processedData.put("queryString", queryString);
        }
    }
    
    /**
     * 检测浏览器
     */
    private String detectBrowser(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        if (ua.contains("chrome") && !ua.contains("chromium")) {
            return "chrome";
        } else if (ua.contains("firefox")) {
            return "firefox";
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            return "safari";
        } else if (ua.contains("edge")) {
            return "edge";
        } else if (ua.contains("opera")) {
            return "opera";
        } else {
            return "other";
        }
    }
    
    /**
     * 检测操作系统
     */
    private String detectOperatingSystem(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        
        String ua = userAgent.toLowerCase();
        
        if (ua.contains("windows")) {
            return "windows";
        } else if (ua.contains("macintosh") || ua.contains("mac os")) {
            return "macos";
        } else if (ua.contains("linux")) {
            return "linux";
        } else if (ua.contains("android")) {
            return "android";
        } else if (ua.contains("iphone") || ua.contains("ipad")) {
            return "ios";
        } else {
            return "other";
        }
    }
    
    /**
     * 计算事件数据的哈希值（用于去重）
     */
    public String calculateEventHash(EventRequest request) {
        StringBuilder hashInput = new StringBuilder();
        hashInput.append(request.getEventType());
        hashInput.append(request.getEventName());
        hashInput.append(request.getTimestamp());
        
        if (request.getEventData() != null) {
            hashInput.append(request.getEventDataJson());
        }
        
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(hashInput.toString().getBytes());
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (Exception e) {
            log.warn("计算事件哈希失败", e);
            return String.valueOf(hashInput.toString().hashCode());
        }
    }
    
    /**
     * 检查是否是重复事件
     */
    public boolean isDuplicateEvent(String eventHash, long timeWindowMillis) {
        // 这里可以实现重复事件检查逻辑
        // 例如：检查在时间窗口内是否有相同哈希的事件
        // 当前实现返回false，实际项目中需要根据具体需求实现
        return false;
    }
}