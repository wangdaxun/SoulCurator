package com.soulcurator.backend.dto.event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * 用户事件请求DTO（适配前端格式）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserEventRequest {
    
    // 事件基本信息
    @JsonProperty("eventType")
    private String eventType;     // 事件类型：page_view/selection/portrait_generated等
    
    @JsonProperty("eventName")
    private String eventName;     // 事件名称
    
    @JsonProperty("eventData")
    private Map<String, Object> eventData;  // 事件详细数据
    
    // 页面信息
    @JsonProperty("pagePath")
    private String pagePath;      // 页面路径
    
    @JsonProperty("pageTitle")
    private String pageTitle;     // 页面标题
    
    // 设备信息
    @JsonProperty("userAgent")
    private String userAgent;     // 浏览器User-Agent
    
    @JsonProperty("screenResolution")
    private String screenResolution; // 屏幕分辨率
    
    @JsonProperty("language")
    private String language;      // 语言
    
    @JsonProperty("timezone")
    private String timezone;      // 时区
    
    // 时间戳
    @JsonProperty("timestamp")
    private Long timestamp;       // 客户端时间戳（毫秒）
    
    // 以下字段从eventData中提取
    private String sessionId;     // 会话ID（从eventData中提取）
    private Long userId;          // 用户ID（可选，从eventData中提取）
    private String pageUrl;       // 页面URL（组合pagePath和其他信息）
    private String deviceType;    // 设备类型（从userAgent解析）
    private String ipAddress;     // IP地址（从HTTP请求头获取）
}