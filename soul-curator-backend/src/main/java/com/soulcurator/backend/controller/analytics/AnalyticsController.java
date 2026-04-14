package com.soulcurator.backend.controller.analytics;

import com.soulcurator.backend.dto.analytics.EventRequest;
import com.soulcurator.backend.dto.analytics.EventResponse;
import com.soulcurator.backend.service.analytics.AnalyticsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 分析数据API控制器
 * 提供用户行为埋点和数据分析接口
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    /**
     * 记录单个用户事件
     * POST /api/v1/analytics/events
     */
    @PostMapping("/events")
    public ResponseEntity<EventResponse> recordEvent(
            @Valid @RequestBody EventRequest request,
            @RequestHeader(value = "User-Agent", required = false) String userAgent,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ipAddress) {
        
        log.info("收到事件记录请求: sessionId={}, eventType={}, eventName={}", 
                request.getSessionId(), request.getEventType(), request.getEventName());
        
        // 从请求头获取用户代理和IP（如果请求体中没有）
        if (request.getUserAgent() == null && userAgent != null) {
            request.setUserAgent(userAgent);
        }
        if (request.getIpAddress() == null && ipAddress != null) {
            // 取第一个IP（可能有多个，如X-Forwarded-For: client, proxy1, proxy2）
            String firstIp = ipAddress.split(",")[0].trim();
            request.setIpAddress(firstIp);
        }
        
        EventResponse response = analyticsService.recordEvent(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 健康检查接口
     * GET /api/v1/analytics/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Analytics API is healthy");
    }
    
    /**
     * 获取API信息
     * GET /api/v1/analytics/info
     */
    @GetMapping("/info")
    public ResponseEntity<ApiInfo> getApiInfo() {
        ApiInfo info = new ApiInfo(
                "SoulCurator Analytics API",
                "v1.0",
                "用户行为埋点和数据分析接口",
                "2026-04-13"
        );
        return ResponseEntity.ok(info);
    }
    
    /**
     * API信息DTO
     */
    public record ApiInfo(
            String name,
            String version,
            String description,
            String lastUpdated
    ) {}
    
    /**
     * 全局异常处理
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<EventResponse> handleException(Exception e) {
        log.error("处理事件记录时发生异常", e);
        
        EventResponse response = new EventResponse();
        response.setSuccess(false);
        response.setMessage("服务器内部错误: " + e.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
    
    /**
     * 验证错误处理
     */
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<EventResponse> handleValidationException(
            org.springframework.web.bind.MethodArgumentNotValidException e) {
        
        String errorMessage = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("请求参数验证失败");
        
        EventResponse response = new EventResponse();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}