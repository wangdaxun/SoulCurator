package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 问题实体类
 * 对应数据库表: questions
 */
@Entity
@Table(name = "questions")
@Data
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 步骤编号（1-5）
     */
    @Column(name = "step_number", unique = true, nullable = false)
    private Integer stepNumber;
    
    /**
     * 问题标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    /**
     * 问题副标题
     */
    @Column(name = "subtitle", length = 500)
    private String subtitle;
    
    /**
     * 问题详细描述
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    /**
     * 维度映射（JSON格式）
     * 示例: {"visual": 2, "rational": 1, "emotional": 3}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_mapping", columnDefinition = "jsonb")
    private Map<String, Integer> dimensionMapping;
    
    /**
     * 是否启用
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    /**
     * 显示顺序
     */
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
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
    
    // 关联关系
    
    /**
     * 问题的选项
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<Option> options = new ArrayList<>();
    
    /**
     * 用户对该问题的选择记录
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSelection> userSelections = new ArrayList<>();
    
    // 构造方法
    public Question() {}
    
    public Question(Integer stepNumber, String title) {
        this.stepNumber = stepNumber;
        this.title = title;
        this.isActive = true;
    }
    
    public Question(Integer stepNumber, String title, String subtitle, String description) {
        this.stepNumber = stepNumber;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.isActive = true;
    }
    
    // 便捷方法
    
    /**
     * 获取可用的选项数量
     */
    public int getAvailableOptionsCount() {
        if (options == null) return 0;
        return (int) options.stream()
                .filter(Option::getIsActive)
                .count();
    }
    
    /**
     * 获取按显示顺序排序的选项
     */
    public List<Option> getSortedOptions() {
        if (options == null) return new ArrayList<>();
        return options.stream()
                .sorted((a, b) -> {
                    int orderCompare = Integer.compare(
                            a.getDisplayOrder() != null ? a.getDisplayOrder() : 0,
                            b.getDisplayOrder() != null ? b.getDisplayOrder() : 0
                    );
                    if (orderCompare != 0) return orderCompare;
                    return a.getId().compareTo(b.getId());
                })
                .toList();
    }
    
    /**
     * 获取活跃的选项
     */
    public List<Option> getActiveOptions() {
        if (options == null) return new ArrayList<>();
        return options.stream()
                .filter(Option::getIsActive)
                .toList();
    }
    
    /**
     * 获取问题完整标题（包含步骤）
     */
    public String getFullTitle() {
        return String.format("第%d步: %s", stepNumber, title);
    }
    
    /**
     * 检查是否是有效步骤
     */
    public boolean isValidStep() {
        return stepNumber != null && stepNumber >= 1 && stepNumber <= 20;
    }
    
    @Override
    public String toString() {
        return String.format("Question{id=%d, step=%d, title='%s'}", 
                id, stepNumber, title);
    }
}