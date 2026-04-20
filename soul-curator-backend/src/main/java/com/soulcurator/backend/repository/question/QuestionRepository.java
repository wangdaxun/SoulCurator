package com.soulcurator.backend.repository.question;

import com.soulcurator.backend.model.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 问题数据仓库
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    /**
     * 根据步骤编号查找问题
     */
    Optional<Question> findByStepNumber(Integer stepNumber);
    
    /**
     * 根据是否启用查找问题
     */
    List<Question> findByIsActiveTrue();
    
    /**
     * 根据是否启用和入口类型查找问题
     */
    List<Question> findByIsActiveTrueAndGatewayType(String gatewayType);
    
    /**
     * 根据入口类型查找问题
     */
    List<Question> findByGatewayType(String gatewayType);
    
    /**
     * 根据入口类型和是否启用查找问题，按步骤编号排序
     */
    List<Question> findByGatewayTypeAndIsActiveTrueOrderByStepNumberAsc(String gatewayType);
    
    /**
     * 根据入口类型查找问题，按步骤编号排序
     */
    List<Question> findByGatewayTypeOrderByStepNumberAsc(String gatewayType);
    
    /**
     * 根据显示顺序查找问题
     */
    List<Question> findAllByOrderByDisplayOrderAsc();
    
    /**
     * 查找指定步骤范围内的问题
     */
    List<Question> findByStepNumberBetween(Integer startStep, Integer endStep);
    
    /**
     * 根据入口类型查找指定步骤范围内的问题
     */
    List<Question> findByGatewayTypeAndStepNumberBetween(String gatewayType, Integer startStep, Integer endStep);
    
    /**
     * 统计指定入口类型的问题数量
     */
    long countByGatewayType(String gatewayType);
    
    /**
     * 统计指定入口类型且启用的问题数量
     */
    long countByGatewayTypeAndIsActiveTrue(String gatewayType);
    
    /**
     * 查找下一个步骤的问题
     */
    @Query("SELECT q FROM Question q WHERE q.gatewayType = :gatewayType AND q.stepNumber > :currentStep " +
           "AND q.isActive = true ORDER BY q.stepNumber ASC LIMIT 1")
    Optional<Question> findNextQuestion(@Param("gatewayType") String gatewayType, 
                                        @Param("currentStep") Integer currentStep);
    
    /**
     * 查找上一个步骤的问题
     */
    @Query("SELECT q FROM Question q WHERE q.gatewayType = :gatewayType AND q.stepNumber < :currentStep " +
           "AND q.isActive = true ORDER BY q.stepNumber DESC LIMIT 1")
    Optional<Question> findPreviousQuestion(@Param("gatewayType") String gatewayType, 
                                            @Param("currentStep") Integer currentStep);
    
    /**
     * 根据入口类型查找最大步骤编号
     */
    @Query("SELECT MAX(q.stepNumber) FROM Question q WHERE q.gatewayType = :gatewayType AND q.isActive = true")
    Optional<Integer> findMaxStepNumberByGatewayType(@Param("gatewayType") String gatewayType);
    
    /**
     * 根据入口类型查找最小步骤编号
     */
    @Query("SELECT MIN(q.stepNumber) FROM Question q WHERE q.gatewayType = :gatewayType AND q.isActive = true")
    Optional<Integer> findMinStepNumberByGatewayType(@Param("gatewayType") String gatewayType);
    
    /**
     * 检查指定入口类型和步骤编号的问题是否存在
     */
    boolean existsByGatewayTypeAndStepNumber(String gatewayType, Integer stepNumber);
}