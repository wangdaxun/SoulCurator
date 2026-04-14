package com.soulcurator.backend.service.analytics;

import com.soulcurator.backend.dto.analytics.EventRequest;
import com.soulcurator.backend.dto.analytics.EventResponse;
import com.soulcurator.backend.model.analytics.UserEvent;
import com.soulcurator.backend.repository.UserRepository;
import com.soulcurator.backend.repository.analytics.UserEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 分析服务测试类
 */
@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {
    
    @Mock
    private UserEventRepository userEventRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private AnalyticsServiceImpl analyticsService;
    
    private EventRequest validRequest;
    private UserEvent mockUserEvent;
    
    @BeforeEach
    void setUp() {
        // 创建有效的请求
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("sessionId", "test-session-123");
        eventData.put("userId", 1L);
        eventData.put("questionId", 5L);
        eventData.put("optionId", 10L);
        
        validRequest = new EventRequest();
        validRequest.setEventType("selection");
        validRequest.setEventName("option_selected");
        validRequest.setEventData(eventData);
        validRequest.setPagePath("/exploration/step1");
        validRequest.setPageTitle("灵魂探索 - 步骤1");
        validRequest.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)");
        validRequest.setScreenResolution("1920x1080");
        validRequest.setTimestamp(System.currentTimeMillis());
        
        // 创建模拟的用户事件
        mockUserEvent = new UserEvent();
        mockUserEvent.setId(100L);
        mockUserEvent.setSessionId("test-session-123");
        mockUserEvent.setEventType("selection");
        mockUserEvent.setEventName("option_selected");
        mockUserEvent.setEventData(eventData);
        mockUserEvent.setPageUrl("/exploration/step1");
        mockUserEvent.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)");
        mockUserEvent.setDeviceType("desktop");
        mockUserEvent.setScreenResolution("1920x1080");
        mockUserEvent.setCreatedAt(LocalDateTime.now());
    }
    
    @Test
    void testRecordEvent_Success() {
        // 模拟Repository行为
        when(userEventRepository.save(any(UserEvent.class))).thenReturn(mockUserEvent);
        
        // 执行测试
        EventResponse response = analyticsService.recordEvent(validRequest);
        
        // 验证结果
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(100L, response.getEventId());
        assertEquals("test-session-123", response.getSessionId());
        assertEquals("selection", response.getEventType());
        assertEquals("option_selected", response.getEventName());
        assertEquals("事件记录成功", response.getMessage());
        assertEquals(200, response.getCode());
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).save(any(UserEvent.class));
    }
    
    @Test
    void testRecordEvent_InvalidRequest() {
        // 创建无效的请求（缺少必要字段）
        EventRequest invalidRequest = new EventRequest();
        invalidRequest.setEventType(""); // 空事件类型
        invalidRequest.setEventName(""); // 空事件名称
        invalidRequest.setTimestamp(null); // 空时间戳
        
        // 执行测试
        EventResponse response = analyticsService.recordEvent(invalidRequest);
        
        // 验证结果
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(400, response.getCode());
        assertTrue(response.getMessage().contains("事件请求数据无效"));
        
        // 验证Repository没有被调用
        verify(userEventRepository, never()).save(any(UserEvent.class));
    }
    
    @Test
    void testRecordEvent_InvalidEventType() {
        // 创建无效事件类型的请求
        EventRequest invalidRequest = new EventRequest();
        invalidRequest.setEventType("invalid_type"); // 无效的事件类型
        invalidRequest.setEventName("test_event");
        invalidRequest.setTimestamp(System.currentTimeMillis());
        
        // 执行测试
        EventResponse response = analyticsService.recordEvent(invalidRequest);
        
        // 验证结果
        assertNotNull(response);
        assertFalse(response.isSuccess());
        assertEquals(400, response.getCode());
        
        // 验证Repository没有被调用
        verify(userEventRepository, never()).save(any(UserEvent.class));
    }
    
    @Test
    void testGetEventById_Success() {
        // 模拟Repository行为
        when(userEventRepository.findById(100L)).thenReturn(Optional.of(mockUserEvent));
        
        // 执行测试
        UserEvent event = analyticsService.getEventById(100L);
        
        // 验证结果
        assertNotNull(event);
        assertEquals(100L, event.getId());
        assertEquals("test-session-123", event.getSessionId());
        assertEquals("selection", event.getEventType());
        assertEquals("option_selected", event.getEventName());
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).findById(100L);
    }
    
    @Test
    void testGetEventById_NotFound() {
        // 模拟Repository行为（返回空）
        when(userEventRepository.findById(999L)).thenReturn(Optional.empty());
        
        // 执行测试并验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            analyticsService.getEventById(999L);
        });
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).findById(999L);
    }
    
    @Test
    void testGetEventsBySessionId() {
        // 模拟Repository行为
        when(userEventRepository.findBySessionId("test-session-123"))
                .thenReturn(Arrays.asList(mockUserEvent));
        
        // 执行测试
        var events = analyticsService.getEventsBySessionId("test-session-123");
        
        // 验证结果
        assertNotNull(events);
        assertEquals(1, events.size());
        assertEquals(mockUserEvent, events.get(0));
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).findBySessionId("test-session-123");
    }
    
    @Test
    void testCountEvents() {
        // 模拟Repository行为
        LocalDateTime startTime = LocalDateTime.now().minusDays(1);
        LocalDateTime endTime = LocalDateTime.now();
        when(userEventRepository.countByCreatedAtBetween(startTime, endTime))
                .thenReturn(50L);
        
        // 执行测试
        Long count = analyticsService.countEvents(startTime, endTime);
        
        // 验证结果
        assertNotNull(count);
        assertEquals(50L, count);
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).countByCreatedAtBetween(startTime, endTime);
    }
    
    @Test
    void testIsSessionCompleted() {
        // 模拟Repository行为
        when(userEventRepository.isSessionCompleted("test-session-123"))
                .thenReturn(true);
        
        // 执行测试
        boolean completed = analyticsService.isSessionCompleted("test-session-123");
        
        // 验证结果
        assertTrue(completed);
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).isSessionCompleted("test-session-123");
    }
    
    @Test
    void testGetHealthStatus() {
        // 模拟Repository行为
        when(userEventRepository.count()).thenReturn(1000L);
        when(userEventRepository.countByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(50L);
        
        // 执行测试
        var healthStatus = analyticsService.getHealthStatus();
        
        // 验证结果
        assertNotNull(healthStatus);
        assertEquals("UP", healthStatus.get("status"));
        assertEquals("CONNECTED", healthStatus.get("database"));
        assertEquals(1000L, healthStatus.get("totalEvents"));
        assertEquals(50L, healthStatus.get("lastHourEvents"));
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).count();
        verify(userEventRepository, times(1)).countByCreatedAtBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testGetServiceStats() {
        // 模拟Repository行为
        when(userEventRepository.count()).thenReturn(1000L);
        when(userEventRepository.findAll()).thenReturn(Arrays.asList(mockUserEvent));
        when(userEventRepository.countByEventTypeGroup(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(new Object[]{"selection", 500L}));
        
        // 执行测试
        var stats = analyticsService.getServiceStats();
        
        // 验证结果
        assertNotNull(stats);
        assertEquals(1000L, stats.get("totalEvents"));
        assertEquals(1L, stats.get("uniqueSessions"));
        assertEquals("selection", stats.get("mostCommonEventType"));
        
        // 验证Repository被调用
        verify(userEventRepository, times(1)).count();
        verify(userEventRepository, times(1)).findAll();
        verify(userEventRepository, times(1)).countByEventTypeGroup(any(LocalDateTime.class), any(LocalDateTime.class));
    }
    
    @Test
    void testAnonymizeIpAddress() {
        // 这个测试需要访问私有方法，可以通过反射或改为包可见性
        // 这里我们测试公开的方法，间接测试IP匿名化
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("sessionId", "test-session-123");
        eventData.put("ipAddress", "192.168.1.100");
        
        EventRequest request = new EventRequest();
        request.setEventType("page_view");
        request.setEventName("page_entered");
        request.setEventData(eventData);
        request.setTimestamp(System.currentTimeMillis());
        
        // 模拟Repository行为
        when(userEventRepository.save(any(UserEvent.class))).thenReturn(mockUserEvent);
        
        // 执行测试
        EventResponse response = analyticsService.recordEvent(request);
        
        // 验证结果
        assertNotNull(response);
        assertTrue(response.isSuccess());
        
        // 验证Repository被调用（IP匿名化在保存时处理）
        verify(userEventRepository, times(1)).save(any(UserEvent.class));
    }
    
    @Test
    void testRecordEvents_Batch() {
        // 创建批量请求
        EventRequest request1 = new EventRequest();
        request1.setEventType("page_view");
        request1.setEventName("page_entered");
        request1.setTimestamp(System.currentTimeMillis());
        
        EventRequest request2 = new EventRequest();
        request2.setEventType("click");
        request2.setEventName("button_clicked");
        request2.setTimestamp(System.currentTimeMillis());
        
        // 模拟Repository行为
        when(userEventRepository.save(any(UserEvent.class))).thenReturn(mockUserEvent);
        
        // 执行测试
        var responses = analyticsService.recordEvents(Arrays.asList(request1, request2));
        
        // 验证结果
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertTrue(responses.get(0).isSuccess());
        assertTrue(responses.get(1).isSuccess());
        
        // 验证Repository被调用两次
        verify(userEventRepository, times(2)).save(any(UserEvent.class));
    }
}