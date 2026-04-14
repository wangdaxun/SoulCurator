package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 问题数据访问接口
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * 根据步骤编号查找问题
     */
    List<Question> findByStepNumber(Integer stepNumber);
    
    /**
     * 根据步骤编号排序查找问题
     */
    List<Question> findAllByOrderByStepNumberAsc();
    
    /**
     * 查找指定步骤范围内的问题
     */
    List<Question> findByStepNumberBetween(Integer startStep, Integer endStep);
    
    /**
     * 统计每个步骤的问题数量
     */
    @Query("SELECT q.stepNumber, COUNT(q) FROM Question q GROUP BY q.stepNumber ORDER BY q.stepNumber")
    List<Object[]> countQuestionsByStep();
    
    /**
     * 根据标题关键词搜索问题
     */
    @Query("SELECT q FROM Question q WHERE LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Question> searchByTitle(@Param("keyword") String keyword);
    
    /**
     * 查找下一个步骤的问题
     */
    @Query("SELECT q FROM Question q WHERE q.stepNumber = :currentStep + 1 ORDER BY q.id")
    List<Question> findNextStepQuestions(@Param("currentStep") Integer currentStep);
    
    /**
     * 查找指定步骤的第一个问题
     */
    @Query("SELECT q FROM Question q WHERE q.stepNumber = :stepNumber ORDER BY q.id LIMIT 1")
    Question findFirstByStepNumber(@Param("stepNumber") Integer stepNumber);
}