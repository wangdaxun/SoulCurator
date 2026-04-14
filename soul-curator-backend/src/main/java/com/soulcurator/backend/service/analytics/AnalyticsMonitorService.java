package com.soulcurator.backend.service.analytics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分析服务监控器
 * 用于监控分析服务的性能指标和运行状态
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsMonitorService {
    
    private final MeterRegistry meterRegistry;
    
    // 性能指标
    private final Counter totalEventsCounter;
    private final Counter successfulEventsCounter;
    private final Counter failedEventsCounter;
    private final Timer eventProcessingTimer;
    
    // 业务指标
    private final Map<String, Counter> eventTypeCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> deviceTypeCounters = new ConcurrentHashMap<>();
    
    // 自定义指标
    private final AtomicLong activeSessions = new AtomicLong(0);
    private final AtomicLong peakConcurrentEvents = new AtomicLong(0);
    private final Map<String, AtomicLong> dimensionSelectionCounts = new ConcurrentHashMap<>();
    
    public AnalyticsMonitorService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        
        // 初始化Micrometer指标
        this.totalEventsCounter = Counter.builder("analytics.events.total")
                .description("总事件数量")
                .register(meterRegistry);
        
        this.successfulEventsCounter = Counter.builder("analytics.events.successful")
                .description("成功处理的事件数量")
                .register(meterRegistry);
        
        this.failedEventsCounter = Counter.builder("analytics.events.failed")
                .description("处理失败的事件数量")
                .register(meterRegistry);
        
        this.eventProcessingTimer = Timer.builder("analytics.events.processing.time")
                .description("事件处理时间")
                .register(meterRegistry);
        
        // 初始化Gauge指标
        meterRegistry.gauge("analytics.sessions.active", activeSessions);
        meterRegistry.gauge("analytics.events.concurrent.peak", peakConcurrentEvents);
    }
    
    /**
     * 记录事件开始处理
     */
    public void recordEventStart(String eventType, String eventName) {
        totalEventsCounter.increment();
        
        // 更新事件类型计数器
        String eventTypeKey = "analytics.events.type." + eventType;
        eventTypeCounters.computeIfAbsent(eventTypeKey, key -> 
                Counter.builder(key)
                        .description(eventType + "类型事件数量")
                        .register(meterRegistry)
        ).increment();
        
        // 更新活跃会话数（如果是会话开始事件）
        if ("session_start".equals(eventType)) {
            activeSessions.incrementAndGet();
            updatePeakConcurrent();
        }
    }
    
    /**
     * 记录事件处理完成
     */
    public void recordEventCompletion(String eventType, boolean success, long processingTimeMs) {
        if (success) {
            successfulEventsCounter.increment();
        } else {
            failedEventsCounter.increment();
        }
        
        // 记录处理时间
        eventProcessingTimer.record(processingTimeMs, TimeUnit.MILLISECONDS);
        
        // 更新会话结束事件
        if ("session_end".equals(eventType)) {
            activeSessions.decrementAndGet();
        }
    }
    
    /**
     * 记录设备类型
     */
    public void recordDeviceType(String deviceType) {
        if (deviceType != null) {
            String deviceKey = "analytics.devices.type." + deviceType;
            deviceTypeCounters.computeIfAbsent(deviceKey, key -> 
                    Counter.builder(key)
                            .description(deviceType + "设备数量")
                            .register(meterRegistry)
            ).increment();
        }
    }
    
    /**
     * 记录维度选择
     */
    public void recordDimensionSelection(String dimensionCode) {
        if (dimensionCode != null) {
            String dimensionKey = "analytics.dimensions.selection." + dimensionCode;
            dimensionSelectionCounts.computeIfAbsent(dimensionKey, key -> 
                    new AtomicLong(0)
            ).incrementAndGet();
            
            // 注册Gauge（如果尚未注册）
            String gaugeName = "analytics.dimensions." + dimensionCode + ".selections";
            meterRegistry.gauge(gaugeName, dimensionSelectionCounts.get(dimensionKey));
        }
    }
    
    /**
     * 记录错误
     */
    public void recordError(String errorType, String errorMessage) {
        Counter.builder("analytics.errors.type." + errorType)
                .description(errorType + "类型错误")
                .register(meterRegistry)
                .increment();
        
        log.error("分析服务错误: type={}, message={}", errorType, errorMessage);
    }
    
    /**
     * 记录缓存命中/未命中
     */
    public void recordCacheHit(String cacheName, boolean hit) {
        String metricName = hit ? "analytics.cache.hits" : "analytics.cache.misses";
        Counter.builder(metricName)
                .tag("cache", cacheName)
                .description(cacheName + "缓存" + (hit ? "命中" : "未命中"))
                .register(meterRegistry)
                .increment();
    }
    
    /**
     * 记录数据库操作
     */
    public void recordDatabaseOperation(String operation, long durationMs, boolean success) {
        Timer.builder("analytics.database.operations")
                .tag("operation", operation)
                .tag("success", String.valueOf(success))
                .description("数据库操作时间")
                .register(meterRegistry)
                .record(durationMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * 获取监控数据
     */
    public Map<String, Object> getMonitoringData() {
        Map<String, Object> data = new java.util.HashMap<>();
        
        // 基本指标
        data.put("totalEvents", totalEventsCounter.count());
        data.put("successfulEvents", successfulEventsCounter.count());
        data.put("failedEvents", failedEventsCounter.count());
        data.put("activeSessions", activeSessions.get());
        data.put("peakConcurrentEvents", peakConcurrentEvents.get());
        
        // 性能指标
        data.put("eventProcessingTimeAvg", eventProcessingTimer.mean(TimeUnit.MILLISECONDS));
        data.put("eventProcessingTimeMax", eventProcessingTimer.max(TimeUnit.MILLISECONDS));
        
        // 事件类型分布
        Map<String, Double> eventTypeDistribution = new java.util.HashMap<>();
        eventTypeCounters.forEach((key, counter) -> {
            String type = key.replace("analytics.events.type.", "");
            eventTypeDistribution.put(type, counter.count());
        });
        data.put("eventTypeDistribution", eventTypeDistribution);
        
        // 设备类型分布
        Map<String, Double> deviceTypeDistribution = new java.util.HashMap<>();
        deviceTypeCounters.forEach((key, counter) -> {
            String type = key.replace("analytics.devices.type.", "");
            deviceTypeDistribution.put(type, counter.count());
        });
        data.put("deviceTypeDistribution", deviceTypeDistribution);
        
        // 维度选择统计
        Map<String, Long> dimensionSelections = new java.util.HashMap<>();
        dimensionSelectionCounts.forEach((key, count) -> {
            String dimension = key.replace("analytics.dimensions.selection.", "");
            dimensionSelections.put(dimension, count.get());
        });
        data.put("dimensionSelections", dimensionSelections);
        
        // 时间戳
        data.put("timestamp", LocalDateTime.now());
        data.put("monitoringSince", getStartTime());
        
        return data;
    }
    
    /**
     * 重置监控数据（用于测试或定期重置）
     */
    public void resetMonitoringData() {
        // 注意：Micrometer的Counter不能直接重置
        // 这里只能重置自定义的AtomicLong指标
        activeSessions.set(0);
        peakConcurrentEvents.set(0);
        dimensionSelectionCounts.clear();
        
        log.info("监控数据已重置");
    }
    
    /**
     * 检查服务健康状态
     */
    public Map<String, Object> checkHealth() {
        Map<String, Object> health = new java.util.HashMap<>();
        
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        
        // 检查事件处理成功率
        double total = totalEventsCounter.count();
        double successful = successfulEventsCounter.count();
        double successRate = total > 0 ? (successful / total) * 100 : 100.0;
        
        health.put("successRate", String.format("%.2f%%", successRate));
        health.put("totalEventsProcessed", total);
        health.put("activeSessions", activeSessions.get());
        
        // 检查处理时间
        double avgProcessingTime = eventProcessingTimer.mean(TimeUnit.MILLISECONDS);
        health.put("avgProcessingTimeMs", String.format("%.2f", avgProcessingTime));
        
        // 性能警告
        if (successRate < 95.0) {
            health.put("warning", "事件处理成功率低于95%");
        }
        
        if (avgProcessingTime > 1000) {
            health.put("warning", "平均处理时间超过1秒");
        }
        
        return health;
    }
    
    /**
     * 生成监控报告
     */
    public String generateReport() {
        Map<String, Object> data = getMonitoringData();
        
        StringBuilder report = new StringBuilder();
        report.append("=== 分析服务监控报告 ===\n");
        report.append("生成时间: ").append(LocalDateTime.now()).append("\n\n");
        
        report.append("1. 事件统计\n");
        report.append("   总事件数: ").append(data.get("totalEvents")).append("\n");
        report.append("   成功事件: ").append(data.get("successfulEvents")).append("\n");
        report.append("   失败事件: ").append(data.get("failedEvents")).append("\n");
        report.append("   活跃会话: ").append(data.get("activeSessions")).append("\n\n");
        
        report.append("2. 性能指标\n");
        report.append("   平均处理时间: ").append(data.get("eventProcessingTimeAvg")).append(" ms\n");
        report.append("   最大处理时间: ").append(data.get("eventProcessingTimeMax")).append(" ms\n\n");
        
        report.append("3. 事件类型分布\n");
        Map<String, Double> eventTypes = (Map<String, Double>) data.get("eventTypeDistribution");
        if (eventTypes != null && !eventTypes.isEmpty()) {
            eventTypes.forEach((type, count) -> 
                report.append("   ").append(type).append(": ").append(count).append("\n")
            );
        }
        report.append("\n");
        
        report.append("4. 设备类型分布\n");
        Map<String, Double> deviceTypes = (Map<String, Double>) data.get("deviceTypeDistribution");
        if (deviceTypes != null && !deviceTypes.isEmpty()) {
            deviceTypes.forEach((type, count) -> 
                report.append("   ").append(type).append(": ").append(count).append("\n")
            );
        }
        
        return report.toString();
    }
    
    // 私有辅助方法
    
    private void updatePeakConcurrent() {
        long current = activeSessions.get();
        long peak = peakConcurrentEvents.get();
        
        while (current > peak) {
            if (peakConcurrentEvents.compareAndSet(peak, current)) {
                break;
            }
            peak = peakConcurrentEvents.get();
        }
    }
    
    private LocalDateTime getStartTime() {
        // 这里可以返回服务启动时间
        // 当前实现返回当前时间减去总事件数对应的估计时间
        long totalEvents = (long) totalEventsCounter.count();
        if (totalEvents == 0) {
            return LocalDateTime.now();
        }
        
        // 假设平均每秒处理10个事件
        long secondsAgo = totalEvents / 10;
        return LocalDateTime.now().minusSeconds(secondsAgo);
    }
}