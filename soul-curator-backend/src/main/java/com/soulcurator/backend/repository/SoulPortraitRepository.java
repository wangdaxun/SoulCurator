package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.SoulPortrait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 灵魂画像数据访问接口
 */
@Repository
public interface SoulPortraitRepository extends JpaRepository<SoulPortrait, Long> {
    
    /**
     * 根据用户ID查找灵魂画像
     */
    List<SoulPortrait> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找最新的灵魂画像
     */
    @Query("SELECT p FROM SoulPortrait p WHERE p.user.id = :userId ORDER BY p.generatedAt DESC LIMIT 1")
    Optional<SoulPortrait> findLatestByUserId(@Param("userId") Long userId);
    
    /**
     * 根据用户ID和画像类型查找
     */
    List<SoulPortrait> findByUserIdAndPortraitType(Long userId, String portraitType);
    
    /**
     * 查找指定时间范围内生成的画像
     */
    List<SoulPortrait> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据用户ID查找并按生成时间排序
     */
    List<SoulPortrait> findByUserIdOrderByGeneratedAtDesc(Long userId);
    
    /**
     * 统计用户的画像数量
     */
    @Query("SELECT COUNT(p) FROM SoulPortrait p WHERE p.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 查找包含特定维度的画像
     */
    @Query("SELECT DISTINCT p FROM SoulPortrait p JOIN p.dimensionScores ds WHERE ds.dimension.id = :dimensionId")
    List<SoulPortrait> findByDimensionId(@Param("dimensionId") Long dimensionId);
    
    /**
     * 查找置信度高于阈值的画像
     */
    List<SoulPortrait> findByConfidenceScoreGreaterThanEqual(Double minConfidence);
    
    /**
     * 查找完整性高于阈值的画像
     */
    List<SoulPortrait> findByCompletenessScoreGreaterThanEqual(Double minCompleteness);
    
    /**
     * 根据画像摘要搜索
     */
    @Query("SELECT p FROM SoulPortrait p WHERE LOWER(p.summary) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SoulPortrait> searchBySummary(@Param("keyword") String keyword);
    
    /**
     * 查找用户的完整画像（完整性>=1.0）
     */
    @Query("SELECT p FROM SoulPortrait p WHERE p.user.id = :userId AND p.completenessScore >= 1.0 ORDER BY p.generatedAt DESC")
    List<SoulPortrait> findCompletePortraitsByUser(@Param("userId") Long userId);
    
    /**
     * 查找所有用户的平均置信度
     */
    @Query("SELECT AVG(p.confidenceScore) FROM SoulPortrait p")
    Double findAverageConfidenceScore();
    
    /**
     * 查找每个用户的最近画像
     */
    @Query("SELECT p.user.id, p FROM SoulPortrait p WHERE p.generatedAt = (SELECT MAX(p2.generatedAt) FROM SoulPortrait p2 WHERE p2.user.id = p.user.id)")
    List<Object[]> findLatestPortraitForEachUser();
    
    /**
     * 统计每种画像类型的数量
     */
    @Query("SELECT p.portraitType, COUNT(p) FROM SoulPortrait p GROUP BY p.portraitType")
    List<Object[]> countByPortraitType();
}