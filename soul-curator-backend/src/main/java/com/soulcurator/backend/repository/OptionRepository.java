package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 选项数据访问接口
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    
    /**
     * 根据问题ID查找选项
     */
    List<Option> findByQuestionId(Long questionId);
    
    /**
     * 根据问题ID和选项顺序查找
     */
    List<Option> findByQuestionIdOrderByOptionOrderAsc(Long questionId);
    
    /**
     * 根据维度ID查找选项
     */
    List<Option> findByDimensionId(Long dimensionId);
    
    /**
     * 根据问题ID和维度ID查找选项
     */
    List<Option> findByQuestionIdAndDimensionId(Long questionId, Long dimensionId);
    
    /**
     * 查找指定问题中影响特定维度的选项
     */
    @Query("SELECT o FROM Option o WHERE o.question.id = :questionId AND o.dimension.id = :dimensionId ORDER BY o.optionOrder")
    List<Option> findOptionsByQuestionAndDimension(
            @Param("questionId") Long questionId,
            @Param("dimensionId") Long dimensionId);
    
    /**
     * 统计每个问题的选项数量
     */
    @Query("SELECT o.question.id, COUNT(o) FROM Option o GROUP BY o.question.id")
    List<Object[]> countOptionsByQuestion();
    
    /**
     * 根据选项文本搜索
     */
    @Query("SELECT o FROM Option o WHERE LOWER(o.text) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Option> searchByText(@Param("keyword") String keyword);
    
    /**
     * 查找影响多个维度的选项
     */
    @Query("SELECT DISTINCT o FROM Option o WHERE o.dimension.id IN :dimensionIds")
    List<Option> findByDimensionIds(@Param("dimensionIds") List<Long> dimensionIds);
    
    /**
     * 查找指定问题中权重最高的选项
     */
    @Query("SELECT o FROM Option o WHERE o.question.id = :questionId ORDER BY o.weight DESC LIMIT 1")
    Option findTopWeightedOptionByQuestion(@Param("questionId") Long questionId);
}