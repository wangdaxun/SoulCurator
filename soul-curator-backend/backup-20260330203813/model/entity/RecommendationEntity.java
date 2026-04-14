package com.soulcurator.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推荐实体
 * 记录系统给用户的推荐
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "recommendations", indexes = {
    @Index(name = "idx_recommendation_user", columnList = "user_id"),
    @Index(name = "idx_recommendation_work", columnList = "work_id"),
    @Index(name = "idx_recommendation_score", columnList = "recommendation_score"),
    @Index(name = "idx_recommendation_status", columnList = "status")
})
public class RecommendationEntity extends BaseEntity {
    
    public enum RecommendationType {
        EMOTION_MATCH,      // 情感匹配
        THEME_EXPLORATION,  // 主题探索
        STYLE_SIMILARITY,   // 风格相似
        COMMUNITY_TREND,    // 社区趋势
        PERSONAL_GROWTH,    // 个人成长
        SERENDIPITY         // 意外发现
    }
    
    public enum RecommendationStatus {
        PENDING,      // 待处理
        PRESENTED,    // 已展示
        VIEWED,       // 已查看
        ACCEPTED,     // 已接受（用户点击了）
        REJECTED,     // 已拒绝
        COMPLETED,    // 已完成（用户评分了）
        EXPIRED       // 已过期
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private WorkEntity work;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false)
    private RecommendationType recommendationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecommendationStatus status = RecommendationStatus.PENDING;
    
    @Column(name = "recommendation_score", nullable = false)
    private Double recommendationScore;  // 推荐分数 0-100
    
    @Column(name = "confidence_score")
    private Double confidenceScore;  // 置信度 0-1
    
    // 推荐理由
    @Column(name = "reason_title")
    private String reasonTitle;
    
    @Column(name = "reason_description", columnDefinition = "TEXT")
    private String reasonDescription;
    
    // 匹配分析
    @Column(name = "emotion_match_score")
    private Double emotionMatchScore;  // 情感匹配度
    
    @Column(name = "theme_match_score")
    private Double themeMatchScore;    // 主题匹配度
    
    @Column(name = "style_match_score")
    private Double styleMatchScore;    // 风格匹配度
    
    @Column(name = "complexity_match_score")
    private Double complexityMatchScore;  // 复杂度匹配度
    
    @Column(name = "novelty_score")
    private Double noveltyScore;  // 新颖度（避免信息茧房）
    
    @Column(name = "growth_potential_score")
    private Double growthPotentialScore;  // 成长潜力
    
    // 上下文信息
    @Column(name = "context_work_id")
    private Long contextWorkId;  // 基于哪个作品的推荐
    
    @Column(name = "context_tags_json", columnDefinition = "TEXT")
    private String contextTagsJson;  // 上下文标签（JSON）
    
    @Column(name = "user_state_json", columnDefinition = "TEXT")
    private String userStateJson;  // 用户状态快照（JSON）
    
    // 用户反馈
    @Column(name = "user_feedback_score")
    private Double userFeedbackScore;  // 用户反馈分数
    
    @Column(name = "user_feedback_comment", columnDefinition = "TEXT")
    private String userFeedbackComment;
    
    @Column(name = "feedback_emotion")
    private String feedbackEmotion;  // 反馈时的情感
    
    // 算法信息
    @Column(name = "algorithm_version")
    private String algorithmVersion;
    
    @Column(name = "model_used")
    private String modelUsed;
    
    @Column(name = "feature_vector_json", columnDefinition = "TEXT")
    private String featureVectorJson;  // 特征向量（JSON）
    
    // 时间信息
    @Column(name = "presented_at")
    private java.time.LocalDateTime presentedAt;
    
    @Column(name = "viewed_at")
    private java.time.LocalDateTime viewedAt;
    
    @Column(name = "accepted_at")
    private java.time.LocalDateTime acceptedAt;
    
    @Column(name = "completed_at")
    private java.time.LocalDateTime completedAt;
    
    @Column(name = "expires_at")
    private java.time.LocalDateTime expiresAt;
    
    // 关联的用户评分（如果用户最终评分了）
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rating_id")
    private UserRatingEntity rating;
    
    // 计算综合匹配度
    @Transient
    public Double getCompositeMatchScore() {
        double total = 0.0;
        double weight = 0.0;
        
        if (emotionMatchScore != null) {
            total += emotionMatchScore * 0.4;
            weight += 0.4;
        }
        if (themeMatchScore != null) {
            total += themeMatchScore * 0.3;
            weight += 0.3;
        }
        if (styleMatchScore != null) {
            total += styleMatchScore * 0.2;
            weight += 0.2;
        }
        if (complexityMatchScore != null) {
            total += complexityMatchScore * 0.1;
            weight += 0.1;
        }
        
        return weight > 0 ? total / weight : 0.0;
    }
}