package com.soulcurator.backend.model.event;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 用户事件埋点实体
 * 对应数据库中的user_events表，用于存储用户行为埋点数据
 */
@Data
@Entity
@Table(name = "user_events", indexes = {
    @Index(name = "idx_events_session", columnList = "sessionId"),
    @Index(name = "idx_events_type", columnList = "eventType"),
    @Index(name = "idx_events_created", columnList = "createdAt"),
    @Index(name = "idx_events_user", columnList = "userId")
})
public class UserEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户标识
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    
    // 事件信息
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    @Column(name = "page_url", length = 500)
    private String pageUrl;
    
    // JSONB字段需要特殊处理
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", columnDefinition = "jsonb")
    private String eventData;
    
    // 设备信息
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "ip_address", length = 255)
    private String ipAddress;
    
    @Column(name = "screen_resolution", length = 50)
    private String screenResolution;
    
    @Column(name = "device_type", length = 50)
    private String deviceType;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}