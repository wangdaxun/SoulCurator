package com.soulcurator.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分析实体
 * 记录对作品的分析（可以是用户分析或AI分析）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "analyses", indexes = {
    @Index(name = "idx_analysis_work", columnList = "work_id"),
    @Index(name = "idx_analysis_user", columnList = "user_id"),
    @Index(name = "idx_analysis_type", columnList = "analysis_type")
})
public class AnalysisEntity extends BaseEntity {
    
    public enum AnalysisType {
        USER,           // 用户分析
        AI_AUTO,        // AI自动分析
        AI_ENHANCED,    // AI增强分析
        COMMUNITY       // 社区共识分析
    }
    
    public enum AnalysisSource {
        DOUBAN,         // 豆瓣
        STEAM,          // Steam
        GOODREADS,      // Goodreads
        IMDB,           // IMDB
        CUSTOM          // 自定义
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private WorkEntity work;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;  // 可以为空（AI分析）
    
    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false)
    private AnalysisType analysisType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "source")
    private AnalysisSource source;
    
    @Column(name = "source_id")
    private String sourceId;  // 来源ID
    
    @Column(columnDefinition = "TEXT")
    private String content;  // 分析内容
    
    @Column(name = "summary", columnDefinition = "TEXT")
    private String summary;  // 摘要
    
    // 情感分析结果
    @Column(name = "primary_emotion")
    private String primaryEmotion;  // 主要情感
    
    @Column(name = "secondary_emotions_json", columnDefinition = "TEXT")
    private String secondaryEmotionsJson;  // 次要情感（JSON数组）
    
    @Column(name = "emotion_intensity")
    private Double emotionIntensity;  // 情感强度 0-10
    
    // 主题分析结果
    @Column(name = "primary_theme")
    private String primaryTheme;  // 主要主题
    
    @Column(name = "secondary_themes_json", columnDefinition = "TEXT")
    private String secondaryThemesJson;  // 次要主题（JSON数组）
    
    @Column(name = "theme_complexity")
    private Double themeComplexity;  // 主题复杂度 0-10
    
    // 风格分析
    @Column(name = "style_tags_json", columnDefinition = "TEXT")
    private String styleTagsJson;  // 风格标签（JSON数组）
    
    // 深度分析
    @Column(name = "philosophical_depth")
    private Double philosophicalDepth;  // 哲学深度 0-10
    
    @Column(name = "psychological_insight")
    private Double psychologicalInsight;  // 心理洞察 0-10
    
    @Column(name = "social_relevance")
    private Double socialRelevance;  // 社会相关性 0-10
    
    // 推荐相关
    @Column(name = "target_audience_json", columnDefinition = "TEXT")
    private String targetAudienceJson;  // 目标受众（JSON）
    
    @Column(name = "similar_works_json", columnDefinition = "TEXT")
    private String similarWorksJson;  // 相似作品（JSON数组）
    
    @Column(name = "recommended_next_json", columnDefinition = "TEXT")
    private String recommendedNextJson;  // 推荐下一步（JSON）
    
    // 元数据
    @Column(name = "confidence_score")
    private Double confidenceScore;  // 置信度 0-1
    
    @Column(name = "model_version")
    private String modelVersion;  // AI模型版本
    
    @Column(name = "analysis_duration_ms")
    private Long analysisDurationMs;  // 分析耗时（毫秒）
    
    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;  // 是否已验证
    
    @Column(name = "verification_score")
    private Double verificationScore;  // 验证分数（社区投票等）
    
    @Column(name = "like_count")
    private Integer likeCount = 0;
    
    @Column(name = "comment_count")
    private Integer commentCount = 0;
    
    @Column(name = "share_count")
    private Integer shareCount = 0;
}