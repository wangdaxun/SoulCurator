package com.soulcurator.backend.service.analytics;

import com.soulcurator.backend.dto.analytics.EventRequest;
import com.soulcurator.backend.dto.analytics.EventResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 服务层冒烟测试
 * 验证整个服务层的基本功能是否正常
 */
@SpringBootTest
@ActiveProfiles("test")
class ServiceLayerSmokeTest {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Test
    void testServiceLayerInitialization() {
        // 验证服务层组件已正确初始化
        assertNotNull(analyticsService, "AnalyticsService应该被注入");
        
        // 验证服务类型（应该是CachedAnalyticsService）
        assertTrue(analyticsService instanceof CachedAnalyticsService, 
                "AnalyticsService应该是CachedAnalyticsService实例");
    }
    
    @Test
    void testBasicEventRecording() {
        // 准备测试数据
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("sessionId", "smoke-test-session-" + System.currentTimeMillis());
        eventData.put("testRun", true);
        eventData.put("smokeTest", "basic");
        
        EventRequest request = new EventRequest();
        request.setEventType("page_view");
        request.setEventName("smoke_test_page");
        request.setEventData(eventData);
        request.setPagePath("/smoke-test");
        request.setPageTitle("冒烟测试页面");
        request.setUserAgent("SmokeTest/1.0");
        request.setTimestamp(System.currentTimeMillis());
        
        // 执行测试
        EventResponse response = analyticsService.recordEvent(request);
        
        // 验证结果
        assertNotNull(response, "响应不应为空");
        assertTrue(response.isSuccess(), "事件记录应该成功");
        assertNotNull(response.getEventId(), "事件ID不应为空");
        assertEquals("page_view", response.getEventType(), "事件类型应该匹配");
        assertEquals("smoke_test_page", response.getEventName(), "事件名称应该匹配");
        assertEquals(200, response.getCode(), "响应代码应该是200");
        
        System.out.println("冒烟测试通过: " + response.getResponseSummary());
    }
    
    @Test
    void testMultipleEventTypes() {
        // 测试多种事件类型
        String sessionId = "multi-type-smoke-" + System.currentTimeMillis();
        
        // 测试页面浏览事件
        testEventType(sessionId, "page_view", "welcome_page");
        
        // 测试点击事件
        testEventType(sessionId, "click", "button_click");
        
        // 测试选择事件
        testEventType(sessionId, "selection", "option_selected");
        
        // 测试自定义事件
        testEventType(sessionId, "custom", "user_action");
        
        System.out.println("多事件类型测试通过");
    }
    
    @Test
    void testServiceHealthCheck() {
        // 测试健康检查
        var healthStatus = analyticsService.getHealthStatus();
        
        assertNotNull(healthStatus, "健康状态不应为空");
        assertTrue(healthStatus.containsKey("status"), "应该包含status字段");
        assertTrue(healthStatus.containsKey("database"), "应该包含database字段");
        
        Object status = healthStatus.get("status");
        assertTrue(status instanceof String, "status应该是字符串");
        assertEquals("UP", status, "服务状态应该是UP");
        
        System.out.println("健康检查测试通过: status=" + status);
    }
    
    @Test
    void testServiceStats() {
        // 测试服务统计
        var serviceStats = analyticsService.getServiceStats();
        
        assertNotNull(serviceStats, "服务统计不应为空");
        assertTrue(serviceStats.containsKey("totalEvents"), "应该包含totalEvents字段");
        assertTrue(serviceStats.containsKey("uniqueSessions"), "应该包含uniqueSessions字段");
        
        System.out.println("服务统计测试通过");
        System.out.println("总事件数: " + serviceStats.get("totalEvents"));
        System.out.println("唯一会话数: " + serviceStats.get("uniqueSessions"));
    }
    
    @Test
    void testErrorHandling() {
        // 测试错误处理 - 无效事件类型
        EventRequest invalidRequest = new EventRequest();
        invalidRequest.setEventType("invalid_type_that_does_not_exist");
        invalidRequest.setEventName("test_event");
        invalidRequest.setTimestamp(System.currentTimeMillis());
        
        EventResponse response = analyticsService.recordEvent(invalidRequest);
        
        assertNotNull(response, "响应不应为空");
        assertFalse(response.isSuccess(), "无效事件类型应该失败");
        assertEquals(400, response.getCode(), "应该是400错误");
        
        System.out.println("错误处理测试通过: " + response.getMessage());
    }
    
    @Test
    void testBatchProcessing() {
        // 测试批量处理
        String sessionId = "batch-smoke-" + System.currentTimeMillis();
        
        EventRequest request1 = createTestRequest(sessionId, "page_view", "page1");
        EventRequest request2 = createTestRequest(sessionId, "click", "button1");
        EventRequest request3 = createTestRequest(sessionId, "selection", "option1");
        
        var responses = analyticsService.recordEvents(java.util.List.of(request1, request2, request3));
        
        assertNotNull(responses, "批量响应不应为空");
        assertEquals(3, responses.size(), "应该有3个响应");
        
        for (EventResponse response : responses) {
            assertTrue(response.isSuccess(), "每个事件都应该成功记录");
        }
        
        System.out.println("批量处理测试通过: 处理了" + responses.size() + "个事件");
    }
    
    // 辅助方法
    
    private void testEventType(String sessionId, String eventType, String eventName) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("sessionId", sessionId);
        eventData.put("testRun", true);
        eventData.put("eventType", eventType);
        
        EventRequest request = new EventRequest();
        request.setEventType(eventType);
        request.setEventName(eventName);
        request.setEventData(eventData);
        request.setTimestamp(System.currentTimeMillis());
        
        EventResponse response = analyticsService.recordEvent(request);
        
        assertNotNull(response, eventType + "响应不应为空");
        assertTrue(response.isSuccess(), eventType + "事件应该成功记录");
        assertEquals(eventType, response.getEventType(), "事件类型应该匹配");
    }
    
    private EventRequest createTestRequest(String sessionId, String eventType, String eventName) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("sessionId", sessionId);
        eventData.put("testRun", true);
        
        EventRequest request = new EventRequest();
        request.setEventType(eventType);
        request.setEventName(eventName);
        request.setEventData(eventData);
        request.setTimestamp(System.currentTimeMillis());
        
        return request;
    }
    
    @Test
    void testCompleteServiceLayer() {
        System.out.println("=== 服务层冒烟测试开始 ===");
        
        // 运行所有测试
        testServiceLayerInitialization();
        testBasicEventRecording();
        testMultipleEventTypes();
        testServiceHealthCheck();
        testServiceStats();
        testErrorHandling();
        testBatchProcessing();
        
        System.out.println("=== 服务层冒烟测试完成 ===");
        System.out.println("所有测试通过！服务层功能正常。");
    }
}