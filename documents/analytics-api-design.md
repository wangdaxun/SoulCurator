# SoulCurator 埋点与数据分析 API 设计

## 概述
本文档定义SoulCurator项目的用户行为埋点和数据分析后端接口设计。基于Spring Boot框架，提供RESTful API。

## 技术栈
- **框架**: Spring Boot 3.2.5
- **数据库**: PostgreSQL
- **ORM**: Spring Data JPA + Hibernate
- **验证**: Spring Validation
- **文档**: SpringDoc OpenAPI 3.0

## 核心实体类设计

### 1. 用户事件实体 (UserEvent)
```java
package com.soulcurator.backend.model.analytics;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "user_events")
@Data
public class UserEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    
    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType; // page_view, selection, portrait_generated
    
    @Column(name = "event_name", nullable = false, length = 100)
    private String eventName;
    
    @Column(name = "page_url", length = 500)
    private String pageUrl;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_data", columnDefinition = "jsonb")
    private Map<String, Object> eventData;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @Column(name = "ip_address")
    private String ipAddress;
    
    @Column(name = "screen_resolution", length = 50)
    private String screenResolution;
    
    @Column(name = "device_type", length = 50)
    private String deviceType; // desktop, mobile, tablet
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

### 2. 事件数据DTO
```java
package com.soulcurator.backend.dto.analytics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class EventRequest {
    @NotBlank(message = "sessionId不能为空")
    private String sessionId;
    
    @NotBlank(message = "eventType不能为空")
    private String eventType; // page_view, selection, portrait_generated
    
    @NotBlank(message = "eventName不能为空")
    private String eventName;
    
    private String pageUrl;
    
    private Map<String, Object> eventData;
    
    private String userAgent;
    
    private String ipAddress;
    
    private String screenResolution;
    
    private String deviceType; // desktop, mobile, tablet
}
```

### 3. 事件响应DTO
```java
package com.soulcurator.backend.dto.analytics;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventResponse {
    private Long eventId;
    private String sessionId;
    private String eventType;
    private String eventName;
    private LocalDateTime createdAt;
    private boolean success;
    private String message;
}
```

## API接口设计

### 基础URL
```
/api/v1/analytics
```

### 1. 记录用户事件
**POST** `/events`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
  "sessionId": "test_session_001",
  "eventType": "page_view",
  "eventName": "welcome_page_entered",
  "pageUrl": "/welcome",
  "eventData": {
    "referrer": "direct",
    "entryTime": "2026-04-13T14:55:00Z"
  },
  "userAgent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
  "ipAddress": "192.168.1.100",
  "screenResolution": "1920x1080",
  "deviceType": "desktop"
}
```

**响应** (201 Created):
```json
{
  "eventId": 12345,
  "sessionId": "test_session_001",
  "eventType": "page_view",
  "eventName": "welcome_page_entered",
  "createdAt": "2026-04-13T14:55:00Z",
  "success": true,
  "message": "事件记录成功"
}
```

### 2. 批量记录事件
**POST** `/events/batch`

**请求体**:
```json
{
  "events": [
    {
      "sessionId": "test_session_001",
      "eventType": "page_view",
      "eventName": "welcome_page_entered",
      "pageUrl": "/welcome"
    },
    {
      "sessionId": "test_session_001",
      "eventType": "selection",
      "eventName": "step1_selected",
      "eventData": {
        "step": 1,
        "optionId": "deep-reflection",
        "selectionTime": 12
      }
    }
  ]
}
```

**响应**:
```json
{
  "total": 2,
  "successful": 2,
  "failed": 0,
  "results": [
    {
      "eventId": 12345,
      "sessionId": "test_session_001",
      "success": true,
      "message": "事件记录成功"
    },
    {
      "eventId": 12346,
      "sessionId": "test_session_001",
      "success": true,
      "message": "事件记录成功"
    }
  ]
}
```

