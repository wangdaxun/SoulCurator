package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户会话分析实体类
 * 对应数据库表: user_sessions
 */
@Entity
@Table(name = "user_sessions")
@Data
public class UserSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 会话ID
     */
    @Column(name = "session_id", unique = true, nullable = false, length = 64)
    private String sessionId;
    
    /**
     * 关联的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    /**
     * 总事件数
     */
    @Column(name = "total_events")
    private Integer totalEvents = 0;
    
    /**
     * 回答问题数
     */
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;
    
    /**
     * 总会话时长（秒）
     */
    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds = 0;
    
    /**
     * 是否完成灵魂探索
     */
    @Column(name = "is_completed")
    private Boolean isCompleted = false;
    
    /**
     * 生成的画像
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portrait_id")
    private SoulPortrait portrait;
    
    /**
     * 首次User-Agent
     */
    @Column(name = "first_user_agent", columnDefinition = "TEXT")
    private String firstUserAgent;
    
    /**
     * 首次IP地址
     */
    @Column(name = "first_ip_address")
    private String firstIpAddress;
    
    /**
     * 首次屏幕分辨率
     */
    @Column(name = "first_screen_resolution", length = 50)
    private String firstScreenResolution;
    
    /**
     * 会话开始时间
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    /**
     * 会话结束时间
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    /**
     * 最后活跃时间
     */
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;
    
    // 关联关系
    
    /**
     * 会话中的事件
     */
    @OneToMany(mappedBy = "userSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.soulcurator.backend.model.analytics.UserEvent> events = new ArrayList<>();
    
    // 构造方法
    public UserSession() {}
    
    public UserSession(String sessionId, User user) {
        this.sessionId = sessionId;
        this.user = user;
        this.startedAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }
    
    public UserSession(String sessionId, String firstUserAgent, String firstIpAddress, 
                      String firstScreenResolution) {
        this.sessionId = sessionId;
        this.firstUserAgent = firstUserAgent;
        this.firstIpAddress = firstIpAddress;
        this.firstScreenResolution = firstScreenResolution;
        this.startedAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }
    
    // 便捷方法
    
    /**
     * 更新最后活跃时间
     */
    public void updateLastActive() {
        this.lastActiveAt = LocalDateTime.now();
    }
    
    /**
     * 结束会话
     */
    public void endSession() {
        this.endedAt = LocalDateTime.now();
        updateLastActive();
    }
    
    /**
     * 标记为完成
     */
    public void markAsCompleted(SoulPortrait portrait) {
        this.isCompleted = true;
        this.portrait = portrait;
        endSession();
    }
    
    /**
     * 增加事件计数
     */
    public void incrementEventCount() {
        this.totalEvents = (totalEvents != null ? totalEvents : 0) + 1;
        updateLastActive();
    }
    
    /**
     * 增加问题计数
     */
    public void incrementQuestionCount() {
        this.totalQuestions = (totalQuestions != null ? totalQuestions : 0) + 1;
        updateLastActive();
    }
    
    /**
     * 更新会话时长
     */
    public void updateTotalTime() {
        if (startedAt != null && lastActiveAt != null) {
            this.totalTimeSeconds = (int) java.time.Duration.between(startedAt, lastActiveAt).getSeconds();
        }
    }
    
    /**
     * 获取会话时长（秒）
     */
    public long getSessionDurationSeconds() {
        if (startedAt == null) return 0;
        
        LocalDateTime endTime = (endedAt != null) ? endedAt : lastActiveAt;
        if (endTime == null) return 0;
        
        return java.time.Duration.between(startedAt, endTime).getSeconds();
    }
    
    /**
     * 获取会话时长的友好显示
     */
    public String getDurationDisplay() {
        long seconds = getSessionDurationSeconds();
        
        if (seconds < 60) {
            return seconds + "秒";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            long remainingSeconds = seconds % 60;
            if (remainingSeconds == 0) {
                return minutes + "分钟";
            } else {
                return minutes + "分" + remainingSeconds + "秒";
            }
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            if (minutes == 0) {
                return hours + "小时";
            } else {
                return hours + "小时" + minutes + "分";
            }
        }
    }
    
    /**
     * 判断会话是否活跃（最后活跃在30分钟内）
     */
    public boolean isActive() {
        if (lastActiveAt == null) return false;
        if (endedAt != null) return false;
        
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        return lastActiveAt.isAfter(thirtyMinutesAgo);
    }
    
    /**
     * 获取会话状态
     */
    public String getStatus() {
        if (endedAt != null) {
            return "已结束";
        } else if (isActive()) {
            return "活跃中";
        } else {
            return "闲置";
        }
    }
    
    /**
     * 获取设备类型
     */
    public String getDeviceType() {
        if (firstUserAgent == null) {
            return "未知";
        }
        
        String userAgent = firstUserAgent.toLowerCase();
        if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
            return "手机";
        } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
            return "平板";
        } else {
            return "桌面";
        }
    }
    
    /**
     * 获取完成率
     */
    public double getCompletionRate() {
        if (totalQuestions == null || totalQuestions == 0) {
            return 0.0;
        }
        
        // 假设总问题是5个
        int totalAvailableQuestions = 5;
        return (double) totalQuestions / totalAvailableQuestions;
    }
    
    /**
     * 获取完成率百分比
     */
    public String getCompletionRatePercentage() {
        double rate = getCompletionRate();
        return String.format("%.1f%%", Math.min(rate * 100, 100));
    }
    
    /**
     * 获取平均每个事件的耗时
     */
    public double getAverageTimePerEvent() {
        if (totalEvents == null || totalEvents == 0 || totalTimeSeconds == null) {
            return 0.0;
        }
        return (double) totalTimeSeconds / totalEvents;
    }
    
    /**
     * 获取平均每个问题的耗时
     */
    public double getAverageTimePerQuestion() {
        if (totalQuestions == null || totalQuestions == 0 || totalTimeSeconds == null) {
            return 0.0;
        }
        return (double) totalTimeSeconds / totalQuestions;
    }
    
    @Override
    public String toString() {
        return String.format("UserSession{id=%d, sessionId='%s', startedAt=%s, isCompleted=%s}", 
                id, sessionId, startedAt, isCompleted);
    }
}