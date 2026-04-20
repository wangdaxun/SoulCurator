package com.soulcurator.backend.model.question;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 问题实体
 * 对应数据库中的questions表，存储灵魂探索的各个问题
 */
@Data
@Entity
@Table(name = "questions", indexes = {
    @Index(name = "idx_questions_step", columnList = "stepNumber"),
    @Index(name = "idx_questions_active", columnList = "isActive"),
    @Index(name = "idx_questions_order", columnList = "displayOrder")
})
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 问题信息
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "subtitle", length = 500)
    private String subtitle;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    // 维度映射（每个问题对应哪些灵魂维度）
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_mapping", columnDefinition = "jsonb")
    private String dimensionMapping;
    
    // AI扩展字段
    @Column(name = "dimension_id", length = 36)
    private String dimensionId; // UUID格式，关联灵魂维度
    
    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    private String aiPrompt; // AI生成提示词
    
    // 入口类型关联
    @Column(name = "gateway_type", length = 50)
    private String gatewayType; // 关联的灵魂之门入口类型
    
    // 元数据
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}