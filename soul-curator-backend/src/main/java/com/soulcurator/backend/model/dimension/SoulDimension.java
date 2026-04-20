package com.soulcurator.backend.model.dimension;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 灵魂维度定义实体
 * 对应数据库中的soul_dimensions表，定义16个灵魂维度的属性
 */
@Data
@Entity
@Table(name = "soul_dimensions", indexes = {
    @Index(name = "idx_dimensions_category", columnList = "category"),
    @Index(name = "idx_dimensions_order", columnList = "displayOrder")
})
public class SoulDimension {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    @Column(name = "english_name", nullable = false, length = 50)
    private String englishName;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "icon", length = 50)
    private String icon;
    
    @Column(name = "color_hex", length = 7)
    private String colorHex;
    
    // 分组信息
    @Column(name = "category", length = 50)
    private String category; // perception/thinking/emotion/social/order/reality/expression
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}