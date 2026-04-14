package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 个性化推荐实体类
 * 对应数据库表: personalized_recommendations
 */
@Entity
@Table(name = "personalized_recommendations", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"portrait_id", "work_id"}))
@Data
public class PersonalizedRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的灵魂画像
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portrait_id", nullable = false)
    private SoulPortrait portrait;
    
    /**
     * 关联的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 关联的推荐作品
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_id", nullable = false)
    private RecommendedWork work;
    
    /**
     * 推荐理由
     */
    @Column(name = "recommendation_reason", nullable = false, columnDefinition = "TEXT")
    private String recommendationReason;
    
    /**
     * 匹配度分数（0-100）
     */
    @Column(name = "match_score", precision = 5, scale = 2)
    private Double matchScore;
    
    /**
     * 用户是否查看
     */
    @Column(name = "is_viewed")
    private Boolean isViewed = false;
    
    /**
     * 用户是否收藏
     */
    @Column(name = "is_saved")
    private Boolean isSaved = false;
    
    /**
     * 用户评分（1-5）
     */
    @Column(name = "feedback_rating")
    private Integer feedbackRating;
    
    /**
     * 用户评论
     */
    @Column(name = "feedback_comment", columnDefinition = "TEXT")
    private String feedbackComment;
    
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
    
    // 构造方法
    public PersonalizedRecommendation() {}
    
    public PersonalizedRecommendation(SoulPortrait portrait, RecommendedWork work, String recommendationReason) {
        this.portrait = portrait;
        this.user = portrait.getUser();
        this.work = work;
        this.recommendationReason = recommendationReason;
        this.createdAt = LocalDateTime.now();
    }
    
    public PersonalizedRecommendation(SoulPortrait portrait, RecommendedWork work, 
                                     String recommendationReason, Double matchScore) {
        this.portrait = portrait;
        this.user = portrait.getUser();
        this.work = work;
        this.recommendationReason = recommendationReason;
        this.matchScore = matchScore;
        this.createdAt = LocalDateTime.now();
    }
    
    // 便捷方法
    
    /**
     * 获取匹配度的友好显示
     */
    public String getMatchScoreDisplay() {
        if (matchScore == null) {
            return "未评分";
        }
        return String.format("%.1f", matchScore);
    }
    
    /**
     * 获取匹配度等级
     */
    public String getMatchLevel() {
        if (matchScore == null) {
            return "未知";
        }
        
        if (matchScore >= 90) return "极高匹配";
        if (matchScore >= 80) return "高度匹配";
        if (matchScore >= 70) return "中度匹配";
        if (matchScore >= 60) return "轻度匹配";
        return "低度匹配";
    }
    
    /**
     * 获取匹配度颜色（用于UI显示）
     */
    public String getMatchColor() {
        if (matchScore == null) {
            return "#9CA3AF"; // 灰色
        }
        
        if (matchScore >= 90) return "#10B981"; // 绿色
        if (matchScore >= 80) return "#3B82F6"; // 蓝色
        if (matchScore >= 70) return "#8B5CF6"; // 紫色
        if (matchScore >= 60) return "#F59E0B"; // 橙色
        return "#EF4444"; // 红色
    }
    
    /**
     * 获取用户反馈的星级显示
     */
    public String getFeedbackStars() {
        if (feedbackRating == null) {
            return "☆☆☆☆☆";
        }
        
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            stars.append(i <= feedbackRating ? "★" : "☆");
        }
        return stars.toString();
    }
    
    /**
     * 获取简化的推荐理由（截断）
     */
    public String getShortReason(int maxLength) {
        if (recommendationReason.length() <= maxLength) {
            return recommendationReason;
        }
        return recommendationReason.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * 获取完整的推荐信息
     */
    public String getFullRecommendation() {
        return String.format("【%s】%s（匹配度：%s）", 
                work != null ? work.getTypeDisplay() : "未知类型",
                recommendationReason,
                getMatchScoreDisplay());
    }
    
    /**
     * 判断是否有用户反馈
     */
    public boolean hasFeedback() {
        return feedbackRating != null || 
               (feedbackComment != null && !feedbackComment.isEmpty());
    }
    
    /**
     * 获取反馈摘要
     */
    public String getFeedbackSummary() {
        if (!hasFeedback()) {
            return "暂无反馈";
        }
        
        StringBuilder sb = new StringBuilder();
        if (feedbackRating != null) {
            sb.append("评分：").append(getFeedbackStars());
        }
        if (feedbackComment != null && !feedbackComment.isEmpty()) {
            if (sb.length() > 0) sb.append("，");
            sb.append("评论：").append(
                    feedbackComment.length() > 50 ? 
                    feedbackComment.substring(0, 47) + "..." : 
                    feedbackComment
            );
        }
        return sb.toString();
    }
    
    /**
     * 更新用户查看状态
     */
    public void markAsViewed() {
        this.isViewed = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新用户收藏状态
     */
    public void toggleSaved() {
        this.isSaved = !this.isSaved;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 提交用户反馈
     */
    public void submitFeedback(Integer rating, String comment) {
        this.feedbackRating = rating;
        this.feedbackComment = comment;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 验证推荐是否有效
     */
    public boolean isValid() {
        return portrait != null && 
               user != null && 
               work != null && 
               recommendationReason != null && 
               !recommendationReason.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("PersonalizedRecommendation{id=%d, portraitId=%d, workId=%d, matchScore=%.1f}", 
                id, portrait != null ? portrait.getId() : null, 
                work != null ? work.getId() : null, matchScore);
    }
}