### 3. 查询事件统计
**GET** `/events/stats`

**查询参数**:
- `sessionId` (可选): 按会话ID筛选
- `eventType` (可选): 按事件类型筛选
- `startDate` (可选): 开始日期，格式: yyyy-MM-dd
- `endDate` (可选): 结束日期，格式: yyyy-MM-dd
- `groupBy` (可选): 分组方式，可选值: hour, day, week, month

**响应**:
```json
{
  "totalEvents": 150,
  "eventTypeDistribution": {
    "page_view": 50,
    "selection": 80,
    "portrait_generated": 20
  },
  "dailyTrend": [
    {
      "date": "2026-04-13",
      "count": 30
    },
    {
      "date": "2026-04-12",
      "count": 45
    }
  ],
  "deviceDistribution": {
    "desktop": 70,
    "mobile": 65,
    "tablet": 15
  }
}
```

### 4. 查询会话分析
**GET** `/sessions/{sessionId}`

**响应**:
```json
{
  "sessionId": "test_session_001",
  "userId": 1,
  "totalEvents": 3,
  "totalQuestions": 5,
  "totalTimeSeconds": 65,
  "isCompleted": true,
  "portraitId": 1,
  "startedAt": "2026-04-11T10:25:00Z",
  "endedAt": "2026-04-11T10:27:00Z",
  "lastActiveAt": "2026-04-11T10:27:00Z",
  "events": [
    {
      "eventType": "page_view",
      "eventName": "welcome_page_entered",
      "createdAt": "2026-04-11T10:25:00Z"
    },
    {
      "eventType": "selection",
      "eventName": "step1_selected",
      "createdAt": "2026-04-11T10:26:00Z"
    },
    {
      "eventType": "portrait_generated",
      "eventName": "soul_portrait_created",
      "createdAt": "2026-04-11T10:27:00Z"
    }
  ]
}
```

### 5. 获取转化率分析
**GET** `/analytics/conversion`

**查询参数**:
- `startDate` (必需): 开始日期
- `endDate` (必需): 结束日期

**响应**:
```json
{
  "period": {
    "startDate": "2026-04-01",
    "endDate": "2026-04-13"
  },
  "totalSessions": 1000,
  "completedSessions": 700,
  "conversionRate": 0.7,
  "averageTimeToComplete": 58,
  "dropOffPoints": [
    {
      "step": 1,
      "dropOffCount": 50,
      "dropOffRate": 0.05
    },
    {
      "step": 2,
      "dropOffCount": 100,
      "dropOffRate": 0.1
    },
    {
      "step": 3,
      "dropOffCount": 150,
      "dropOffRate": 0.15
    }
  ],
  "popularOptions": [
    {
      "optionId": "deep-reflection",
      "optionTitle": "深刻的人生反思",
      "selectionCount": 300,
      "selectionRate": 0.3
    },
    {
      "optionId": "heart-healing",
      "optionTitle": "温暖的心灵治愈",
      "selectionCount": 250,
      "selectionRate": 0.25
    }
  ]
}
```

## 控制器实现

### AnalyticsController
```java
package com.soulcurator.backend.controller.analytics;

import com.soulcurator.backend.dto.analytics.*;
import com.soulcurator.backend.service.analytics.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @PostMapping("/events")
    public ResponseEntity<EventResponse> recordEvent(
            @Valid @RequestBody EventRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {
        
        // 从请求头获取用户代理和IP（如果请求体中没有）
        if (request.getUserAgent() == null && userAgent != null) {
            request.setUserAgent(userAgent);
        }
        if (request.getIpAddress() == null && ipAddress != null) {
            request.setIpAddress(ipAddress);
        }
        
        EventResponse response = analyticsService.recordEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/events/batch")
    public ResponseEntity<BatchEventResponse> recordEventsBatch(
            @Valid @RequestBody BatchEventRequest request) {
        
        BatchEventResponse response = analyticsService.recordEventsBatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/events/stats")
    public ResponseEntity<EventStatsResponse> getEventStats(
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "day") String groupBy) {
        
        EventStatsResponse response = analyticsService.getEventStats(
            sessionId, eventType, startDate, endDate, groupBy);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionDetailResponse> getSessionDetail(
            @PathVariable String sessionId) {
        
        SessionDetailResponse response = analyticsService.getSessionDetail(sessionId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/analytics/conversion")
    public ResponseEntity<ConversionAnalysisResponse> getConversionAnalysis(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ConversionAnalysisResponse response = analyticsService.getConversionAnalysis(startDate, endDate);
        return ResponseEntity.ok(response);
    }
}
```

