package com.soulcurator.backend.model.entity;

import com.soulcurator.backend.model.enums.WorkType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 作品实体
 * 代表游戏、电影、书籍等文艺作品
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "works", indexes = {
    @Index(name = "idx_work_title", columnList = "title"),
    @Index(name = "idx_work_type", columnList = "type"),
    @Index(name = "idx_work_external", columnList = "externalSource, externalId")
})
public class WorkEntity extends BaseEntity {
    
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType type;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "cover_url")
    private String coverUrl;
    
    @Column(name = "release_year")
    private Integer releaseYear;
    
    @Column(name = "release_date")
    private LocalDate releaseDate;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;  // 电影/游戏时长（分钟）
    
    @Column(name = "page_count")
    private Integer pageCount;  // 书籍页数
    
    @Column(name = "external_id")
    private String externalId;  // 外部ID（豆瓣ID、SteamID等）
    
    @Column(name = "external_source")
    private String externalSource;  // 外部来源：DOUBAN, STEAM, GOODREADS
    
    @Column(name = "external_url")
    private String externalUrl;
    
    @Column(name = "rating_score")
    private Double ratingScore;  // 评分（0-10）
    
    @Column(name = "rating_count")
    private Integer ratingCount;  // 评分人数
    
    // 情感标签（多对多）
    @ManyToMany
    @JoinTable(
        name = "work_emotions",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "emotion_id")
    )
    private Set<TagEntity> emotions = new HashSet<>();
    
    // 主题标签（多对多）
    @ManyToMany
    @JoinTable(
        name = "work_themes",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "theme_id")
    )
    private Set<TagEntity> themes = new HashSet<>();
    
    // 风格标签（多对多）
    @ManyToMany
    @JoinTable(
        name = "work_styles",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "style_id")
    )
    private Set<TagEntity> styles = new HashSet<>();
    
    // 关联作品（续集、前传、改编等）
    @ManyToMany
    @JoinTable(
        name = "work_relations",
        joinColumns = @JoinColumn(name = "work_id"),
        inverseJoinColumns = @JoinColumn(name = "related_work_id")
    )
    private Set<WorkEntity> relatedWorks = new HashSet<>();
    
    @Column(name = "ai_analysis_json", columnDefinition = "TEXT")
    private String aiAnalysisJson;  // AI分析结果的JSON存储
    
    @Column(name = "popularity_score")
    private Double popularityScore = 0.0;  // 热度分数
    
    @Column(name = "depth_score")
    private Double depthScore = 0.0;  // 深度分数（情感/主题复杂度）
}