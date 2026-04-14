package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.PersonalizedRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 个性化推荐数据访问接口
 */
@Repository
public interface PersonalizedRecommendationRepository extends JpaRepository<PersonalizedRecommendation, Long> {
    
    /**
     * 根据用户ID查找推荐
     */
    List<PersonalizedRecommendation> findByUserId(Long userId);
    
    /**
     * 根据用户ID和作品ID查找推荐
     */
    Optional<PersonalizedRecommendation> findByUserIdAndWorkId(Long userId, Long workId);
    
    /**
     * 根据用户ID查找最新的推荐
     */
    @Query("SELECT r FROM PersonalizedRecommendation r WHERE r.user.id = :userId ORDER BY r.generatedAt DESC LIMIT :limit")
    List<PersonalizedRecommendation> findLatestByUserId(
            @Param("userId") Long userId,
            @Param("limit") int limit);
    
    /**
     * 根据推荐类型查找
     */
    List<PersonalizedRecommendation> findByRecommendationType(String recommendationType);
    
    /**
     * 根据用户ID和推荐类型查找
     */
    List<PersonalizedRecommendation> findByUserIdAndRecommendationType(Long userId, String recommendationType);
    
    /**
     * 查找匹配度高于阈值的推荐
     */
    List<PersonalizedRecommendation> findByMatchScoreGreaterThanEqual(Double minMatchScore);
    
    /**
     * 查找指定时间范围内生成的推荐
     */
    List<PersonalizedRecommendation> findByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据用户ID查找并按匹配度排序
     */
    List<PersonalizedRecommendation> findByUserIdOrderByMatchScoreDesc(Long userId);
    
    /**
     * 统计用户的推荐数量
     */
    @Query("SELECT COUNT(r) FROM PersonalizedRecommendation r WHERE r.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户未查看的推荐
     */
    @Query("SELECT r FROM PersonalizedRecommendation r WHERE r.user.id = :userId AND r.viewedAt IS NULL ORDER BY r.matchScore DESC")
    List<PersonalizedRecommendation> findUnviewedByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户已查看的推荐
     */
    @Query("SELECT r FROM PersonalizedRecommendation r WHERE r.user.id = :userId AND r.viewedAt IS NOT NULL ORDER BY r.viewedAt DESC")
    List<PersonalizedRecommendation> findViewedByUserId(@Param("userId") Long userId);
    
    /**
     * 查找用户已收藏的推荐
     */
    @Query("SELECT r FROM PersonalizedRecommendation r WHERE r.user.id = :userId AND r.isFavorited = true ORDER BY r.favoritedAt DESC")
    List<PersonalizedRecommendation> findFavoritedByUserId(@Param("userId") Long userId);
    
    /**
     * 查找最受欢迎的作品（被推荐次数最多）
     */
    @Query("SELECT r.work.id, COUNT(r) as count FROM PersonalizedRecommendation r GROUP BY r.work.id ORDER BY count DESC LIMIT :limit")
    List<Object[]> findMostRecommendedWorks(@Param("limit") int limit);
    
    /**
     * 查找匹配度最高的推荐
     */
    @Query("SELECT r FROM PersonalizedRecommendation r ORDER BY r.matchScore DESC LIMIT :limit")
    List<PersonalizedRecommendation> findTopByMatchScore(@Param("limit") int limit);
    
    /**
     * 根据推荐理由搜索
     */
    @Query("SELECT r FROM PersonalizedRecommendation r WHERE LOWER(r.reason) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<PersonalizedRecommendation> searchByReason(@Param("keyword") String keyword);
    
    /**
     * 统计每种推荐类型的数量
     */
    @Query("SELECT r.recommendationType, COUNT(r) FROM PersonalizedRecommendation r GROUP BY r.recommendationType")
    List<Object[]> countByRecommendationType();
    
    /**
     * 查找用户的平均匹配度
     */
    @Query("SELECT AVG(r.matchScore) FROM PersonalizedRecommendation r WHERE r.user.id = :userId")
    Double findAverageMatchScoreByUser(@Param("userId") Long userId);
    
    /**
     * 检查用户是否有未查看的推荐
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM PersonalizedRecommendation r WHERE r.user.id = :userId AND r.viewedAt IS NULL")
    boolean hasUnviewedRecommendations(@Param("userId") Long userId);
}