package com.soulcurator.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_username", columnList = "username"),
    @Index(name = "idx_user_email", columnList = "email")
})
public class UserEntity extends BaseEntity implements UserDetails {
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(name = "nickname")
    private String nickname;
    
    @Column(name = "avatar_url")
    private String avatarUrl;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.USER;
    
    public enum UserRole {
        USER,       // 普通用户
        CURATOR,    // 策展人（可以创建标签、写分析）
        ADMIN       // 管理员
    }
    
    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;
    
    @Column(name = "is_locked", nullable = false)
    private Boolean isLocked = false;
    
    // 用户偏好的情感标签
    @ManyToMany
    @JoinTable(
        name = "user_preferred_emotions",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "emotion_id")
    )
    private Set<TagEntity> preferredEmotions = new HashSet<>();
    
    // 用户偏好的主题标签
    @ManyToMany
    @JoinTable(
        name = "user_preferred_themes",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<TagEntity> preferredThemes = new HashSet<>();
    
    // 用户收藏的作品
    @ManyToMany
    @JoinTable(
        name = "user_favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "work_id")
    )
    private Set<WorkEntity> favoriteWorks = new HashSet<>();
    
    // 用户评分记录（通过中间表）
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<UserRatingEntity> ratings = new HashSet<>();
    
    // 用户分析记录
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<AnalysisEntity> analyses = new HashSet<>();
    
    // 用户推荐记录
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<RecommendationEntity> recommendations = new HashSet<>();
    
    // 用户画像JSON（AI生成的用户偏好分析）
    @Column(name = "profile_json", columnDefinition = "TEXT")
    private String profileJson;
    
    // 用户探索进度（记录用户的文艺探索深度）
    @Column(name = "exploration_score")
    private Double explorationScore = 0.0;
    
    // Spring Security 接口实现
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(() -> "ROLE_" + role.name());
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}