package com.soulcurator.backend.model.analysis;

import com.soulcurator.backend.model.user.User;
import com.soulcurator.backend.model.work.RecommendedWork;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 作品分析实体
 * 对应数据库中的analyses表，存储对推荐作品的深度分析结果
 * 注意：这是一个混合表，既用于作品分析也用于用户行为分析（通过analysis_type区分）
 */
@Data
@Entity
@Table(name = "analyses", indexes = {
    @Index(name = "idx_analysis_work", columnList = "workId"),
    @Index(name = "idx_analysis_user", columnList = "userId"),
    @Index(name = "idx_analysis_type", columnList = "analysisType")
})
public class Analysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ========== 关联信息 ==========
    @Column(name = "work_id", nullable = false)
    private Long workId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", insertable = false, updatable = false)
    private RecommendedWork work;
    
    @Column(name = "user_id")
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    // ========== 分析类型和来源 ==========
    @Column(name = "analysis_type", nullable = false, length = 255)
    private String analysisType; // USER, AI_AUTO, AI_ENHANCED, COMMUNITY
    
    @Column(name = "source", length = 255)
    private String source; // DOUBAN, STEAM, GOODREADS, IMDB, CUSTOM
    
    @Column(name = "source_id", length = 255)
    private String sourceId;
    
    // ========== 分析内容 ==========
    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;
    
    // ========== 情感分析结果 ==========
    @Column(name = "primary_emotion", length = 255)
    private String primaryEmotion;
    
    @Column(name = "secondary_emotions_json", columnDefinition = "TEXT")
    private String secondaryEmotionsJson;
    
    @Column(name = "emotion_intensity")
    private Double emotionIntensity;
    
    // ========== 主题分析结果 ==========
    @Column(name = "primary_theme", length = 255)
    private String primaryTheme;
    
    @Column(name = "secondary_themes_json", columnDefinition = "TEXT")
    private String secondaryThemesJson;
    
    @Column(name = "theme_complexity")
    private Double themeComplexity;
    
    // ========== 风格分析 ==========
    @Column(name = "style_tags_json", columnDefinition = "TEXT")
    private String styleTagsJson;
    
    // ========== 深度分析 ==========
    @Column(name = "philosophical_depth")
    private Double philosophicalDepth;
    
    @Column(name = "psychological_insight")
    private Double psychologicalInsight;
    
    @Column(name = "social_relevance")
    private Double socialRelevance;
    
    // ========== 推荐相关 ==========
    @Column(name = "target_audience_json", columnDefinition = "TEXT")
    private String targetAudienceJson;
    
    @Column(name = "similar_works_json", columnDefinition = "TEXT")
    private String similarWorksJson;
    
    @Column(name = "recommended_next_json", columnDefinition = "TEXT")
    private String recommendedNextJson;
    
    // ========== 元数据 ==========
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Column(name = "model_version", length = 255)
    private String modelVersion;
    
    @Column(name = "analysis_duration_ms")
    private Long analysisDurationMs;
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;
    
    @Column(name = "verification_score")
    private Double verificationScore;
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Column(name = "comment_count")
    private Integer commentCount = 0;
    
    @Column(name = "share_count")
    private Integer shareCount = 0;
    
    // ========== 状态字段 ==========
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    // ========== 时间戳 ==========
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // ========== 操作人字段 ==========
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "updated_by")
    private Long updatedBy;
    
    @Column(name = "deleted_by")
    private Long deletedBy;
    
    // ========== 版本控制 ==========
    @Column(name = "version", nullable = false)
    private Integer version = 1;
}