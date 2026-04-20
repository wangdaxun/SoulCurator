package com.soulcurator.backend.controller.event;

import com.soulcurator.backend.dto.event.UserEventRequest;
import com.soulcurator.backend.dto.event.UserEventResponse;
import com.soulcurator.backend.service.event.UserEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户事件分析控制器
 * 保持原有的/v1/analytics路径，但内部使用user_events表
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "用户事件分析API")
public class AnalyticsController {

  private final UserEventService userEventService;

  /**
   * 记录单个用户事件
   */
  @PostMapping("/events")
  @Operation(summary = "记录用户事件", description = "记录用户行为事件到user_events表")
  public ResponseEntity<UserEventResponse> recordEvent(
      @RequestBody UserEventRequest request,
      HttpServletRequest httpRequest) {

    log.debug("收到用户事件记录请求: eventType={}, eventName={}, sessionId={}",
        request.getEventType(), request.getEventName(), request.getSessionId());

    // 自动填充请求信息
    enrichRequestWithHttpInfo(request, httpRequest);

    UserEventResponse response = userEventService.recordEvent(request);

    if (response.getSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * 批量记录用户事件
   */
  @PostMapping("/events/batch")
  @Operation(summary = "批量记录用户事件", description = "批量记录用户行为事件")
  public ResponseEntity<UserEventResponse> recordEvents(
      @RequestBody UserEventRequest[] requests,
      HttpServletRequest httpRequest) {

    log.debug("收到批量用户事件记录请求: 数量={}", requests.length);

    // 为每个请求自动填充信息
    for (UserEventRequest request : requests) {
      enrichRequestWithHttpInfo(request, httpRequest);
    }

    UserEventResponse response = userEventService.recordEvents(requests);

    if (response.getSuccess()) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  /**
   * 健康检查
   */
  @GetMapping("/health")
  @Operation(summary = "健康检查", description = "检查分析服务健康状态")
  public ResponseEntity<Map<String, Object>> healthCheck() {
    log.debug("收到健康检查请求");

    Map<String, Object> healthStatus = userEventService.getHealthStatus();

    if ("healthy".equals(healthStatus.get("status"))) {
      return ResponseEntity.ok(healthStatus);
    } else {
      return ResponseEntity.status(503).body(healthStatus);
    }
  }

  /**
   * 获取服务信息
   */
  @GetMapping("/info")
  @Operation(summary = "服务信息", description = "获取分析服务信息")
  public ResponseEntity<Map<String, Object>> getServiceInfo() {
    log.debug("收到服务信息请求");

    Map<String, Object> serviceInfo = userEventService.getServiceInfo();

    return ResponseEntity.ok(serviceInfo);
  }

  /**
   * 使用HTTP请求信息丰富事件请求
   */
  private void enrichRequestWithHttpInfo(UserEventRequest request, HttpServletRequest httpRequest) {
    if (httpRequest == null) {
      return;
    }

    // 设置IP地址
    String ipAddress = getClientIpAddress(httpRequest);
    request.setIpAddress(ipAddress);

    // 如果请求中没有用户代理，从HTTP请求中获取
    if (request.getUserAgent() == null || request.getUserAgent().isEmpty()) {
      String userAgent = httpRequest.getHeader("User-Agent");
      request.setUserAgent(userAgent);
    }

    // 如果请求中没有页面路径，从HTTP请求中获取
    if (request.getPagePath() == null || request.getPagePath().isEmpty()) {
      String pagePath = httpRequest.getRequestURI();
      request.setPagePath(pagePath);
    }

    // 如果请求中没有页面标题，使用默认值
    if (request.getPageTitle() == null || request.getPageTitle().isEmpty()) {
      request.setPageTitle("SoulCurator");
    }

    // 解析用户代理信息
    if (request.getUserAgent() != null && !request.getUserAgent().isEmpty()) {
      parseUserAgent(request);
    }
  }

  /**
   * 获取客户端IP地址
   */
  private String getClientIpAddress(HttpServletRequest request) {
    String ipAddress = request.getHeader("X-Forwarded-For");

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_CLIENT_IP");
    }

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
    }

    if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
    }

    // 对于多个代理的情况，取第一个IP
    if (ipAddress != null && ipAddress.contains(",")) {
      ipAddress = ipAddress.split(",")[0].trim();
    }

    return ipAddress;
  }

  /**
   * 解析用户代理字符串
   */
  private void parseUserAgent(UserEventRequest request) {
    String userAgent = request.getUserAgent().toLowerCase();

    // 检测设备类型
    if (userAgent.contains("mobile") || userAgent.contains("android") || userAgent.contains("iphone")) {
      request.setDeviceType("mobile");
    } else if (userAgent.contains("tablet") || userAgent.contains("ipad")) {
      request.setDeviceType("tablet");
    } else {
      request.setDeviceType("desktop");
    }

    // 检测屏幕分辨率（这里简化处理，实际应该从前端获取）
    if (request.getDeviceType().equals("mobile")) {
      request.setScreenResolution("375x667"); // 常见手机分辨率
    } else if (request.getDeviceType().equals("tablet")) {
      request.setScreenResolution("768x1024"); // 常见平板分辨率
    } else {
      request.setScreenResolution("1920x1080"); // 常见桌面分辨率
    }
  }
}