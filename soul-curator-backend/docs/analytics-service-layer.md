# SoulCurator 分析服务层文档

## 概述

分析服务层（Analytics Service Layer）是SoulCurator后端系统的核心组件之一，负责处理用户行为埋点、数据分析、监控和统计功能。本服务层采用分层架构设计，包含接口层、实现层、缓存层、监控层和工具层。

## 架构设计

### 1. 分层架构

```
┌─────────────────────────────────────────────┐
│                Controller层                  │
│          AnalyticsController                │
└─────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────┐
│                 Service接口层                │
│            AnalyticsService                 │
└─────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────┐
│              Service实现层                   │
│          AnalyticsServiceImpl               │
│          CachedAnalyticsService             │
└─────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────┐
│                工具与辅助层                  │
│        EventDataProcessor                   │
│        EventProcessingListener              │
│        AnalyticsMonitorService              │
└─────────────────────────────────────────────┘
                      │
                      ▼
┌─────────────────────────────────────────────┐
│                Repository层                 │
│          UserEventRepository               │
└─────────────────────────────────────────────┘
```

### 2. 核心组件

#### 2.1 AnalyticsService (接口)
- **位置**: `com.soulcurator.backend.service.analytics.AnalyticsService`
- **职责**: 定义分析服务的所有业务方法
- **主要方法**:
  - `recordEvent()` - 记录单个事件
  - `recordEvents()` - 批量记录事件
  - `getEventsBySessionId()` - 获取会话事件
  - `countEvents()` - 统计事件数量
  - `getPopularPages()` - 获取热门页面
  - `getHealthStatus()` - 获取健康状态

#### 2.2 AnalyticsServiceImpl (实现)
- **位置**: `com.soulcurator.backend.service.analytics.AnalyticsServiceImpl`
- **职责**: 实现AnalyticsService接口的核心业务逻辑
- **特性**:
  - 完整的事务管理（@Transactional）
  - 数据验证和处理
  - 异步事件发布
  - 监控集成

#### 2.3 CachedAnalyticsService (缓存包装)
- **位置**: `com.soulcurator.backend.service.analytics.CachedAnalyticsService`
- **职责**: 为分析服务添加缓存层，提高性能
- **缓存策略**:
  - 事件统计：5分钟TTL
  - 热门页面：10分钟TTL
  - 用户会话：30分钟TTL
  - 健康状态：1分钟TTL

#### 2.4 EventDataProcessor (数据处理器)
- **位置**: `com.soulcurator.backend.service.analytics.EventDataProcessor`
- **职责**: 处理事件数据的验证、转换和丰富
- **功能**:
  - 数据验证和清洗
  - 设备信息提取
  - IP地址匿名化
  - 会话ID生成和管理
  - 重复事件检测

#### 2.5 EventProcessingListener (事件监听器)
- **位置**: `com.soulcurator.backend.service.analytics.EventProcessingListener`
- **职责**: 异步处理事件记录后的操作
- **处理流程**:
  1. 更新实时统计
  2. 检查异常模式
  3. 触发下游处理
  4. 更新缓存

#### 2.6 AnalyticsMonitorService (监控服务)
- **位置**: `com.soulcurator.backend.service.analytics.AnalyticsMonitorService`
- **职责**: 监控分析服务的性能和运行状态
- **监控指标**:
  - 事件处理数量和成功率
  - 处理时间分布
  - 设备类型分布
  - 维度选择统计
  - 缓存命中率

## 核心功能

### 1. 事件记录流程

```java
// 1. 客户端发送事件
EventRequest request = new EventRequest();
request.setEventType("selection");
request.setEventName("option_selected");
request.setEventData(Map.of("questionId", 1, "optionId", 2));
request.setTimestamp(System.currentTimeMillis());

// 2. 服务层处理
EventResponse response = analyticsService.recordEvent(request);

// 3. 处理步骤：
//    a) 数据验证（EventDataProcessor）
//    b) 重复事件检查
//    c) 用户关联查找
//    d) 事件数据丰富
//    e) 数据库保存
//    f) 异步事件发布
//    g) 监控数据记录
//    h) 响应构建
```

### 2. 数据验证规则

