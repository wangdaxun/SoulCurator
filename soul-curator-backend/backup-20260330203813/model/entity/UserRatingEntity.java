package com.soulcurator.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户评分实体
 * 记录用户对作品的评分
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_ratings", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "work_id"}),
       indexes = {
           @Index(name = "idx_rating_user_work", columnList = "user_id, work_id"),
           @Index(name = "idx_rating_score", columnList = "score")
       })
public class UserRatingEntity extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private WorkEntity work;
    
    @Column(nullable = false)
    private Double score;  // 评分 0-10
    
    @Column(name = "emotional_impact")
    private Double emotionalImpact;  // 情感冲击力 0-10
    
    @Column(name = "thematic_depth")
    private Double thematicDepth;  // 主题深度 0-10
    
    @Column(name = "artistic_quality")
    private Double artisticQuality;  // 艺术质量 0-10
    
    @Column(name = "personal_relevance")
    private Double personalRelevance;  // 个人相关性 0-10
    
    @Column(columnDefinition = "TEXT")
    private String comment;  // 评论
    
    @Column(name = "emotion_tags_json", columnDefinition = "TEXT")
    private String emotionTagsJson;  // 用户标记的情感标签（JSON数组）
    
    @Column(name = "theme_tags_json", columnDefinition = "TEXT")
    private String themeTagsJson;  // 用户标记的主题标签（JSON数组）
    
    @Column(name = "is_public", nullable = false)
    private Boolean isPublic = true;  // 是否公开
    
    @Column(name = "analysis_json", columnDefinition = "TEXT")
    private String analysisJson;  // AI对评分的分析结果
    
    // 计算综合分数（加权平均）
    @Transient
    public Double getCompositeScore() {
        double total = score;
        int count = 1;
        
        if (emotionalImpact != null) {
            total += emotionalImpact;
            count++;
        }
        if (thematicDepth != null) {
            total += thematicDepth;
            count++;
        }
        if (artisticQuality != null) {
            total += artisticQuality;
            count++;
        }
        if (personalRelevance != null) {
            total += personalRelevance;
            count++;
        }
        
        return total / count;
    }
}