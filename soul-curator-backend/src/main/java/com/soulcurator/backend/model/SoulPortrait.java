package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 灵魂画像实体类
 * 对应数据库表: soul_portraits
 */
@Entity
@Table(name = "soul_portraits")
@Data
public class SoulPortrait {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 会话ID
     */
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    
    /**
     * 灵魂类型
     */
    @Column(name = "soul_type", nullable = false, length = 100)
    private String soulType;
    
    /**
     * 灵魂描述
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    /**
     * 引用文本
     */
    @Column(name = "quote_text", columnDefinition = "TEXT")
    private String quoteText;
    
    /**
     * 引用作者
     */
    @Column(name = "quote_author", length = 100)
    private String quoteAuthor;
    
    /**
     * 维度得分（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_scores", nullable = false, columnDefinition = "jsonb")
    private Map<String, Integer> dimensionScores;
    
    /**
     * 得分最高的3个维度
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "top_dimensions", columnDefinition = "varchar(50)[]")
    private List<String> topDimensions = new ArrayList<>();
    
    /**
     * 总问题数
     */
    @Column(name = "total_questions")
    private Integer totalQuestions = 5;
    
    /**
     * 完成问题数
     */
    @Column(name = "completed_questions")
    private Integer completedQuestions = 5;
    
    /**
     * 总耗时（秒）
     */
    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;
    
    /**
     * 生成时间
     */
    @CreationTimestamp
    @Column(name = "generated_at", updatable = false)
    private LocalDateTime generatedAt;
    
    // 关联关系
    
    /**
     * 个性化推荐
     */
    @OneToMany(mappedBy = "portrait", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonalizedRecommendation> recommendations = new ArrayList<>();
    
    /**
     * 关联的会话
     */
    @OneToOne(mappedBy = "portrait", fetch = FetchType.LAZY)
    private UserSession userSession;
    
    // 构造方法
    public SoulPortrait() {}
    
    public SoulPortrait(User user, String soulType, String description) {
        this.user = user;
        this.sessionId = user.getSessionId();
        this.soulType = soulType;
        this.description = description;
        this.generatedAt = LocalDateTime.now();
    }
    
    // 便捷方法
    
    /**
     * 获取完整的引用
     */
    public String getFullQuote() {
        if (quoteText == null || quoteText.isEmpty()) {
            return "";
        }
        if (quoteAuthor != null && !quoteAuthor.isEmpty()) {
            return "「" + quoteText + "」—— " + quoteAuthor;
        }
        return "「" + quoteText + "」";
    }
    
    /**
     * 获取维度得分的字符串表示
     */
    public String getDimensionScoresString() {
        if (dimensionScores == null || dimensionScores.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 获取最高维度得分
     */
    public int getHighestDimensionScore() {
        if (dimensionScores == null || dimensionScores.isEmpty()) {
            return 0;
        }
        return dimensionScores.values().stream()
                .max(Integer::compare)
                .orElse(0);
    }
    
    /**
     * 获取平均维度得分
     */
    public double getAverageDimensionScore() {
        if (dimensionScores == null || dimensionScores.isEmpty()) {
            return 0.0;
        }
        return dimensionScores.values().stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
    
    /**
     * 获取完成时间的友好显示
     */
    public String getCompletionTimeDisplay() {
        if (totalTimeSeconds == null) {
            return "未知";
        }
        
        if (totalTimeSeconds < 60) {
            return totalTimeSeconds + "秒";
        } else {
            int minutes = totalTimeSeconds / 60;
            int seconds = totalTimeSeconds % 60;
            if (seconds == 0) {
                return minutes + "分钟";
            } else {
                return minutes + "分" + seconds + "秒";
            }
        }
    }
    
    /**
     * 获取完成率
     */
    public double getCompletionRate() {
        if (totalQuestions == null || totalQuestions == 0) {
            return 0.0;
        }
        if (completedQuestions == null) {
            return 0.0;
        }
        return (double) completedQuestions / totalQuestions;
    }
    
    /**
     * 获取完成率百分比
     */
    public String getCompletionRatePercentage() {
        double rate = getCompletionRate();
        return String.format("%.1f%%", rate * 100);
    }
    
    /**
     * 判断是否完成所有问题
     */
    public boolean isCompleted() {
        if (totalQuestions == null || completedQuestions == null) {
            return false;
        }
        return completedQuestions >= totalQuestions;
    }
    
    /**
     * 获取推荐数量
     */
    public int getRecommendationCount() {
        if (recommendations == null) return 0;
        return recommendations.size();
    }
    
    /**
     * 获取有效的推荐（匹配度高的）
     */
    public List<PersonalizedRecommendation> getHighMatchRecommendations(double threshold) {
        if (recommendations == null) return new ArrayList<>();
        return recommendations.stream()
                .filter(r -> r.getMatchScore() != null && r.getMatchScore() >= threshold)
                .toList();
    }
    
    @Override
    public String toString() {
        return String.format("SoulPortrait{id=%d, userId=%d, soulType='%s', generatedAt=%s}", 
                id, user != null ? user.getId() : null, soulType, generatedAt);
    }
}