package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 用户选择记录实体类
 * 对应数据库表: user_selections
 */
@Entity
@Table(name = "user_selections", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "question_id"}))
@Data
public class UserSelection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    /**
     * 会话ID（冗余存储，便于查询）
     */
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    
    /**
     * 关联的问题
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    /**
     * 关联的选项
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;
    
    /**
     * 步骤编号
     */
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;
    
    /**
     * 花费时间（秒）
     */
    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;
    
    /**
     * 创建时间（选择时间）
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // 构造方法
    public UserSelection() {}
    
    public UserSelection(User user, Question question, Option option) {
        this.user = user;
        this.sessionId = user.getSessionId();
        this.question = question;
        this.option = option;
        this.stepNumber = question.getStepNumber();
        this.createdAt = LocalDateTime.now();
    }
    
    public UserSelection(User user, Question question, Option option, Integer timeSpentSeconds) {
        this.user = user;
        this.sessionId = user.getSessionId();
        this.question = question;
        this.option = option;
        this.stepNumber = question.getStepNumber();
        this.timeSpentSeconds = timeSpentSeconds;
        this.createdAt = LocalDateTime.now();
    }
    
    // 便捷方法
    
    /**
     * 获取选择的维度得分
     */
    public java.util.Map<String, Integer> getDimensionScores() {
        if (option != null && option.getDimensionScores() != null) {
            return option.getDimensionScores();
        }
        return java.util.Collections.emptyMap();
    }
    
    /**
     * 获取选择的价值描述
     */
    public String getSelectionDescription() {
        if (question == null || option == null) {
            return "未知选择";
        }
        return String.format("第%d步: 选择了「%s」", 
                stepNumber, option.getTitle());
    }
    
    /**
     * 判断是否是快速选择（小于5秒）
     */
    public boolean isQuickSelection() {
        return timeSpentSeconds != null && timeSpentSeconds < 5;
    }
    
    /**
     * 判断是否是深思熟虑的选择（大于30秒）
     */
    public boolean isThoughtfulSelection() {
        return timeSpentSeconds != null && timeSpentSeconds > 30;
    }
    
    /**
     * 获取选择时间的友好显示
     */
    public String getTimeSpentDisplay() {
        if (timeSpentSeconds == null) {
            return "未知";
        }
        
        if (timeSpentSeconds < 60) {
            return timeSpentSeconds + "秒";
        } else {
            int minutes = timeSpentSeconds / 60;
            int seconds = timeSpentSeconds % 60;
            if (seconds == 0) {
                return minutes + "分钟";
            } else {
                return minutes + "分" + seconds + "秒";
            }
        }
    }
    
    /**
     * 验证选择是否有效
     */
    public boolean isValid() {
        return user != null && 
               question != null && 
               option != null && 
               stepNumber != null && 
               stepNumber >= 1 && 
               stepNumber <= 20;
    }
    
    /**
     * 获取选择的JSON表示（用于分析）
     */
    public String toJsonString() {
        return String.format(
            "{\"step\": %d, \"optionId\": \"%s\", \"timeSpent\": %d, \"createdAt\": \"%s\"}",
            stepNumber,
            option != null ? option.getId() : "unknown",
            timeSpentSeconds != null ? timeSpentSeconds : 0,
            createdAt != null ? createdAt.toString() : ""
        );
    }
    
    @Override
    public String toString() {
        return String.format("UserSelection{id=%d, userId=%d, step=%d, optionId='%s'}", 
                id, user != null ? user.getId() : null, stepNumber, 
                option != null ? option.getId() : null);
    }
}