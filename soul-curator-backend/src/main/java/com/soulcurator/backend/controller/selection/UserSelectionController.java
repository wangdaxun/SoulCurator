package com.soulcurator.backend.controller.selection;

import com.soulcurator.backend.dto.ApiResponse;
import com.soulcurator.backend.dto.selection.UserSelectionRequest;
import com.soulcurator.backend.dto.selection.UserSelectionResponse;
import com.soulcurator.backend.service.selection.UserSelectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户选择记录控制器
 */
@RestController
@RequestMapping("/api/v1/selections")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "UserSelection", description = "用户选择记录API")
public class UserSelectionController {
    
    private final UserSelectionService userSelectionService;
    
    /**
     * 记录用户选择
     */
    @PostMapping("/record")
    @Operation(summary = "记录用户选择", 
               description = "记录用户在灵魂探索过程中的选择")
    public ResponseEntity<ApiResponse<UserSelectionResponse>> recordSelections(
            @Parameter(description = "用户选择记录请求", required = true)
            @Valid @RequestBody UserSelectionRequest request) {
        
        log.info("收到用户选择记录请求: sessionId={}, gatewayType={}, selections={}", 
                request.getSessionId(), request.getGatewayType(), 
                request.getSelections() != null ? request.getSelections().size() : 0);
        
        try {
            // 验证请求
            if (!request.isValid()) {
                log.warn("用户选择记录请求无效");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("请求参数无效"));
            }
            
            // 处理请求
            UserSelectionResponse response = userSelectionService.recordSelections(request);
            
            log.info("用户选择记录成功: sessionId={}, totalSelections={}, completion={}%", 
                    response.getSessionId(), response.getTotalSelections(), 
                    response.getCompletionPercentage());
            
            return ResponseEntity.ok(ApiResponse.success(response, "选择记录成功"));
            
        } catch (IllegalArgumentException e) {
            log.warn("用户选择记录参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (IllegalStateException e) {
            log.warn("用户选择记录状态错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("用户选择记录失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("记录用户选择失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户选择记录
     */
    @GetMapping
    @Operation(summary = "获取用户选择记录", 
               description = "根据会话ID获取用户的灵魂探索选择记录")
    public ResponseEntity<ApiResponse<UserSelectionResponse>> getSelections(
            @Parameter(description = "会话ID", required = true)
            @RequestParam String sessionId,
            
            @Parameter(description = "灵魂之门入口类型（可选）")
            @RequestParam(required = false) String gatewayType) {
        
        log.info("获取用户选择记录: sessionId={}, gatewayType={}", sessionId, gatewayType);
        
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("会话ID不能为空");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("会话ID不能为空"));
            }
            
            UserSelectionResponse response = userSelectionService.getSelections(
                    sessionId.trim(), gatewayType != null ? gatewayType.trim() : null);
            
            log.info("成功获取用户选择记录: sessionId={}, totalSelections={}", 
                    sessionId, response.getTotalSelections());
            
            return ResponseEntity.ok(ApiResponse.success(response, "获取成功"));
            
        } catch (IllegalArgumentException e) {
            log.warn("获取用户选择记录参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("获取用户选择记录失败: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取用户选择记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取用户选择摘要
     */
    @GetMapping("/summary")
    @Operation(summary = "获取用户选择摘要", 
               description = "获取用户选择记录的统计摘要信息")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSelectionSummary(
            @Parameter(description = "会话ID", required = true)
            @RequestParam String sessionId) {
        
        log.info("获取用户选择摘要: sessionId={}", sessionId);
        
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("会话ID不能为空");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("会话ID不能为空"));
            }
            
            Map<String, Object> summary = userSelectionService.getSelectionSummary(sessionId.trim());
            
            log.info("成功获取用户选择摘要: sessionId={}", sessionId);
            
            return ResponseEntity.ok(ApiResponse.success(summary, "获取成功"));
            
        } catch (IllegalArgumentException e) {
            log.warn("获取用户选择摘要参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("获取用户选择摘要失败: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取用户选择摘要失败: " + e.getMessage()));
        }
    }
    
    /**
     * 删除用户选择记录
     */
    @DeleteMapping
    @Operation(summary = "删除用户选择记录", 
               description = "删除指定会话的用户选择记录")
    public ResponseEntity<ApiResponse<Void>> deleteSelections(
            @Parameter(description = "会话ID", required = true)
            @RequestParam String sessionId,
            
            @Parameter(description = "灵魂之门入口类型（可选，不传则删除所有）")
            @RequestParam(required = false) String gatewayType) {
        
        log.info("删除用户选择记录: sessionId={}, gatewayType={}", sessionId, gatewayType);
        
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("会话ID不能为空");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("会话ID不能为空"));
            }
            
            userSelectionService.deleteSelections(
                    sessionId.trim(), 
                    gatewayType != null ? gatewayType.trim() : null);
            
            log.info("成功删除用户选择记录: sessionId={}", sessionId);
            
            return ResponseEntity.ok(ApiResponse.success(null, "删除成功"));
            
        } catch (IllegalArgumentException e) {
            log.warn("删除用户选择记录参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("删除用户选择记录失败: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("删除用户选择记录失败: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", 
               description = "检查用户选择记录服务是否健康")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        log.debug("用户选择记录服务健康检查");
        
        try {
            return ResponseEntity.ok(ApiResponse.success("OK", "用户选择记录服务运行正常"));
            
        } catch (Exception e) {
            log.error("用户选择记录服务健康检查失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("用户选择记录服务异常: " + e.getMessage()));
        }
    }
}