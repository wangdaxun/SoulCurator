package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.RecommendedWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 推荐作品数据访问接口
 */
@Repository
public interface RecommendedWorkRepository extends JpaRepository<RecommendedWork, Long> {
    
    /**
     * 根据作品类型查找
     */
    List<RecommendedWork> findByWorkType(String workType);
    
    /**
     * 根据作品类型和类别查找
     */
    List<RecommendedWork> findByWorkTypeAndCategory(String workType, String category);
    
    /**
     * 根据作品标题搜索
     */
    @Query("SELECT w FROM RecommendedWork w WHERE LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(w.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<RecommendedWork> searchByTitleOrDescription(@Param("keyword") String keyword);
    
    /**
     * 查找活跃作品
     */
    List<RecommendedWork> findByIsActiveTrue();
    
    /**
     * 根据推荐分数排序查找作品
     */
    List<RecommendedWork> findAllByOrderByRecommendationScoreDesc();
    
    /**
     * 查找推荐分数高于阈值的作品
     */
    List<RecommendedWork> findByRecommendationScoreGreaterThanEqual(Double minScore);
    
    /**
     * 根据作品类型查找并按推荐分数排序
     */
    List<RecommendedWork> findByWorkTypeOrderByRecommendationScoreDesc(String workType);
    
    /**
     * 查找指定类别中的热门作品
     */
    @Query("SELECT w FROM RecommendedWork w WHERE w.category = :category ORDER BY w.recommendationScore DESC LIMIT :limit")
    List<RecommendedWork> findTopByCategory(
            @Param("category") String category,
            @Param("limit") int limit);
    
    /**
     * 查找所有作品类型
     */
    @Query("SELECT DISTINCT w.workType FROM RecommendedWork w")
    List<String> findAllWorkTypes();
    
    /**
     * 查找所有作品类别
     */
    @Query("SELECT DISTINCT w.category FROM RecommendedWork w WHERE w.category IS NOT NULL")
    List<String> findAllCategories();
    
    /**
     * 统计每种作品类型的数量
     */
    @Query("SELECT w.workType, COUNT(w) FROM RecommendedWork w GROUP BY w.workType")
    List<Object[]> countByWorkType();
    
    /**
     * 统计每个类别的作品数量
     */
    @Query("SELECT w.category, COUNT(w) FROM RecommendedWork w WHERE w.category IS NOT NULL GROUP BY w.category")
    List<Object[]> countByCategory();
    
    /**
     * 查找与特定维度相关的作品
     */
    @Query("SELECT DISTINCT w FROM RecommendedWork w JOIN w.dimensionTags dt WHERE dt.dimension.id = :dimensionId")
    List<RecommendedWork> findByDimensionId(@Param("dimensionId") Long dimensionId);
    
    /**
     * 查找包含多个维度的作品
     */
    @Query("SELECT DISTINCT w FROM RecommendedWork w JOIN w.dimensionTags dt WHERE dt.dimension.id IN :dimensionIds")
    List<RecommendedWork> findByDimensionIds(@Param("dimensionIds") List<Long> dimensionIds);
    
    /**
     * 查找推荐分数最高的作品
     */
    @Query("SELECT w FROM RecommendedWork w ORDER BY w.recommendationScore DESC LIMIT :limit")
    List<RecommendedWork> findTopByRecommendationScore(@Param("limit") int limit);
    
    /**
     * 根据标签搜索作品
     */
    @Query("SELECT DISTINCT w FROM RecommendedWork w WHERE EXISTS (SELECT t FROM w.tags t WHERE LOWER(t) LIKE LOWER(CONCAT('%', :tag, '%')))")
    List<RecommendedWork> searchByTag(@Param("tag") String tag);
    
    /**
     * 查找最近添加的作品
     */
    List<RecommendedWork> findAllByOrderByCreatedAtDesc();
}