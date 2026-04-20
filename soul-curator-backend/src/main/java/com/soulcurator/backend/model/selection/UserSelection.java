package com.soulcurator.backend.model.selection;

import com.soulcurator.backend.model.option.Option;
import com.soulcurator.backend.model.question.Question;
import com.soulcurator.backend.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 用户选择记录实体
 * 对应数据库中的user_selections表，记录用户在每个问题上的选择
 */
@Data
@Entity
@Table(name = "user_selections", indexes = {
    @Index(name = "idx_selections_user", columnList = "userId"),
    @Index(name = "idx_selections_session", columnList = "sessionId"),
    @Index(name = "idx_selections_gateway", columnList = "gateway_type"),
    @Index(name = "idx_selections_created", columnList = "createdAt")
})
public class UserSelection {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 关联信息
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;
    
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    
    @Column(name = "gateway_type", nullable = false, length = 20)
    private String gatewayType;
    
    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", insertable = false, updatable = false)
    private Question question;
    
    @Column(name = "option_id", nullable = false, length = 50)
    private String optionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", insertable = false, updatable = false)
    private Option option;
    
    // 选择上下文
    @Column(name = "step_number", nullable = false)
    private Integer stepNumber;
    
    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;
    
    // AI扩展字段
    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private String metadata; // 扩展字段，存储用户选择的额外信息
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}