## 服务层实现

### AnalyticsService
```java
package com.soulcurator.backend.service.analytics;

import com.soulcurator.backend.dto.analytics.*;
import com.soulcurator.backend.model.User;
import com.soulcurator.backend.model.analytics.UserEvent;
import com.soulcurator.backend.repository.UserRepository;
import com.soulcurator.backend.repository.analytics.UserEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    
    private final UserEventRepository userEventRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public EventResponse recordEvent(EventRequest request) {
        try {
            // 查找用户（如果sessionId对应已注册用户）
            User user = userRepository.findBySessionId(request.getSessionId())
                    .orElse(null);
            
            // 匿名化处理IP地址
            String anonymizedIp = anonymizeIpAddress(request.getIpAddress());
            
            UserEvent event = new UserEvent();
            event.setUser(user);
            event.setSessionId(request.getSessionId());
            event.setEventType(request.getEventType());
            event.setEventName(request.getEventName());
            event.setPageUrl(request.getPageUrl());
            event.setEventData(request.getEventData());
            event.setUserAgent(request.getUserAgent());
            event.setIpAddress(anonymizedIp);
            event.setScreenResolution(request.getScreenResolution());
            event.setDeviceType(request.getDeviceType());
            event.setCreatedAt(LocalDateTime.now());
            
            UserEvent savedEvent = userEventRepository.save(event);
            log.info("记录用户事件成功: sessionId={}, eventType={}, eventName={}", 
                    request.getSessionId(), request.getEventType(), request.getEventName());
            
            return EventResponse.builder()
                    .eventId(savedEvent.getId())
                    .sessionId(savedEvent.getSessionId())
                    .eventType(savedEvent.getEventType())
                    .eventName(savedEvent.getEventName())
                    .createdAt(savedEvent.getCreatedAt())
                    .success(true)
                    .message("事件记录成功")
                    .build();
                    
        } catch (Exception e) {
            log.error("记录用户事件失败: sessionId={}, error={}", 
                    request.getSessionId(), e.getMessage(), e);
            
            return EventResponse.builder()
                    .eventId(null)
                    .sessionId(request.getSessionId())
                    .eventType(request.getEventType())
                    .eventName(request.getEventName())
                    .createdAt(LocalDateTime.now())
                    .success(false)
                    .message("事件记录失败: " + e.getMessage())
                    .build();
        }
    }
    
    private String anonymizeIpAddress(String ipAddress) {
        if (ipAddress == null || ipAddress.isEmpty()) {
            return null;
        }
        
        try {
            // 简单匿名化：保留前3段，最后一段设为0
            String[] parts = ipAddress.split("\\.");
            if (parts.length == 4) {
                return parts[0] + "." + parts[1] + "." + parts[2] + ".0";
            }
            return ipAddress;
        } catch (Exception e) {
            return ipAddress;
        }
    }
    
    @Transactional
    public BatchEventResponse recordEventsBatch(BatchEventRequest request) {
        List<EventResponse> results = request.getEvents().stream()
                .map(this::recordEvent)
                .collect(Collectors.toList());
        
        long successful = results.stream().filter(EventResponse::isSuccess).count();
        long failed = results.size() - successful;
        
        return BatchEventResponse.builder()
                .total(request.getEvents().size())
                .successful(successful)
                .failed(failed)
                .results(results)
                .build();
    }
    
    public EventStatsResponse getEventStats(String sessionId, String eventType, 
                                           LocalDate startDate, LocalDate endDate, String groupBy) {
        // 实现统计逻辑
        // 这里简化实现，实际需要复杂的查询
        
        return EventStatsResponse.builder()
                .totalEvents(150L)
                .eventTypeDistribution(Map.of(
                    "page_view", 50L,
                    "selection", 80L,
                    "portrait_generated", 20L
                ))
                .deviceDistribution(Map.of(
                    "desktop", 70L,
                    "mobile", 65L,
                    "tablet", 15L
                ))
                .build();
    }
    
    public SessionDetailResponse getSessionDetail(String sessionId) {
        // 实现会话详情查询
        return SessionDetailResponse.builder()
                .sessionId(sessionId)
                .totalEvents(3)
                .totalQuestions(5)
                .totalTimeSeconds(65)
                .isCompleted(true)
                .build();
    }
    
    public ConversionAnalysisResponse getConversionAnalysis(LocalDate startDate, LocalDate endDate) {
        // 实现转化率分析
        return ConversionAnalysisResponse.builder()
                .period(new Period(startDate, endDate))
                .totalSessions(1000)
                .completedSessions(700)
                .conversionRate(0.7)
                .averageTimeToComplete(58)
                .build();
    }
}

## 前端集成示例

### 1. Vue3组件中集成埋点
```javascript
// src/utils/analytics.js
export class AnalyticsTracker {
    constructor() {
        this.baseUrl = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080';
        this.sessionId = this.getOrCreateSessionId();
    }
    
