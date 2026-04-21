package com.soulcurator.backend.repository.portrait;

import com.soulcurator.backend.model.portrait.SoulPortrait;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     * 根据会话ID查找灵魂画像
     */
    Optional<SoulPortrait> findBySessionId(String sessionId);
    
    /**
     * 根据用户ID查找灵魂画像列表
     */
    List<SoulPortrait> findByUserId(Long userId);
    
    /**
     * 根据用户ID查找最新的灵魂画像
     */
    @Query("SELECT p FROM SoulPortrait p WHERE p.userId = :userId ORDER BY p.generatedAt DESC")
    List<SoulPortrait> findLatestByUserId(Long userId, org.springframework.data.domain.Pageable pageable);
    
    /**
     * 根据灵魂类型查找画像
     */
    List<SoulPortrait> findBySoulType(String soulType);
    
    /**
     * 统计指定时间范围内的画像数量
     */
    long countByGeneratedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 查找匹配度高于指定值的画像
     */
    List<SoulPortrait> findByMatchScoreGreaterThanEqual(Double minScore);
    
    /**
     * 根据生成方法查找画像
     */
    List<SoulPortrait> findByGenerationMethod(String generationMethod);
    
    /**
     * 检查会话是否已有画像
     */
    boolean existsBySessionId(String sessionId);
    
    /**
     * 根据用户ID和会话ID查找画像
     */
    Optional<SoulPortrait> findByUserIdAndSessionId(Long userId, String sessionId);
    
    /**
     * 删除指定会话的画像
     */
    void deleteBySessionId(String sessionId);
}