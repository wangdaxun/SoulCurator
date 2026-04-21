package com.soulcurator.backend.model.prototype;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 灵魂原型配置实体
 * 对应数据库表: soul_prototypes
 */
@Entity
@Table(name = "soul_prototypes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoulPrototype {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(name = "description_template", nullable = false, columnDefinition = "TEXT")
    private String descriptionTemplate;
    
    @Column(name = "icon", length = 50)
    private String icon;
    
    @Column(name = "color_hex", length = 7)
    @Builder.Default
    private String colorHex = "#8b5cf6";
    
    @Column(name = "dimension_weights", nullable = false, columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private String dimensionWeights;
    
    @Column(name = "traits", nullable = false, columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private String traits;
    
    @Column(name = "quotes", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private String quotes;
    
    // AI扩展字段
    @Column(name = "ai_prompt_template", columnDefinition = "TEXT")
    private String aiPromptTemplate;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
    
    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}