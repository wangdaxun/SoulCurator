package com.soulcurator.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签实体
 * 用于标记作品的情感、主题、风格等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "tags", indexes = {
    @Index(name = "idx_tag_name", columnList = "name"),
    @Index(name = "idx_tag_category", columnList = "category")
})
public class TagEntity extends BaseEntity {
    
    public enum TagCategory {
        EMOTION,      // 情感标签
        THEME,        // 主题标签
        STYLE,        // 风格标签
        GENRE,        // 类型标签
        CHARACTER,    // 角色标签
        SETTING       // 设定标签
    }
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TagCategory category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "parent_id")
    private Long parentId;  // 父标签ID，用于标签层级
    
    @Column(name = "color_code")
    private String colorCode;  // 标签颜色（用于前端展示）
    
    @Column(name = "icon_url")
    private String iconUrl;  // 标签图标
    
    @Column(name = "usage_count")
    private Integer usageCount = 0;  // 使用次数
    
    @Column(name = "ai_generated", nullable = false)
    private Boolean aiGenerated = false;  // 是否为AI生成
    
    @Column(name = "confidence_score")
    private Double confidenceScore = 0.0;  // AI置信度分数
    
    // 关联的情感类型（如果category=EMOTION）
    @Column(name = "emotion_type")
    private String emotionType;
    
    // 关联的主题类型（如果category=THEME）
    @Column(name = "theme_type")
    private String themeType;
    
    // 标签权重（用于推荐算法）
    @Column(name = "weight")
    private Double weight = 1.0;
    
    // 标签相似度矩阵（JSON格式存储与其他标签的相似度）
    @Column(name = "similarity_matrix_json", columnDefinition = "TEXT")
    private String similarityMatrixJson;
}