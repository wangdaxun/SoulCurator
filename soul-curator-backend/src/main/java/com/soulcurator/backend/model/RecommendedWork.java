package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 推荐作品实体类
 * 对应数据库表: recommended_works
 */
@Entity
@Table(name = "recommended_works")
@Data
public class RecommendedWork {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 作品标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    /**
     * 原标题（外文）
     */
    @Column(name = "original_title", length = 200)
    private String originalTitle;
    
    /**
     * 作品类型
     * 可选值: movie, book, music, game
     */
    @Column(name = "type", nullable = false, length = 20)
    private String type;
    
    /**
     * 作品描述
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    /**
     * 发布年份
     */
    @Column(name = "year")
    private Integer year;
    
    /**
     * 国家/地区
     */
    @Column(name = "country", length = 50)
    private String country;
    
    /**
     * 语言
     */
    @Column(name = "language", length = 50)
    private String language;
    
    /**
     * 封面图URL
     */
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
    
    /**
     * 预告片URL
     */
    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;
    
    /**
     * 外部链接（豆瓣/IMDb等）
     */
    @Column(name = "external_link", length = 500)
    private String externalLink;
    
    /**
     * 标签数组
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tags", columnDefinition = "varchar(50)[]")
    private List<String> tags = new ArrayList<>();
    
    /**
     * 类型数组
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "genres", columnDefinition = "varchar(50)[]")
    private List<String> genres = new ArrayList<>();
    
    /**
     * 维度映射（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_mapping", columnDefinition = "jsonb")
    private Map<String, Integer> dimensionMapping;
    
    /**
     * 是否启用
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * 热度评分
     */
    @Column(name = "popularity_score")
    private Integer popularityScore = 0;
    
    /**
     * 质量评分
     */
    @Column(name = "quality_score")
    private Integer qualityScore = 0;
    
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
    
    // 关联关系
    
    /**
     * 个性化推荐
     */
    @OneToMany(mappedBy = "work", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PersonalizedRecommendation> recommendations = new ArrayList<>();
    
    // 构造方法
    public RecommendedWork() {}
    
    public RecommendedWork(String title, String type, String description) {
        this.title = title;
        this.type = type;
        this.description = description;
        this.isActive = true;
    }
    
    // 便捷方法
    
    /**
     * 获取显示标题（优先使用原标题）
     */
    public String getDisplayTitle() {
        if (originalTitle != null && !originalTitle.isEmpty()) {
            return originalTitle + " (" + title + ")";
        }
        return title;
    }
    
    /**
     * 获取类型的中文显示
     */
    public String getTypeDisplay() {
        return switch (type) {
            case "movie" -> "电影";
            case "book" -> "书籍";
            case "music" -> "音乐";
            case "game" -> "游戏";
            default -> type;
        };
    }
    
    /**
     * 获取类型的图标
     */
    public String getTypeIcon() {
        return switch (type) {
            case "movie" -> "🎬";
            case "book" -> "📚";
            case "music" -> "🎵";
            case "game" -> "🎮";
            default -> "📌";
        };
    }
    
    /**
     * 获取带图标和类型的完整标题
     */
    public String getFullTitle() {
        return getTypeIcon() + " " + getDisplayTitle() + " [" + getTypeDisplay() + "]";
    }
    
    /**
     * 获取简化的描述（截断）
     */
    public String getShortDescription(int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * 获取标签的字符串表示
     */
    public String getTagsString() {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        return String.join("、", tags);
    }
    
    /**
     * 获取类型的字符串表示
     */
    public String getGenresString() {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        return String.join("、", genres);
    }
    
    /**
     * 获取综合评分（0-100）
     */
    public int getCompositeScore() {
        int popScore = popularityScore != null ? popularityScore : 0;
        int qualScore = qualityScore != null ? qualityScore : 0;
        
        // 权重：质量60%，热度40%
        return (int) (qualScore * 0.6 + popScore * 0.4);
    }
    
    /**
     * 获取评分星级（1-5星）
     */
    public int getStarRating() {
        int score = getCompositeScore();
        if (score >= 90) return 5;
        if (score >= 80) return 4;
        if (score >= 70) return 3;
        if (score >= 60) return 2;
        return 1;
    }
    
    /**
     * 获取推荐次数
     */
    public long getRecommendationCount() {
        if (recommendations == null) return 0;
        return recommendations.size();
    }
    
    /**
     * 获取平均匹配度
     */
    public double getAverageMatchScore() {
        if (recommendations == null || recommendations.isEmpty()) {
            return 0.0;
        }
        
        return recommendations.stream()
                .filter(r -> r.getMatchScore() != null)
                .mapToDouble(PersonalizedRecommendation::getMatchScore)
                .average()
                .orElse(0.0);
    }
    
    /**
     * 检查是否包含特定标签
     */
    public boolean hasTag(String tag) {
        if (tags == null) return false;
        return tags.contains(tag);
    }
    
    /**
     * 检查是否包含特定类型
     */
    public boolean hasGenre(String genre) {
        if (genres == null) return false;
        return genres.contains(genre);
    }
    
    @Override
    public String toString() {
        return String.format("RecommendedWork{id=%d, title='%s', type='%s'}", 
                id, title, type);
    }
}