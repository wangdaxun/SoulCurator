package com.soulcurator.backend.repository.selection;

import com.soulcurator.backend.model.selection.UserSelection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户选择记录仓库接口
 */
@Repository
public interface UserSelectionRepository extends JpaRepository<UserSelection, Long> {
    
    /**
     * 根据会话ID查找选择记录
     */
    List<UserSelection> findBySessionId(String sessionId);
    
    /**
     * 根据会话ID和问题ID查找选择记录
     */
    Optional<UserSelection> findBySessionIdAndQuestionId(String sessionId, Long questionId);
    
    /**
     * 根据会话ID和选项ID查找选择记录
     */
    Optional<UserSelection> findBySessionIdAndOptionId(String sessionId, String optionId);
    
    /**
     * 根据会话ID和网关类型查找选择记录
     */
    List<UserSelection> findBySessionIdAndGatewayType(String sessionId, String gatewayType);
    
    /**
     * 根据用户ID查找选择记录
     */
    List<UserSelection> findByUserId(Long userId);
    
    /**
     * 根据会话ID统计选择数量
     */
    long countBySessionId(String sessionId);
    
    /**
     * 根据会话ID和网关类型统计选择数量
     */
    long countBySessionIdAndGatewayType(String sessionId, String gatewayType);
    
    /**
     * 根据会话ID获取最早的选择时间
     */
    @Query("SELECT MIN(us.createdAt) FROM UserSelection us WHERE us.sessionId = :sessionId")
    Optional<LocalDateTime> findEarliestSelectionTimeBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据会话ID获取最新的选择时间
     */
    @Query("SELECT MAX(us.createdAt) FROM UserSelection us WHERE us.sessionId = :sessionId")
    Optional<LocalDateTime> findLatestSelectionTimeBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据会话ID获取已选择的问题ID列表
     */
    @Query("SELECT DISTINCT us.questionId FROM UserSelection us WHERE us.sessionId = :sessionId")
    List<Long> findSelectedQuestionIdsBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * 根据会话ID删除所有选择记录
     */
    void deleteBySessionId(String sessionId);
    
    /**
     * 根据会话ID和网关类型删除选择记录
     */
    void deleteBySessionIdAndGatewayType(String sessionId, String gatewayType);
}