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
 * 选项实体类
 * 对应数据库表: options
 */
@Entity
@Table(name = "options")
@Data
public class Option {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    /**
     * 关联的问题
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    /**
     * 选项标题
     */
    @Column(name = "title", nullable = false, length = 100)
    private String title;
    
    /**
     * 表情符号
     */
    @Column(name = "emoji", length = 10)
    private String emoji;
    
    /**
     * 选项描述
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    /**
     * 参考作品数组
     */
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "work_references", columnDefinition = "text[]")
    private List<String> workReferences = new ArrayList<>();
    
    /**
     * 维度得分（JSON格式）
     * 示例: {"visual": 3, "rational": 2, "emotional": 1}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_scores", nullable = false, columnDefinition = "jsonb")
    private Map<String, Integer> dimensionScores;
    
    /**
     * 显示顺序
     */
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    /**
     * 是否启用
     */
    @Column(name = "is_active")
    private Boolean isActive = true;
    
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
     * 用户选择此选项的记录
     */
    @OneToMany(mappedBy = "option", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserSelection> userSelections = new ArrayList<>();
    
    // 构造方法
    public Option() {}
    
    public Option(String id, Question question, String title, String description) {
        this.id = id;
        this.question = question;
        this.title = title;
        this.description = description;
        this.isActive = true;
    }
    
    public Option(String id, Question question, String title, String emoji, String description) {
        this.id = id;
        this.question = question;
        this.title = title;
        this.emoji = emoji;
        this.description = description;
        this.isActive = true;
    }
    
    // 便捷方法
    
    /**
     * 获取带表情的标题
     */
    public String getTitleWithEmoji() {
        if (emoji != null && !emoji.isEmpty()) {
            return emoji + " " + title;
        }
        return title;
    }
    
    /**
     * 获取完整的描述（包含参考作品）
     */
    public String getFullDescription() {
        StringBuilder sb = new StringBuilder(description);
        
        if (workReferences != null && !workReferences.isEmpty()) {
            sb.append("\n\n参考作品: ");
            for (int i = 0; i < workReferences.size(); i++) {
                if (i > 0) sb.append("、");
                sb.append(workReferences.get(i));
            }
        }
        
        return sb.toString();
    }
    
    /**
     * 获取简化的描述（截断）
     */
    public String getShortDescription(int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * 获取维度得分的字符串表示
     */
    public String getDimensionScoresString() {
        if (dimensionScores == null || dimensionScores.isEmpty()) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Integer> entry : dimensionScores.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append(": ").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * 获取该选项被选择的次数
     */
    public long getSelectionCount() {
        if (userSelections == null) return 0;
        return userSelections.size();
    }
    
    /**
     * 获取该选项的受欢迎程度（0-100）
     */
    public int getPopularityScore() {
        if (question == null || question.getUserSelections() == null) return 0;
        
        long totalSelections = question.getUserSelections().size();
        if (totalSelections == 0) return 0;
        
        long optionSelections = getSelectionCount();
        return (int) ((optionSelections * 100) / totalSelections);
    }
    
    @Override
    public String toString() {
        return String.format("Option{id='%s', title='%s', questionId=%d}", 
                id, title, question != null ? question.getId() : null);
    }
}