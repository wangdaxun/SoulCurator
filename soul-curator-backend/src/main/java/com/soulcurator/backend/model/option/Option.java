package com.soulcurator.backend.model.option;

import com.soulcurator.backend.model.question.Question;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 选项实体
 * 对应数据库中的options表，每个问题的可选答案
 */
@Data
@Entity
@Table(name = "options", indexes = {
    @Index(name = "idx_options_question", columnList = "questionId"),
    @Index(name = "idx_options_order", columnList = "displayOrder")
})
public class Option {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    // 关联信息
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
    
    // 选项内容
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    @Column(name = "emoji", length = 10)
    private String emoji;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "work_references", columnDefinition = "text[]")
    private String[] workReferences;
    
    // 维度得分（选择此选项对各个维度的贡献）
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_scores", nullable = false, columnDefinition = "jsonb")
    private String dimensionScores;
    
    // AI扩展字段
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weight", columnDefinition = "jsonb")
    private String weight; // 权重配置，用于分支计算
    
    @Column(name = "next_question_id", length = 36)
    private String nextQuestionId; // UUID格式，固定流程的下一个问题
    
    @Column(name = "ai_context", columnDefinition = "TEXT")
    private String aiContext; // AI生成上下文
    
    // 元数据
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}