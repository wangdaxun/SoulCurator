package com.soulcurator.backend.model.session;

import com.soulcurator.backend.model.portrait.SoulPortrait;
import com.soulcurator.backend.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 用户会话分析实体
 * 对应数据库中的user_sessions表，记录用户会话的完整信息
 */
@Data
@Entity
@Table(name = "user_sessions", indexes = {
    @Index(name = "idx_sessions_user", columnList = "userId"),
    @Index(name = "idx_sessions_started", columnList = "startedAt"),
    @Index(name = "idx_sessions_completed", columnList = "isCompleted")
})
public class UserSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 会话信息
    @Column(name = "session_id", unique = true, nullable = false, length = 64)
    private String sessionId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Column(name = "gateway_type", length = 50)
    private String gatewayType; // 灵魂之门入口类型
    
    // 会话统计
    @Column(name = "total_events")
    private Integer totalEvents = 0;
    
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;
    
    @Column(name = "completed_questions")
    private Integer completedQuestions = 0;
    
    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds = 0;
    
    // 会话状态
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    @Column(name = "portrait_id")
    private Long portraitId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portrait_id", insertable = false, updatable = false)
    private SoulPortrait portrait;
    
    // 设备信息
    @Column(name = "first_user_agent", columnDefinition = "TEXT")
    private String firstUserAgent;
    
    @Column(name = "first_ip_address", length = 255)
    private String firstIpAddress;
    
    @Column(name = "first_screen_resolution", length = 50)
    private String firstScreenResolution;
    
    // 时间范围
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;
}