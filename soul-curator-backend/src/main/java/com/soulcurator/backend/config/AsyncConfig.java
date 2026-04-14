package com.soulcurator.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 异步处理配置
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    
    /**
     * 异步处理配置
     * 启用@Async注解支持
     * 
     * 可以在这里配置：
     * 1. 线程池大小
     * 2. 队列容量
     * 3. 线程名前缀
     * 4. 拒绝策略
     */
    
    // 示例：自定义线程池配置
    /*
    @Bean(name = "analyticsTaskExecutor")
    public TaskExecutor analyticsTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("analytics-async-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
    
    @Bean(name = "eventProcessingExecutor")
    public TaskExecutor eventProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("event-process-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.initialize();
        return executor;
    }
    */
    
    // 示例：为不同的异步任务指定不同的线程池
    /*
    @Async("analyticsTaskExecutor")
    public void processAnalytics() {
        // 分析任务使用analyticsTaskExecutor线程池
    }
    
    @Async("eventProcessingExecutor")
    public void processEvent() {
        // 事件处理使用eventProcessingExecutor线程池
    }
    */
}