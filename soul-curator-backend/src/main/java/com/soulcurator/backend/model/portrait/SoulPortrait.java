package com.soulcurator.backend.model.portrait;

import com.soulcurator.backend.model.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * 灵魂画像实体
 * 对应数据库中的soul_portraits表，用户完成探索后生成的灵魂画像
 */
@Data
@Entity
@Table(name = "soul_portraits", indexes = {
    @Index(name = "idx_portraits_user", columnList = "userId"),
    @Index(name = "idx_portraits_session", columnList = "sessionId"),
    @Index(name = "idx_portraits_generated", columnList = "generatedAt"),
    @Index(name = "idx_portraits_soul_type", columnList = "soulType")
})
public class SoulPortrait {
    
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
    
    // 画像信息
    @Column(name = "soul_type", nullable = false, length = 100)
    private String soulType;
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "quote_text", columnDefinition = "TEXT")
    private String quoteText;
    
    @Column(name = "quote_author", length = 100)
    private String quoteAuthor;
    
    // 维度得分
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "dimension_scores", nullable = false, columnDefinition = "jsonb")
    private String dimensionScores;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "top_dimensions", columnDefinition = "jsonb")
    private String topDimensions;
    
    // 生成信息
    @Column(name = "total_questions")
    private Integer totalQuestions = 5;
    
    @Column(name = "completed_questions")
    private Integer completedQuestions = 5;
    
    @Column(name = "total_time_seconds")
    private Integer totalTimeSeconds;
    
    // 时间戳
    @CreationTimestamp
    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;
}