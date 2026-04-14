package com.soulcurator.backend.model.analytics;

import com.soulcurator.backend.model.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户事件埋点实体类
 * 对应数据库表: user_events
 */
@Entity
@Table(name = "user_events")
@Data
public class UserEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的用户（可为空，支持匿名用户）
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    /**
     * 会话ID，用于关联未登录用户的数据
     */
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    
    /**
     * 事件类型
     * 可选值: page_view, selection, portrait_generated
     */
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
    /**
     * 事件名称
     * 示例: welcome_page_entered, step1_selected, soul_portrait_created
     */
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    /**
     * 页面URL
     */
    @Column(name = "page_url", length = 500)
    private String pageUrl;
    
    /**
     * 事件详细数据（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", columnDefinition = "jsonb")
    private Map<String, Object> eventData;
    
    /**
     * 浏览器User-Agent
     */
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    /**
     * IP地址（已匿名化处理）
     */
    @Column(name = "ip_address")
    private String ipAddress;
    
    /**
     * 屏幕分辨率
     * 格式: 1920x1080
     */
    @Column(name = "screen_resolution", length = 50)
    private String screenResolution;
    
    /**
     * 设备类型
     * 可选值: desktop, mobile, tablet
     */
    @Column(name = "device_type", length = 50)
    private String deviceType;
    
    /**
     * 关联的会话
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", referencedColumnName = "session_id", insertable = false, updatable = false)
    private com.soulcurator.backend.model.UserSession userSession;
    
    /**
     * 创建时间（自动记录）
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // 构造方法
    public UserEvent() {}
    
    public UserEvent(String sessionId, String eventType, String eventName) {
        this.sessionId = sessionId;
        this.eventType = eventType;
        this.eventName = eventName;
        this.createdAt = LocalDateTime.now();
    }
    
    // 便捷方法
    public boolean isPageViewEvent() {
        return "page_view".equals(eventType);
    }
    
    public boolean isSelectionEvent() {
        return "selection".equals(eventType);
    }
    
    public boolean isPortraitGeneratedEvent() {
        return "portrait_generated".equals(eventType);
    }
    
    /**
     * 获取设备类型的友好显示名称
     */
    public String getDeviceTypeDisplay() {
        if ("desktop".equals(deviceType)) {
            return "桌面";
        } else if ("mobile".equals(deviceType)) {
            return "手机";
        } else if ("tablet".equals(deviceType)) {
            return "平板";
        }
        return deviceType;
    }
    
    /**
     * 获取事件类型的友好显示名称
     */
    public String getEventTypeDisplay() {
        switch (eventType) {
            case "page_view":
                return "页面访问";
            case "selection":
                return "选项选择";
            case "portrait_generated":
                return "画像生成";
            default:
                return eventType;
        }
    }
    
    @Override
    public String toString() {
        return String.format("UserEvent{id=%d, sessionId='%s', eventType='%s', eventName='%s', createdAt=%s}",
                id, sessionId, eventType, eventName, createdAt);
    }
}