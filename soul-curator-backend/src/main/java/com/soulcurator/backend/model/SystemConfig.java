package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 系统配置实体类
 * 对应数据库表: system_configs
 */
@Entity
@Table(name = "system_configs")
@Data
public class SystemConfig {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    /**
     * 配置值（JSON格式）
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> value;
    
    /**
     * 配置描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 是否公开配置
     */
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造方法
    public SystemConfig() {}
    
    public SystemConfig(String id, Map<String, Object> value) {
        this.id = id;
        this.value = value;
    }
    
    public SystemConfig(String id, Map<String, Object> value, String description) {
        this.id = id;
        this.value = value;
        this.description = description;
    }
    
    public SystemConfig(String id, Map<String, Object> value, String description, Boolean isPublic) {
        this.id = id;
        this.value = value;
        this.description = description;
        this.isPublic = isPublic;
    }
    
    // 便捷方法
    
    /**
     * 获取字符串类型的配置值
     */
    public String getValueAsString() {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
    
    /**
     * 获取特定键的字符串值
     */
    public String getString(String key) {
        if (value == null) {
            return null;
        }
        Object obj = value.get(key);
        return obj != null ? obj.toString() : null;
    }
    
    /**
     * 获取特定键的整数值
     */
    public Integer getInteger(String key) {
        if (value == null) {
            return null;
        }
        Object obj = value.get(key);
        if (obj instanceof Integer) {
            return (Integer) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue();
        } else if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 获取特定键的布尔值
     */
    public Boolean getBoolean(String key) {
        if (value == null) {
            return null;
        }
        Object obj = value.get(key);
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        } else if (obj instanceof String) {
            return Boolean.parseBoolean((String) obj);
        } else if (obj instanceof Number) {
            return ((Number) obj).intValue() != 0;
        }
        return null;
    }
    
    /**
     * 获取特定键的双精度值
     */
    public Double getDouble(String key) {
        if (value == null) {
            return null;
        }
        Object obj = value.get(key);
        if (obj instanceof Double) {
            return (Double) obj;
        } else if (obj instanceof Number) {
            return ((Number) obj).doubleValue();
        } else if (obj instanceof String) {
            try {
                return Double.parseDouble((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    /**
     * 设置字符串值
     */
    public void setString(String key, String value) {
        if (this.value != null) {
            this.value.put(key, value);
        }
    }
    
    /**
     * 设置整数值
     */
    public void setInteger(String key, Integer value) {
        if (this.value != null) {
            this.value.put(key, value);
        }
    }
    
    /**
     * 设置布尔值
     */
    public void setBoolean(String key, Boolean value) {
        if (this.value != null) {
            this.value.put(key, value);
        }
    }
    
    /**
     * 设置双精度值
     */
    public void setDouble(String key, Double value) {
        if (this.value != null) {
            this.value.put(key, value);
        }
    }
    
    /**
     * 检查配置是否包含特定键
     */
    public boolean containsKey(String key) {
        return value != null && value.containsKey(key);
    }
    
    /**
     * 获取配置的大小（键值对数量）
     */
    public int size() {
        return value != null ? value.size() : 0;
    }
    
    /**
     * 判断配置是否为空
     */
    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
    
    /**
     * 获取所有键
     */
    public java.util.Set<String> keySet() {
        if (value == null) {
            return java.util.Collections.emptySet();
        }
        return value.keySet();
    }
    
    /**
     * 获取所有值
     */
    public java.util.Collection<Object> values() {
        if (value == null) {
            return java.util.Collections.emptyList();
        }
        return value.values();
    }
    
    /**
     * 获取配置的JSON字符串
     */
    public String toJsonString() {
        if (value == null) {
            return "{}";
        }
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            return value.toString();
        }
    }
    
    /**
     * 从JSON字符串解析配置值
     */
    public void parseFromJson(String json) {
        if (json == null || json.isEmpty()) {
            this.value = java.util.Collections.emptyMap();
            return;
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            this.value = mapper.readValue(json, java.util.Map.class);
        } catch (Exception e) {
            this.value = java.util.Collections.emptyMap();
        }
    }
    
    /**
     * 更新配置值
     */
    public void updateValue(Map<String, Object> newValue) {
        if (this.value == null) {
            this.value = new java.util.HashMap<>();
        }
        this.value.putAll(newValue);
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 合并配置值
     */
    public void mergeValue(Map<String, Object> otherValue) {
        if (otherValue == null || otherValue.isEmpty()) {
            return;
        }
        
        if (this.value == null) {
            this.value = new java.util.HashMap<>(otherValue);
        } else {
            // 深度合并
            for (Map.Entry<String, Object> entry : otherValue.entrySet()) {
                if (entry.getValue() instanceof Map && this.value.get(entry.getKey()) instanceof Map) {
                    // 如果是Map，递归合并
                    Map<String, Object> existingMap = (Map<String, Object>) this.value.get(entry.getKey());
                    Map<String, Object> newMap = (Map<String, Object>) entry.getValue();
                    existingMap.putAll(newMap);
                } else {
                    this.value.put(entry.getKey(), entry.getValue());
                }
            }
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return String.format("SystemConfig{id='%s', size=%d, isPublic=%s}", 
                id, size(), isPublic);
    }
}