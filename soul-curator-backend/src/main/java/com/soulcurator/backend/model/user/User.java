package com.soulcurator.backend.model.user;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 用户实体
 * 对应数据库中的users表，支持匿名和注册用户
 */
@Data
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_session_id", columnList = "sessionId"),
    @Index(name = "idx_users_created_at", columnList = "createdAt"),
    @Index(name = "idx_users_is_active", columnList = "isActive")
})
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 用户标识
    @Column(name = "session_id", unique = true, nullable = false, length = 64)
    private String sessionId;
    
    @Column(name = "username", unique = true, length = 50)
    private String username;
    
    @Column(name = "email", unique = true, length = 100)
    private String email;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    // 用户状态
    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = true;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_active_at")
    private LocalDateTime lastActiveAt;
}