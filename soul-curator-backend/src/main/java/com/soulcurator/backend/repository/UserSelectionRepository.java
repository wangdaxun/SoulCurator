package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.UserSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户选择记录数据访问接口
 */
@Repository
public interface UserSelectionRepository extends JpaRepository<UserSelection, Long> {
    
    /**
     * 根据用户ID查找选择记录
     */
    List<UserSelection> findByUserId(Long userId);
    
    /**
     * 根据用户ID和问题ID查找选择记录
     */
    List<UserSelection> findByUserIdAndQuestionId(Long userId, Long questionId);
    
    /**
     * 根据用户ID和选项ID查找选择记录
     */
    Optional<UserSelection> findByUserIdAndOptionId(Long userId, Long optionId);
    
    /**
     * 根据用户ID和步骤编号查找选择记录
     */
    @Query("SELECT s FROM UserSelection s WHERE s.user.id = :userId AND s.question.stepNumber = :stepNumber")
    List<UserSelection> findByUserIdAndStepNumber(
            @Param("userId") Long userId,
            @Param("stepNumber") Integer stepNumber);
    
    /**
     * 统计用户的选择数量
     */
    @Query("SELECT COUNT(s) FROM UserSelection s WHERE s.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户在每个步骤的选择数量
     */
    @Query("SELECT s.question.stepNumber, COUNT(s) FROM UserSelection s WHERE s.user.id = :userId GROUP BY s.question.stepNumber")
    List<Object[]> countSelectionsByStepForUser(@Param("userId") Long userId);
    
    /**
     * 查找用户最近的选择记录
     */
    List<UserSelection> findByUserIdOrderBySelectedAtDesc(Long userId);
    
    /**
     * 查找指定时间范围内的选择记录
     */
    List<UserSelection> findBySelectedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 根据用户ID和维度ID查找影响特定维度的选择
     */
    @Query("SELECT s FROM UserSelection s WHERE s.user.id = :userId AND s.option.dimension.id = :dimensionId")
    List<UserSelection> findByUserIdAndDimensionId(
            @Param("userId") Long userId,
            @Param("dimensionId") Long dimensionId);
    
    /**
     * 查找用户未完成的步骤（已选择的问题步骤）
     */
    @Query("SELECT DISTINCT s.question.stepNumber FROM UserSelection s WHERE s.user.id = :userId")
    List<Integer> findCompletedStepsByUserId(@Param("userId") Long userId);
    
    /**
     * 检查用户是否已回答过某个问题
     */
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM UserSelection s WHERE s.user.id = :userId AND s.question.id = :questionId")
    boolean existsByUserIdAndQuestionId(@Param("userId") Long userId, @Param("questionId") Long questionId);
    
    /**
     * 查找用户选择最多的选项
     */
    @Query("SELECT s.option.id, COUNT(s) as count FROM UserSelection s WHERE s.user.id = :userId GROUP BY s.option.id ORDER BY count DESC LIMIT :limit")
    List<Object[]> findTopSelectedOptionsByUser(@Param("userId") Long userId, @Param("limit") int limit);
}