package com.soulcurator.backend.model.recommendation;

import com.soulcurator.backend.model.portrait.SoulPortrait;
import com.soulcurator.backend.model.user.User;
import com.soulcurator.backend.model.work.RecommendedWork;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 个性化推荐实体
 * 对应数据库中的personalized_recommendations表，为用户生成的个性化推荐
 */
@Data
@Entity
@Table(name = "personalized_recommendations", indexes = {
    @Index(name = "idx_recommendations_portrait", columnList = "portraitId"),
    @Index(name = "idx_recommendations_user", columnList = "userId"),
    @Index(name = "idx_recommendations_work", columnList = "workId"),
    @Index(name = "idx_recommendations_match_score", columnList = "matchScore DESC")
})
public class PersonalizedRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 关联信息
    @Column(name = "portrait_id", nullable = false)
    private Long portraitId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portrait_id", insertable = false, updatable = false)
    private SoulPortrait portrait;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Column(name = "work_id", nullable = false)
    private Long workId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", insertable = false, updatable = false)
    private RecommendedWork work;
    
    // 推荐信息
    @Column(name = "recommendation_reason", nullable = false, columnDefinition = "TEXT")
    private String recommendationReason;
    
    @Column(name = "match_score", precision = 5, scale = 2)
    private BigDecimal matchScore;
    
    // 用户反馈
    @Column(name = "is_viewed")
    private Boolean isViewed = false;
    
    @Column(name = "is_saved")
    private Boolean isSaved = false;
    
    @Column(name = "feedback_rating")
    private Integer feedbackRating;
    
    @Column(name = "feedback_comment", columnDefinition = "TEXT")
    private String feedbackComment;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}