    getOrCreateSessionId() {
        let sessionId = localStorage.getItem('soulcurator_session_id');
        if (!sessionId) {
            sessionId = 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9);
            localStorage.setItem('soulcurator_session_id', sessionId);
        }
        return sessionId;
    }
    
    async trackEvent(eventType, eventName, eventData = {}) {
        const payload = {
            sessionId: this.sessionId,
            eventType,
            eventName,
            eventData,
            pageUrl: window.location.pathname,
            screenResolution: `${window.screen.width}x${window.screen.height}`,
            deviceType: this.getDeviceType()
        };
        
        try {
            const response = await fetch(`${this.baseUrl}/api/v1/analytics/events`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload)
            });
            
            if (!response.ok) {
                console.warn('埋点记录失败:', await response.text());
            }
        } catch (error) {
            console.warn('埋点记录异常:', error);
            // 可以降级到localStorage存储，下次重试
            this.queueFailedEvent(payload);
        }
    }
    
    getDeviceType() {
        const width = window.screen.width;
        if (width < 768) return 'mobile';
        if (width < 1024) return 'tablet';
        return 'desktop';
    }
    
    queueFailedEvent(payload) {
        const failedEvents = JSON.parse(localStorage.getItem('failed_analytics_events') || '[]');
        failedEvents.push({
            ...payload,
            timestamp: Date.now()
        });
        localStorage.setItem('failed_analytics_events', JSON.stringify(failedEvents.slice(-50))); // 只保留最近50个
    }
    
    // 常用事件快捷方法
    trackPageView(pageName) {
        return this.trackEvent('page_view', `${pageName}_entered`);
    }
    
    trackSelection(step, optionId, selectionTime) {
        return this.trackEvent('selection', `step${step}_selected`, {
            step,
            optionId,
            selectionTime
        });
    }
    
    trackPortraitGenerated(soulType, completionTime) {
        return this.trackEvent('portrait_generated', 'soul_portrait_created', {
            soulType,
            completionTime
        });
    }
}

// 创建全局实例
export const analytics = new AnalyticsTracker();
```

### 2. 在Vue组件中使用
```vue
<template>
  <div @click="handleOptionClick(option)">
    {{ option.title }}
  </div>
