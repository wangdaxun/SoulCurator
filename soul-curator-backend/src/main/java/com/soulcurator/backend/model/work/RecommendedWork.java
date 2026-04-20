package com.soulcurator.backend.model.work;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 推荐作品实体
 * 对应数据库中的recommended_works表，存储可推荐的作品信息
 */
@Data
@Entity
@Table(name = "recommended_works", indexes = {
    @Index(name = "idx_works_type", columnList = "type"),
    @Index(name = "idx_works_popularity", columnList = "popularityScore DESC"),
    @Index(name = "idx_works_quality", columnList = "qualityScore DESC")
})
public class RecommendedWork {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 作品信息
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "original_title", length = 200)
    private String originalTitle;
    
    @Column(name = "type", nullable = false, length = 20)
    private String type; // movie/book/music/game
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "year")
    private Integer year;
    
    @Column(name = "country", length = 50)
    private String country;
    
    @Column(name = "language", length = 50)
    private String language;
    
    // 媒体信息
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
    
    @Column(name = "trailer_url", length = 500)
    private String trailerUrl;
    
    @Column(name = "external_link", length = 500)
    private String externalLink;
    
    // 标签和分类
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    private String tags;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "genres", columnDefinition = "jsonb")
    private String genres;
    
    // 灵魂维度匹配
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_mapping", columnDefinition = "jsonb")
    private String dimensionMapping;
    
    // 元数据
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "popularity_score")
    private Integer popularityScore = 0;
    
    @Column(name = "quality_score")
    private Integer qualityScore = 0;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}