| 字段 | 验证规则 | 错误代码 |
|------|----------|----------|
| eventType | 非空，长度≤50，有效类型 | 400 |
| eventName | 非空，长度≤100 | 400 |
| timestamp | 非空，正数 | 400 |
| eventData | JSON大小≤10KB | 400 |
| pagePath | 长度≤500 | 400 |
| userAgent | 长度≤1000 | 400 |

### 3. 支持的事件类型

```java
// 核心事件类型
"page_view"        // 页面浏览
"click"            // 点击事件
"selection"        // 选项选择
"session_start"    // 会话开始
"session_end"      // 会话结束

// 业务事件类型
"question_view"    // 问题查看
"option_hover"     // 选项悬停
"portrait_view"    // 画像查看
"portrait_generated" // 画像生成
"recommendation_view" // 推荐查看
"feedback_submit"  // 反馈提交

// 系统事件类型
"error"            // 错误事件
"api_call"         // API调用
"custom"           // 自定义事件
```

### 4. 缓存策略

#### 4.1 缓存配置
```yaml
缓存名称:
  eventStats:      事件统计缓存 (5分钟)
  popularPages:    热门页面缓存 (10分钟)
  userSessions:    用户会话缓存 (30分钟)
  analyticsHealth: 健康状态缓存 (1分钟)
  deviceStats:     设备统计缓存 (10分钟)
  retentionRates:  留存率缓存 (1小时)
```

#### 4.2 缓存键设计
```java
// 事件统计缓存键
"stats:{startTime}:{endTime}"
"stats:recent:{timeWindow}"

// 用户会话缓存键
"session:{sessionId}"
"user:{userId}:sessions"

// 热门页面缓存键
"popular:pages:{timeRange}:{limit}"
```

### 5. 监控指标

#### 5.1 Micrometer指标
```java
// 计数器
analytics.events.total          // 总事件数
analytics.events.successful     // 成功事件数
analytics.events.failed         // 失败事件数
analytics.events.type.{type}    // 事件类型分布

// 计时器
analytics.events.processing.time // 事件处理时间

// 仪表
analytics.sessions.active       // 活跃会话数
analytics.events.concurrent.peak // 峰值并发事件
```

#### 5.2 健康检查
```json
{
  "status": "UP",
  "successRate": "98.5%",
  "totalEventsProcessed": 1250,
  "activeSessions": 42,
  "avgProcessingTimeMs": "45.2",
  "timestamp": "2026-04-14T15:30:00"
}
```

## 配置说明

### 1. 应用配置
```properties
# 分析服务配置
analytics.event.validation.enabled=true
analytics.event.duplicate-check.enabled=true
analytics.event.duplicate-window-ms=5000
analytics.event.max-data-size-kb=10

# 缓存配置
analytics.cache.enabled=true
analytics.cache.ttl.event-stats=300000    # 5分钟
analytics.cache.ttl.popular-pages=600000  # 10分钟
analytics.cache.ttl.user-sessions=1800000 # 30分钟

# 监控配置
analytics.monitoring.enabled=true
analytics.monitoring.export-interval=60000 # 1分钟
```

### 2. 异步配置
```java
@Configuration
@EnableAsync
public class AsyncConfig {
    // 事件处理线程池
    @Bean
    public TaskExecutor analyticsTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("analytics-async-");
        return executor;
    }
}
```

## 测试策略

### 1. 单元测试
```java
@Test
void testRecordEvent_Success() {
    // 模拟依赖
    when(userEventRepository.save(any())).thenReturn(mockEvent);
    
    // 执行测试
    EventResponse response = analyticsService.recordEvent(validRequest);
    
    // 验证结果
    assertTrue(response.isSuccess());
    assertEquals(100L, response.getEventId());
}
```

### 2. 集成测试
```java
@SpringBootTest
@Transactional
class AnalyticsIntegrationTest {
    
    @Test
    void testFullEventRecordingFlow() {
        // 准备测试数据
        EventRequest request = createTestRequest();
        
        // 执行完整流程
        EventResponse response = analyticsService.recordEvent(request);
        
        // 验证数据库
        UserEvent event = userEventRepository.findById(response.getEventId());
        assertNotNull(event);
        
        // 验证缓存
        // 验证监控数据
    }
}
```

