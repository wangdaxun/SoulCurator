package com.soulcurator.backend.dto.analytics;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EventRequestTest {

    @Test
    void testGettersAndSetters() {
        EventRequest request = new EventRequest();

        // 测试基本字段
        request.setEventType("click");
        request.setEventName("button_click");
        request.setSessionId("session-123");
        request.setIpAddress("192.168.1.1");
        request.setUserAgent("Test Agent");
        request.setPagePath("/test");
        request.setPageTitle("Test Page");
        request.setTimestamp(1234567890L);

        assertEquals("click", request.getEventType());
        assertEquals("button_click", request.getEventName());
        assertEquals("session-123", request.getSessionId());
        assertEquals("192.168.1.1", request.getIpAddress());
        assertEquals("Test Agent", request.getUserAgent());
        assertEquals("/test", request.getPagePath());
        assertEquals("Test Page", request.getPageTitle());
        assertEquals(1234567890L, request.getTimestamp());
    }

    @Test
    void testConstructors() {
        // 测试基础构造函数
        EventRequest request1 = new EventRequest("page_view", "homepage_visit", 1234567890L);
        assertEquals("page_view", request1.getEventType());
        assertEquals("homepage_visit", request1.getEventName());
        assertEquals(1234567890L, request1.getTimestamp());

        // 测试带eventData的构造函数
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");
        EventRequest request2 = new EventRequest("click", "button_click", data, 1234567890L);
        assertEquals("click", request2.getEventType());
        assertEquals("button_click", request2.getEventName());
        assertEquals(data, request2.getEventData());
        assertEquals(1234567890L, request2.getTimestamp());

        // 测试带sessionId的构造函数
        EventRequest request3 = new EventRequest("selection", "option_selected", "session-456", data, 1234567890L);
        assertEquals("selection", request3.getEventType());
        assertEquals("option_selected", request3.getEventName());
        assertEquals("session-456", request3.getSessionId());
        assertEquals(data, request3.getEventData());
        assertEquals(1234567890L, request3.getTimestamp());

        // 测试带ipAddress的构造函数
        EventRequest request4 = new EventRequest("error", "api_error", "session-789", "10.0.0.1", data, 1234567890L);
        assertEquals("error", request4.getEventType());
        assertEquals("api_error", request4.getEventName());
        assertEquals("session-789", request4.getSessionId());
        assertEquals("10.0.0.1", request4.getIpAddress());
        assertEquals(data, request4.getEventData());
        assertEquals(1234567890L, request4.getTimestamp());
    }

    @Test
    void testEventDataMethods() {
        EventRequest request = new EventRequest();

        // 测试getEventDataJson()空数据
        assertEquals("{}", request.getEventDataJson());

        // 测试带数据的getEventDataJson()
        Map<String, Object> data = new HashMap<>();
        data.put("key1", "value1");
        data.put("key2", 123);
        request.setEventData(data);

        String json = request.getEventDataJson();
        assertTrue(json.contains("key1") && json.contains("value1") && json.contains("key2") && json.contains("123"));

        // 测试getEventDataSummary()
        String summary = request.getEventDataSummary();
        assertTrue(summary.contains("key1: value1") || summary.contains("key2: 123"));

        // 测试大量数据时的摘要
        for (int i = 0; i < 10; i++) {
            data.put("key" + i, "value" + i);
        }
        request.setEventData(data);
        summary = request.getEventDataSummary();
        assertTrue(summary.contains("...") || summary.split(", ").length <= 3);
    }

    @Test
    void testValidationMethods() {
        EventRequest request = new EventRequest();

        // 测试有效事件类型
        request.setEventType("page_view");
        assertTrue(request.isValidEventType());

        request.setEventType("click");
        assertTrue(request.isValidEventType());

        request.setEventType("selection");
        assertTrue(request.isValidEventType());

        request.setEventType("custom");
        assertTrue(request.isValidEventType());

        // 测试无效事件类型
        request.setEventType("invalid_type");
        assertFalse(request.isValidEventType());

        // 测试null事件类型
        request.setEventType(null);
        assertFalse(request.isValidEventType());
    }

    @Test
    void testDisplayMethods() {
        EventRequest request = new EventRequest("click", "button_submit", 1234567890L);

        // 测试事件类型显示
        assertEquals("点击事件", request.getEventTypeDisplay());

        // 测试事件名称显示（下划线转空格，首字母大写）
        assertEquals("Button Submit", request.getEventNameDisplay());

        // 测试完整事件描述
        assertEquals("点击事件: Button Submit", request.getEventDescription());

        // 测试时间显示方法
        request.setTimestamp(1234567890000L); // 2009-02-13 23:31:30 UTC
        String timeDisplay = request.getTimeDisplay();
        assertNotNull(timeDisplay);
        assertTrue(timeDisplay.contains("2009") || timeDisplay.contains("未知时间"));
    }

    @Test
    void testToString() {
        EventRequest request = new EventRequest("click", "button_click", "session-123", 1234567890L);
        String str = request.toString();
        assertTrue(str.contains("click"));
        assertTrue(str.contains("button_click"));
        assertTrue(str.contains("session-123"));
        assertTrue(str.contains("1234567890"));
    }
}