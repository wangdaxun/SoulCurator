package com.soulcurator.backend.dto.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

/**
 * 事件埋点请求DTO
 * 用于接收前端发送的事件数据
 */
@Data
public class EventRequest {
    
    /**
     * 事件类型
     * 可选值: page_view, click, selection, session_start, session_end, error, custom
     */
    @NotBlank(message = "事件类型不能为空")
    @Size(max = 50, message = "事件类型长度不能超过50个字符")
    private String eventType;
    
    /**
     * 事件名称
     * 示例: "question_click", "option_selected", "portrait_generated"
     */
    @NotBlank(message = "事件名称不能为空")
    @Size(max = 100, message = "事件名称长度不能超过100个字符")
    private String eventName;
    
    /**
     * 事件数据（JSON格式）
     * 包含事件的详细信息
     */
    private Map<String, Object> eventData;
    
    /**
     * 页面路径
     * 示例: "/exploration", "/portrait", "/recommendations"
     */
    @Size(max = 500, message = "页面路径长度不能超过500个字符")
    private String pagePath;
    
    /**
     * 页面标题
     */
    @Size(max = 200, message = "页面标题长度不能超过200个字符")
    private String pageTitle;
    
    /**
     * 会话ID
     * 用于跟踪用户会话
     */
    @Size(max = 100, message = "会话ID长度不能超过100个字符")
    private String sessionId;

    /**
     * IP地址
     * 用户访问的IP地址
     */
    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    private String ipAddress;

    /**
     * 用户代理
     */
    @Size(max = 1000, message = "用户代理长度不能超过1000个字符")
    private String userAgent;
    
    /**
     * 屏幕分辨率
     * 示例: "1920x1080"
     */
    @Size(max = 50, message = "屏幕分辨率长度不能超过50个字符")
    private String screenResolution;
    
    /**
     * 语言
     * 示例: "zh-CN", "en-US"
     */
    @Size(max = 10, message = "语言长度不能超过10个字符")
    private String language;
    
    /**
     * 时区
     * 示例: "Asia/Shanghai"
     */
    @Size(max = 50, message = "时区长度不能超过50个字符")
    private String timezone;
    
    /**
     * 时间戳（毫秒）
     * 前端发送事件的时间戳
     */
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;
    
    // 构造方法
    public EventRequest() {}
    
    public EventRequest(String eventType, String eventName, Long timestamp) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.timestamp = timestamp;
    }

    public EventRequest(String eventType, String eventName, Map<String, Object> eventData, Long timestamp) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }

    public EventRequest(String eventType, String eventName, String sessionId, Long timestamp) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
    }

    public EventRequest(String eventType, String eventName, String sessionId, Map<String, Object> eventData, Long timestamp) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.sessionId = sessionId;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }

    public EventRequest(String eventType, String eventName, String sessionId, String ipAddress, Map<String, Object> eventData, Long timestamp) {
        this.eventType = eventType;
        this.eventName = eventName;
        this.sessionId = sessionId;
        this.ipAddress = ipAddress;
        this.eventData = eventData;
        this.timestamp = timestamp;
    }
    
    // 便捷方法
    
    /**
     * 获取事件数据的JSON字符串
     */
    public String getEventDataJson() {
        if (eventData == null || eventData.isEmpty()) {
            return "{}";
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(eventData);
        } catch (Exception e) {
            return eventData.toString();
        }
    }
    
    /**
     * 获取事件数据的简化表示
     */
    public String getEventDataSummary() {
        if (eventData == null || eventData.isEmpty()) {
            return "无数据";
        }
        
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Map.Entry<String, Object> entry : eventData.entrySet()) {
            if (count > 2) {
                sb.append("...");
                break;
            }
            if (count > 0) sb.append(", ");
            sb.append(entry.getKey()).append(": ").append(
                entry.getValue() != null ? entry.getValue().toString() : "null"
            );
            count++;
        }
        return sb.toString();
    }
    
    /**
     * 验证事件类型是否有效
     */
    public boolean isValidEventType() {
        if (eventType == null) return false;
        
        String[] validTypes = {
            "page_view", "click", "selection", 
            "session_start", "session_end", 
            "error", "custom", "api_call",
            "question_view", "option_hover", "portrait_view",
            "recommendation_view", "feedback_submit"
        };
        
        for (String validType : validTypes) {
            if (validType.equals(eventType)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 获取事件类型的友好显示
     */
    public String getEventTypeDisplay() {
        if (eventType == null) return "未知";
        
        return switch (eventType) {
            case "page_view" -> "页面浏览";
            case "click" -> "点击事件";
            case "selection" -> "选择事件";
            case "session_start" -> "会话开始";
            case "session_end" -> "会话结束";
            case "error" -> "错误事件";
            case "custom" -> "自定义事件";
            case "api_call" -> "API调用";
            case "question_view" -> "问题查看";
            case "option_hover" -> "选项悬停";
            case "portrait_view" -> "画像查看";
            case "recommendation_view" -> "推荐查看";
            case "feedback_submit" -> "反馈提交";
            default -> eventType;
        };
    }
    
    /**
     * 获取事件名称的友好显示
     */
    public String getEventNameDisplay() {
        if (eventName == null) return "未知事件";
        
        // 简单的转换：将下划线转换为空格，并首字母大写
        String[] words = eventName.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                if (result.length() > 0) result.append(" ");
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase());
            }
        }
        return result.toString();
    }
    
    /**
     * 获取完整的事件描述
     */
    public String getEventDescription() {
        return getEventTypeDisplay() + ": " + getEventNameDisplay();
    }
    
    /**
     * 获取时间戳的LocalDateTime
     */
    public java.time.LocalDateTime getLocalDateTime() {
        if (timestamp == null) {
            return java.time.LocalDateTime.now();
        }
        return java.time.Instant.ofEpochMilli(timestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDateTime();
    }
    
    /**
     * 获取时间的友好显示
     */
    public String getTimeDisplay() {
        if (timestamp == null) {
            return "未知时间";
        }
        
        java.time.LocalDateTime dateTime = getLocalDateTime();
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    @Override
    public String toString() {
        return String.format("EventRequest{eventType='%s', eventName='%s', sessionId='%s', timestamp=%d}",
                eventType, eventName, sessionId, timestamp);
    }
}