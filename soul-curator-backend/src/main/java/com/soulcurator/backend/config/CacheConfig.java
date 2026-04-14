package com.soulcurator.backend.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 缓存配置
 */
@Configuration
@EnableCaching
public class CacheConfig {
    
    /**
     * 配置缓存管理器
     * 这里使用简单的内存缓存，生产环境可以考虑使用Redis
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        
        // 配置缓存名称
        cacheManager.setCacheNames(List.of(
                "eventStats",          // 事件统计缓存
                "popularPages",        // 热门页面缓存
                "userSessions",        // 用户会话缓存
                "dimensionScores",     // 维度得分缓存
                "recommendations",     // 推荐缓存
                "systemConfigs",       // 系统配置缓存
                "analyticsHealth",     // 分析服务健康状态缓存
                "eventTypes",          // 事件类型缓存
                "deviceStats",         // 设备统计缓存
                "retentionRates"       // 留存率缓存
        ));
        
        // 可以在这里配置缓存过期时间等
        // cacheManager.setCacheBuilder(...)
        
        return cacheManager;
    }
    
    /**
     * 缓存配置说明：
     * 
     * 1. eventStats - 事件统计缓存
     *    key: "stats:{startTime}:{endTime}" 或 "stats:recent:{timeWindow}"
     *    value: 统计结果Map
     *    TTL: 5分钟
     * 
     * 2. popularPages - 热门页面缓存
     *    key: "popular:pages:{timeRange}:{limit}"
     *    value: 页面访问排名列表
     *    TTL: 10分钟
     * 
     * 3. userSessions - 用户会话缓存
     *    key: "session:{sessionId}" 或 "user:{userId}:sessions"
     *    value: 会话信息或会话列表
     *    TTL: 30分钟
     * 
     * 4. dimensionScores - 维度得分缓存
     *    key: "user:{userId}:dimensions" 或 "session:{sessionId}:dimensions"
     *    value: 维度得分Map
     *    TTL: 1小时
     * 
     * 5. recommendations - 推荐缓存
     *    key: "user:{userId}:recommendations" 或 "session:{sessionId}:recommendations"
     *    value: 推荐列表
     *    TTL: 15分钟
     * 
     * 6. systemConfigs - 系统配置缓存
     *    key: "config:{configKey}" 或 "config:group:{groupName}"
     *    value: 配置值
     *    TTL: 1小时（或配置变更时失效）
     * 
     * 7. analyticsHealth - 分析服务健康状态缓存
     *    key: "health:analytics"
     *    value: 健康状态信息
     *    TTL: 1分钟
     * 
     * 8. eventTypes - 事件类型缓存
     *    key: "event:types"
     *    value: 事件类型列表
     *    TTL: 1天
     * 
     * 9. deviceStats - 设备统计缓存
     *    key: "stats:devices:{timeRange}"
     *    value: 设备统计Map
     *    TTL: 10分钟
     * 
     * 10. retentionRates - 留存率缓存
     *    key: "retention:{startDate}:{endDate}"
     *    value: 留存率数据
     *    TTL: 1小时
     */
    
    // 示例：如果需要更复杂的缓存配置，可以创建多个CacheManager
    /*
    @Bean
    public CacheManager shortTermCacheManager() {
        // 短期缓存配置
    }
    
    @Bean
    public CacheManager longTermCacheManager() {
        // 长期缓存配置
    }
    */
    
    // 示例：Redis缓存配置（生产环境推荐）
    /*
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .withInitialCacheConfigurations(getCacheConfigurations())
                .build();
    }
    
    private Map<String, RedisCacheConfiguration> getCacheConfigurations() {
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        
        // 为不同的缓存设置不同的TTL
        configMap.put("eventStats", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)));
        
        configMap.put("popularPages", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)));
        
        configMap.put("userSessions", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)));
        
        return configMap;
    }
    */
}