### 3. 性能测试
```java
@Test
void testPerformance_Record1000Events() {
    // 准备1000个测试事件
    List<EventRequest> requests = createBatchRequests(1000);
    
    // 执行批量记录
    long startTime = System.currentTimeMillis();
    List<EventResponse> responses = analyticsService.recordEvents(requests);
    long duration = System.currentTimeMillis() - startTime;
    
    // 验证性能
    assertTrue(duration < 5000); // 5秒内完成
    assertEquals(1000, responses.size());
}
```

## 部署和运维

### 1. 健康检查端点
```
GET /api/v1/analytics/health
GET /api/v1/analytics/info
GET /api/v1/analytics/monitoring
GET /api/v1/analytics/stats
```

### 2. 监控告警规则
```yaml
告警规则:
  - 事件处理成功率 < 95% (持续5分钟)
  - 平均处理时间 > 1000ms (持续10分钟)
  - 活跃会话数 > 1000 (峰值)
  - 数据库连接失败 (立即)
```

### 3. 日志配置
```properties
# 分析服务日志
logging.level.com.soulcurator.backend.service.analytics=INFO
logging.level.com.soulcurator.backend.service.analytics.AnalyticsServiceImpl=DEBUG
logging.level.com.soulcurator.backend.service.analytics.EventDataProcessor=DEBUG

# 监控日志
logging.level.com.soulcurator.backend.service.analytics.AnalyticsMonitorService=INFO
```

## 扩展和定制

### 1. 添加新事件类型
```java
// 1. 在EventDataProcessor中添加验证
private boolean isValidEventType(String eventType) {
    // 添加新的事件类型
    String[] validTypes = {
        // ... 现有类型
        "new_event_type"  // 新增类型
    };
    // ... 验证逻辑
}

// 2. 在EventRequest中添加显示名称
public String getEventTypeDisplay() {
    return switch (eventType) {
        // ... 现有case
        case "new_event_type" -> "新事件类型";
        default -> eventType;
    };
}
```

### 2. 自定义数据处理
```java
@Component
public class CustomEventProcessor {
    
    @EventListener
    public void processCustomEvent(EventProcessingListener.EventRecordedEvent event) {
        UserEvent userEvent = event.getUserEvent();
        
        if ("custom_event".equals(userEvent.getEventType())) {
            // 自定义处理逻辑
            processCustomLogic(userEvent);
        }
    }
}
```

### 3. 集成外部系统
```java
@Service
public class ExternalIntegrationService {
    
    @Async
    @EventListener
    public void sendToExternalSystem(EventProcessingListener.EventRecordedEvent event) {
        // 发送到外部分析系统（如Google Analytics, Mixpanel等）
        sendToGoogleAnalytics(event.getUserEvent());
        sendToMixpanel(event.getUserEvent());
    }
}
```

## 故障排除

### 常见问题及解决方案

#### 1. 事件记录失败
**症状**: 返回400或500错误
**可能原因**:
- 数据验证失败
- 数据库连接问题
- 重复事件检测
**解决方案**:
- 检查请求数据格式
- 查看服务日志
- 检查数据库状态

#### 2. 性能下降
**症状**: 处理时间变长
**可能原因**:
- 缓存失效
- 数据库压力大
- 线程池饱和
**解决方案**:
- 检查缓存命中率
- 优化数据库查询
- 调整线程池配置

#### 3. 监控数据异常
**症状**: 监控指标显示异常值
**可能原因**:
- 监控服务故障
- 数据统计错误
- 系统时间不同步
**解决方案**:
- 重启监控服务
- 检查数据统计逻辑
- 同步系统时间

## 版本历史

### v1.0 (2026-04-14)
- 初始版本发布
- 基础事件记录功能
- 数据验证和处理
- 缓存和监控支持
- 完整测试覆盖

### v1.1 (计划中)
- 实时分析功能
- 高级报表生成
- 机器学习集成
- 多租户支持

---

**文档维护**: 后端技术架构师节点
**最后更新**: 2026-04-14
**版本**: 1.0