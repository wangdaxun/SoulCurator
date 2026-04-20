package com.soulcurator.backend.model.config;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 系统配置实体
 * 对应数据库中的system_configs表，存储系统运行时配置
 */
@Data
@Entity
@Table(name = "system_configs", indexes = {
    @Index(name = "idx_configs_public", columnList = "isPublic")
})
public class SystemConfig {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value", nullable = false, columnDefinition = "jsonb")
    private String value;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}