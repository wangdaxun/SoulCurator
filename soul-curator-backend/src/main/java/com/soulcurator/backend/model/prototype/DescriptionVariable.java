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
 * 描述变量实体
 * 对应数据库表: description_variables
 */
@Entity
@Table(name = "description_variables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescriptionVariable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "variable_key", nullable = false, length = 50)
    private String variableKey;
    
    @Column(name = "variable_type", nullable = false, length = 20)
    private String variableType;
    
    @Column(name = "values", nullable = false, columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private String values;
    
    @Column(name = "dimension", length = 50)
    private String dimension;
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
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