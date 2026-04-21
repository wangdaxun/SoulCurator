package com.soulcurator.backend.model.gateway;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 灵魂之门入口实体
 * 对应数据库中的soul_gateways表，存储灵魂之门入口的配置信息
 */
@Data
@Entity
@Table(name = "soul_gateways", indexes = {
    @Index(name = "idx_gateways_type", columnList = "gatewayType"),
    @Index(name = "idx_gateways_active", columnList = "isActive"),
    @Index(name = "idx_gateways_order", columnList = "displayOrder"),
    @Index(name = "idx_gateways_popularity", columnList = "popularityScore DESC")
})
public class SoulGateway {
    
    @Id
    @Column(name = "id", length = 50)
    private String id; // 入口类型ID，如'movie', 'literature'
    
    // 基本信息
    @Column(name = "name", nullable = false, length = 100)
    private String name; // 入口名称，如'电影之门'
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description; // 入口描述
    
    @Column(name = "color_hex", length = 7)
    private String colorHex; // 颜色代码，如'#8B5CF6'
    
    @Column(name = "icon", length = 50)
    private String icon; // 图标名称，如'clapperboard'
    
    // 分类信息
    @Column(name = "gateway_type", nullable = false, length = 50)
    private String gatewayType; // 入口类型：movie/literature/music/game
    
    @Column(name = "category", length = 50)
    private String category; // 分类：art/entertainment/knowledge
    
    // 显示配置
    @Column(name = "display_order")
    private Integer displayOrder = 0; // 显示顺序
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 是否启用
    
    // 统计信息
    @Column(name = "popularity_score")
    private Integer popularityScore = 0; // 热度评分
    
    @Column(name = "completion_rate", columnDefinition = "DECIMAL(5,2)")
    private BigDecimal completionRate; // 完成率
    
    // AI扩展字段
    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    private String aiPrompt; // AI生成提示词
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "target_dimensions", columnDefinition = "jsonb")
    private String targetDimensions; // 目标维度
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}