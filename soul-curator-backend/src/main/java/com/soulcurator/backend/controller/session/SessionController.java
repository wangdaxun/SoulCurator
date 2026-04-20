package com.soulcurator.backend.controller.session;

import com.soulcurator.backend.dto.ApiResponse;
import com.soulcurator.backend.dto.session.CreateSessionRequest;
import com.soulcurator.backend.dto.session.CreateSessionResponse;
import com.soulcurator.backend.service.session.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Session管理控制器
 */
@RestController
@RequestMapping("/api/v1/session")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Session", description = "会话管理API")
public class SessionController {
    
    private final SessionService sessionService;
    
    /**
     * 创建新会话
     */
    @PostMapping("/create")
    @Operation(summary = "创建新会话", 
               description = "创建新的灵魂探索会话，返回会话ID")
    public ResponseEntity<ApiResponse<CreateSessionResponse>> createSession(
            @RequestBody CreateSessionRequest request,
            HttpServletRequest httpRequest) {
        
        log.info("创建会话请求: gatewayType={}", request.getGatewayType());
        
        try {
            // 获取客户端信息
            String userAgent = httpRequest.getHeader("User-Agent");
            String ipAddress = getClientIpAddress(httpRequest);
            String screenResolution = request.getScreenResolution();
            
            // 创建会话
            String sessionId = sessionService.createSession(
                    request.getGatewayType(),
                    userAgent,
                    ipAddress,
                    screenResolution
            );
            
            // 构建响应
            CreateSessionResponse response = CreateSessionResponse.builder()
                    .sessionId(sessionId)
                    .gatewayType(request.getGatewayType())
                    .createdAt(System.currentTimeMillis())
                    .build();
            
            log.info("会话创建成功: sessionId={}", sessionId);
            
            return ResponseEntity.ok(ApiResponse.success(response, "会话创建成功"));
            
        } catch (Exception e) {
            log.error("创建会话失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("创建会话失败: " + e.getMessage()));
        }
    }
    
    /**
     * 验证会话
     */
    @GetMapping("/validate/{sessionId}")
    @Operation(summary = "验证会话", 
               description = "验证会话是否存在且有效")
    public ResponseEntity<ApiResponse<Boolean>> validateSession(
            @PathVariable String sessionId) {
        
        log.debug("验证会话: {}", sessionId);
        
        try {
            boolean isValid = sessionService.validateSession(sessionId);
            
            return ResponseEntity.ok(ApiResponse.success(isValid, 
                    isValid ? "会话有效" : "会话无效或已过期"));
            
        } catch (Exception e) {
            log.error("验证会话失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("验证会话失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}