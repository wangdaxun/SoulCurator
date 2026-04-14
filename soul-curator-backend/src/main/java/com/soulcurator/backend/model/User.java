package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户实体类
 * 对应数据库表: users
 */
@Entity
@Table(name = "users")
@Data
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 会话ID，用于匿名用户标识
     */
    @Column(name = "session_id", unique = true, nullable = false, length = 64)
    private String sessionId;
    
    /**
     * 用户名（注册用户）
     */
    @Column(name = "username", unique = true, length = 50)
    private String username;
    
    /**
     * 邮箱（注册用户）
     */
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    /**
     * 头像URL
     */
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    /**
     * 是否为匿名用户
     */
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = true;
    
    /**
     * 用户是否活跃
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * 最后活跃时间
     */
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;
    
    // 关联关系
    
    /**
     * 用户的选择记录
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSelection> selections = new ArrayList<>();
    
    /**
     * 用户的灵魂画像
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SoulPortrait> portraits = new ArrayList<>();
    
    /**
     * 用户的个性化推荐
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonalizedRecommendation> recommendations = new ArrayList<>();
    
    /**
     * 用户的事件记录
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.soulcurator.backend.model.analytics.UserEvent> events = new ArrayList<>();
    
    /**
     * 用户的会话记录
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSession> sessions = new ArrayList<>();
    
    // 构造方法
    public User() {}
    
    public User(String sessionId) {
        this.sessionId = sessionId;
        this.isAnonymous = true;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
        this.lastActiveAt = LocalDateTime.now();
    }
    
    public User(String username, String email, String sessionId) {
        this.username = username;
        this.email = email;
        this.sessionId = sessionId;
        this.isAnonymous = false;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
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
     * 判断是否为注册用户
     */
    public boolean isRegistered() {
        return !isAnonymous;
    }
    
    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        if (username != null && !username.isEmpty()) {
            return username;
        }
        return "匿名用户#" + (sessionId != null ? sessionId.substring(0, Math.min(8, sessionId.length())) : "unknown");
    }
    
    @Override
    public String toString() {
        return String.format("User{id=%d, username='%s', sessionId='%s', isAnonymous=%s}", 
                id, username, sessionId, isAnonymous);
    }
}