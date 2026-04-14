package com.soulcurator.backend.repository;

import com.soulcurator.backend.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 系统配置数据访问接口
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    /**
     * 根据配置键查找配置
     */
    Optional<SystemConfig> findByConfigKey(String configKey);
    
    /**
     * 根据配置键前缀查找配置
     */
    List<SystemConfig> findByConfigKeyStartingWith(String prefix);
    
    /**
     * 根据配置组查找配置
     */
    List<SystemConfig> findByConfigGroup(String configGroup);
    
    /**
     * 查找所有配置组
     */
    @Query("SELECT DISTINCT s.configGroup FROM SystemConfig s WHERE s.configGroup IS NOT NULL")
    List<String> findAllConfigGroups();
    
    /**
     * 查找活跃配置
     */
    List<SystemConfig> findByIsActiveTrue();
    
    /**
     * 根据配置类型查找配置
     */
    List<SystemConfig> findByConfigType(String configType);
    
    /**
     * 查找可编辑的配置
     */
    List<SystemConfig> findByIsEditableTrue();
    
    /**
     * 查找需要重启的配置
     */
    List<SystemConfig> findByRequiresRestartTrue();
    
    /**
     * 根据配置键搜索配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE LOWER(s.configKey) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SystemConfig> searchByKeyOrDescription(@Param("keyword") String keyword);
    
    /**
     * 统计每个配置组的配置数量
     */
    @Query("SELECT s.configGroup, COUNT(s) FROM SystemConfig s WHERE s.configGroup IS NOT NULL GROUP BY s.configGroup")
    List<Object[]> countByConfigGroup();
    
    /**
     * 统计每种配置类型的数量
     */
    @Query("SELECT s.configType, COUNT(s) FROM SystemConfig s GROUP BY s.configType")
    List<Object[]> countByConfigType();
    
    /**
     * 查找系统级配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configGroup = 'system' OR s.configGroup IS NULL")
    List<SystemConfig> findSystemConfigs();
    
    /**
     * 查找业务级配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configGroup NOT IN ('system', 'security') AND s.configGroup IS NOT NULL")
    List<SystemConfig> findBusinessConfigs();
    
    /**
     * 查找安全相关配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configGroup = 'security'")
    List<SystemConfig> findSecurityConfigs();
    
    /**
     * 根据配置键批量查找配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configKey IN :keys")
    List<SystemConfig> findByConfigKeys(@Param("keys") List<String> keys);
    
    /**
     * 更新配置值
     */
    @Query("UPDATE SystemConfig s SET s.configValue = :value, s.updatedAt = CURRENT_TIMESTAMP WHERE s.configKey = :key")
    void updateConfigValue(@Param("key") String key, @Param("value") String value);
    
    /**
     * 检查配置键是否存在
     */
    boolean existsByConfigKey(String configKey);
    
    /**
     * 查找需要刷新的配置（缓存相关）
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.requiresCacheRefresh = true")
    List<SystemConfig> findConfigsRequiringCacheRefresh();
    
    /**
     * 查找数值型配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configType = 'number' OR s.configType = 'integer' OR s.configType = 'float'")
    List<SystemConfig> findNumericConfigs();
    
    /**
     * 查找布尔型配置
     */
    @Query("SELECT s FROM SystemConfig s WHERE s.configType = 'boolean'")
    List<SystemConfig> findBooleanConfigs();
}