</template>

<script setup>
import { analytics } from '@/utils/analytics';
import { ref } from 'vue';

const props = defineProps({
  option: Object,
  step: Number
});

const selectionStartTime = ref(null);

const handleOptionClick = async (option) => {
  // 记录选择开始时间
  selectionStartTime.value = Date.now();
  
  // 记录选择事件
  const selectionTime = Date.now() - selectionStartTime.value;
  await analytics.trackSelection(props.step, option.id, selectionTime);
  
  // 其他业务逻辑...
};

// 页面进入时记录
onMounted(() => {
  analytics.trackPageView('selector_page');
});
</script>
```

### 3. 路由守卫中集成
```javascript
// src/router/index.js
import { analytics } from '@/utils/analytics';

const router = createRouter({
  // ...路由配置
});

router.beforeEach((to, from, next) => {
  // 记录页面离开事件
  if (from.name) {
    analytics.trackEvent('page_view', `${from.name}_left`);
  }
  
  // 记录页面进入事件
  analytics.trackEvent('page_view', `${to.name}_entered`, {
    from: from.name,
    to: to.name
  });
  
  next();
});
```

## 部署和配置

### 1. 环境变量配置
```properties
# application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/soulcurator_db
spring.datasource.username=postgres
spring.datasource.password=your_password

# 分析配置
analytics.ip-anonymization.enabled=true
analytics.data-retention-days.events=365
analytics.data-retention-days.sessions=180

# CORS配置（允许前端访问）
spring.web.cors.allowed-origins=http://localhost:5173,https://soulcurator.app
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

### 2. 数据库索引优化
```sql
-- 为分析查询创建额外索引
CREATE INDEX idx_events_session_type ON user_events(session_id, event_type);
CREATE INDEX idx_events_created_type ON user_events(created_at, event_type);
CREATE INDEX idx_sessions_started_completed ON user_sessions(started_at, is_completed);

-- 分区表（如果数据量很大）
-- 按月份对user_events表进行分区
```

### 3. 监控和告警
- 监控API响应时间
- 监控错误率
- 设置事件丢失告警
- 定期清理旧数据

## 测试策略

### 1. 单元测试
```java
@SpringBootTest
class AnalyticsServiceTest {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Test
    void testRecordEvent() {
        EventRequest request = new EventRequest();
        request.setSessionId("test_session");
        request.setEventType("page_view");
        request.setEventName("welcome_page_entered");
        
        EventResponse response = analyticsService.recordEvent(request);
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getEventId());
    }
}
```

### 2. 集成测试
```javascript
// 前端集成测试
describe('AnalyticsTracker', () => {
    it('should track page view event', async () => {
        const tracker = new AnalyticsTracker();
        const mockFetch = jest.fn().mockResolvedValue({ ok: true });
        global.fetch = mockFetch;
        
        await tracker.trackPageView('welcome');
        
        expect(mockFetch).toHaveBeenCalledWith(
            expect.stringContaining('/api/v1/analytics/events'),
            expect.objectContaining({
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
        );
    });
});
```

## 安全考虑

1. **IP匿名化**: 所有IP地址在存储前进行匿名化处理
2. **数据加密**: 敏感信息加密存储
3. **访问控制**: API需要适当的认证和授权
4. **数据保留**: 设置合理的数据保留期限
5. **GDPR合规**: 提供数据删除接口

## 性能优化

1. **批量写入**: 支持事件批量记录，减少HTTP请求
2. **异步处理**: 非关键事件可以异步记录
3. **连接池**: 数据库连接池优化
4. **缓存**: 频繁查询的数据可以缓存
5. **索引优化**: 根据查询模式优化索引

---

**文档版本**: v1.0  
**最后更新**: 2026-04-13  
**维护者**: 王达迅  
**下次评审**: 2026-05-13
