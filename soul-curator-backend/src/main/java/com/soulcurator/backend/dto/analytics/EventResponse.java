package com.soulcurator.backend.dto.analytics;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 事件埋点响应DTO
 * 用于返回事件记录的结果
 */
@Data
public class EventResponse {
    
    /**
     * 事件ID
     */
    private Long eventId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 事件名称
     */
    private String eventName;
    
    /**
     * 事件类型显示名称
     */
    private String eventTypeDisplay;
    
    /**
     * 事件名称显示名称
     */
    private String eventNameDisplay;
    
    /**
     * 事件数据摘要
     */
    private String eventDataSummary;
    
    /**
     * 页面路径
     */
    private String pagePath;
    
    /**
     * 页面标题
     */
    private String pageTitle;
    
    /**
     * 记录时间
     */
    private LocalDateTime recordedAt;
    
    /**
     * 记录时间的友好显示
     */
    private String recordedAtDisplay;
    
    /**
     * 是否成功记录
     */
    private Boolean success;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应代码
     * 200: 成功
     * 400: 参数错误
     * 500: 服务器错误
     */
    private Integer code;
    
    // 构造方法
    
    public EventResponse() {}
    
    /**
     * 成功响应
     */
    public static EventResponse success(Long eventId, String sessionId, String eventType, 
                                       String eventName, LocalDateTime recordedAt) {
        EventResponse response = new EventResponse();
        response.setSuccess(true);
        response.setEventId(eventId);
        response.setSessionId(sessionId);
        response.setEventType(eventType);
        response.setEventName(eventName);
        response.setEventTypeDisplay(getEventTypeDisplay(eventType));
        response.setEventNameDisplay(getEventNameDisplay(eventName));
        response.setRecordedAt(recordedAt);
        response.setRecordedAtDisplay(formatDateTime(recordedAt));
        response.setMessage("事件记录成功");
        response.setCode(200);
        return response;
    }
    
    /**
     * 成功响应（带数据）
     */
    public static EventResponse success(Long eventId, String sessionId, String eventType, 
                                       String eventName, String eventDataSummary, 
                                       String pagePath, String pageTitle, LocalDateTime recordedAt) {
        EventResponse response = success(eventId, sessionId, eventType, eventName, recordedAt);
        response.setEventDataSummary(eventDataSummary);
        response.setPagePath(pagePath);
        response.setPageTitle(pageTitle);
        return response;
    }
    
    /**
     * 失败响应
     */
    public static EventResponse error(String message) {
        EventResponse response = new EventResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setCode(400);
        return response;
    }
    
    /**
     * 失败响应（带代码）
     */
    public static EventResponse error(String message, Integer code) {
        EventResponse response = new EventResponse();
        response.setSuccess(false);
        response.setMessage(message);
        response.setCode(code);
        return response;
    }
    
    /**
     * 服务器错误响应
     */
    public static EventResponse serverError(String message) {
        return error(message, 500);
    }
    
    /**
     * 验证错误响应
     */
    public static EventResponse validationError(String message) {
        return error(message, 400);
    }
    
    // 便捷方法
    
    /**
     * 获取事件类型的友好显示
     */
    private static String getEventTypeDisplay(String eventType) {
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
    private static String getEventNameDisplay(String eventName) {
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
     * 格式化日期时间
     */
    private static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "未知时间";
        }
        
        java.time.format.DateTimeFormatter formatter = 
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    /**
     * 获取完整的事件描述
     */
    public String getEventDescription() {
        return eventTypeDisplay + ": " + eventNameDisplay;
    }
    
    /**
     * 获取响应时间的友好显示
     */
    public String getResponseTimeDisplay() {
        return java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        );
    }
    
    /**
     * 获取响应摘要
     */
    public String getResponseSummary() {
        if (success == null) {
            return "未知状态";
        }
        
        if (success) {
            return String.format("成功记录事件 #%d (%s)", eventId, getEventDescription());
        } else {
            return String.format("记录失败: %s (代码: %d)", message, code);
        }
    }
    
    /**
     * 判断是否是成功响应
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }
    
    /**
     * 判断是否是错误响应
     */
    public boolean isError() {
        return !isSuccess();
    }
    
    /**
     * 获取错误类型
     */
    public String getErrorType() {
        if (isSuccess()) {
            return "无错误";
        }
        
        if (code == null) {
            return "未知错误";
        }
        
        return switch (code) {
            case 400 -> "参数错误";
            case 401 -> "未授权";
            case 403 -> "禁止访问";
            case 404 -> "资源未找到";
            case 500 -> "服务器错误";
            default -> "错误代码: " + code;
        };
    }
    
    /**
     * 转换为Map（用于日志记录）
     */
    public java.util.Map<String, Object> toMap() {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        map.put("eventId", eventId);
        map.put("sessionId", sessionId);
        map.put("eventType", eventType);
        map.put("eventName", eventName);
        map.put("success", success);
        map.put("message", message);
        map.put("code", code);
        map.put("recordedAt", recordedAt);
        return map;
    }
    
    /**
     * 转换为JSON字符串
     */
    public String toJsonString() {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(toMap());
        } catch (Exception e) {
            return toString();
        }
    }
    
    @Override
    public String toString() {
        return String.format("EventResponse{success=%s, eventId=%d, eventType='%s', code=%d}", 
                success, eventId, eventType, code);
    }
}