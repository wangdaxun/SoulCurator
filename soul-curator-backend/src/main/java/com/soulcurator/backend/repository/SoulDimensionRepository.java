package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.SoulDimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 灵魂维度数据访问接口
 */
@Repository
public interface SoulDimensionRepository extends JpaRepository<SoulDimension, Long> {
    
    /**
     * 根据维度代码查找维度
     */
    Optional<SoulDimension> findByCode(String code);
    
    /**
     * 根据维度名称查找维度
     */
    Optional<SoulDimension> findByName(String name);
    
    /**
     * 查找所有维度并按代码排序
     */
    List<SoulDimension> findAllByOrderByCodeAsc();
    
    /**
     * 查找活跃维度
     */
    List<SoulDimension> findByIsActiveTrue();
    
    /**
     * 根据维度类型查找维度
     */
    List<SoulDimension> findByDimensionType(String dimensionType);
    
    /**
     * 查找核心维度
     */
    @Query("SELECT d FROM SoulDimension d WHERE d.isCore = true ORDER BY d.code")
    List<SoulDimension> findCoreDimensions();
    
    /**
     * 查找衍生维度
     */
    @Query("SELECT d FROM SoulDimension d WHERE d.isCore = false ORDER BY d.code")
    List<SoulDimension> findDerivedDimensions();
    
    /**
     * 根据权重范围查找维度
     */
    List<SoulDimension> findByWeightBetween(Double minWeight, Double maxWeight);
    
    /**
     * 根据名称关键词搜索维度
     */
    @Query("SELECT d FROM SoulDimension d WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SoulDimension> searchByNameOrDescription(@Param("keyword") String keyword);
    
    /**
     * 统计维度数量
     */
    @Query("SELECT d.dimensionType, COUNT(d) FROM SoulDimension d GROUP BY d.dimensionType")
    List<Object[]> countDimensionsByType();
    
    /**
     * 查找权重最高的维度
     */
    @Query("SELECT d FROM SoulDimension d ORDER BY d.weight DESC LIMIT :limit")
    List<SoulDimension> findTopDimensionsByWeight(@Param("limit") int limit);
    
    /**
     * 检查维度代码是否存在
     */
    boolean existsByCode(String code);
}