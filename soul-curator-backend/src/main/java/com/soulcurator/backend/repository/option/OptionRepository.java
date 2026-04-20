package com.soulcurator.backend.repository.option;

import com.soulcurator.backend.model.option.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 选项数据仓库
 */
@Repository
public interface OptionRepository extends JpaRepository<Option, String> {
    
    /**
     * 根据问题ID查找选项
     */
    List<Option> findByQuestionId(Long questionId);
    
    /**
     * 根据问题ID查找选项，按显示顺序排序
     */
    List<Option> findByQuestionIdOrderByDisplayOrderAsc(Long questionId);
    
    /**
     * 根据多个问题ID查找选项
     */
    List<Option> findByQuestionIdIn(List<Long> questionIds);
    
    /**
     * 根据入口类型查找选项（通过关联questions表）
     */
    @Query("SELECT o FROM Option o JOIN Question q ON o.questionId = q.id " +
           "WHERE q.gatewayType = :gatewayType AND q.isActive = true")
    List<Option> findByGatewayType(@Param("gatewayType") String gatewayType);
    
    /**
     * 根据入口类型和问题ID查找选项
     */
    @Query("SELECT o FROM Option o JOIN Question q ON o.questionId = q.id " +
           "WHERE q.gatewayType = :gatewayType AND o.questionId = :questionId " +
           "AND q.isActive = true ORDER BY o.displayOrder ASC")
    List<Option> findByGatewayTypeAndQuestionId(@Param("gatewayType") String gatewayType, 
                                                @Param("questionId") Long questionId);
    
    /**
     * 根据入口类型和步骤编号查找选项
     */
    @Query("SELECT o FROM Option o JOIN Question q ON o.questionId = q.id " +
           "WHERE q.gatewayType = :gatewayType AND q.stepNumber = :stepNumber " +
           "AND q.isActive = true ORDER BY o.displayOrder ASC")
    List<Option> findByGatewayTypeAndStepNumber(@Param("gatewayType") String gatewayType, 
                                                @Param("stepNumber") Integer stepNumber);
    
    /**
     * 统计指定问题ID的选项数量
     */
    long countByQuestionId(Long questionId);
    
    /**
     * 检查选项ID是否存在
     */
    boolean existsById(String optionId);
    
    /**
     * 根据选项ID列表查找选项
     */
    List<Option> findByIdIn(List<String> optionIds);
}