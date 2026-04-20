package com.soulcurator.backend.controller.exploration;

import com.soulcurator.backend.dto.ApiResponse;
import com.soulcurator.backend.dto.exploration.StartExplorationRequest;
import com.soulcurator.backend.dto.exploration.StartExplorationResponse;
import com.soulcurator.backend.service.exploration.SoulExplorationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 灵魂探索控制器
 */
@RestController
@RequestMapping("/api/v1/soul-exploration")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SoulExploration", description = "灵魂探索流程API")
public class SoulExplorationController {
    
    private final SoulExplorationService soulExplorationService;
    
    /**
     * 开始灵魂探索
     */
    @PostMapping("/start")
    @Operation(summary = "开始灵魂探索", 
               description = "用户选择灵魂之门后开始探索，生成会话ID和问题列表")
    public ResponseEntity<ApiResponse<StartExplorationResponse>> startExploration(
            @Parameter(description = "开始探索请求", required = true)
            @Valid @RequestBody StartExplorationRequest request) {
        
        log.info("开始灵魂探索请求: gatewayType={}, sessionId={}", 
                request.getGatewayType(), request.getSessionId());
        
        try {
            // 验证请求
            if (!request.isValid()) {
                log.warn("开始灵魂探索请求无效: gatewayType={}", request.getGatewayType());
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("入口类型不能为空"));
            }
            
            // 处理请求
            StartExplorationResponse response = soulExplorationService.startExploration(request);
            
            log.info("灵魂探索开始成功: sessionId={}, gatewayType={}, questions={}", 
                    response.getSessionId(), response.getGatewayType(), 
                    response.getQuestions() != null ? response.getQuestions().size() : 0);
            
            return ResponseEntity.ok(ApiResponse.success(response, "灵魂探索开始成功"));
            
        } catch (IllegalArgumentException e) {
            log.warn("开始灵魂探索参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (IllegalStateException e) {
            log.warn("开始灵魂探索状态错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("开始灵魂探索失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("开始灵魂探索失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取探索进度
     */
    @GetMapping("/progress/{sessionId}")
    @Operation(summary = "获取探索进度", 
               description = "根据会话ID获取灵魂探索的进度信息")
    public ResponseEntity<ApiResponse<SoulExplorationService.ExplorationProgress>> getExplorationProgress(
            @Parameter(description = "会话ID", required = true)
            @PathVariable String sessionId) {
        
        log.info("获取探索进度: sessionId={}", sessionId);
        
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("会话ID不能为空");
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("会话ID不能为空"));
            }
            
            SoulExplorationService.ExplorationProgress progress = 
                    soulExplorationService.getExplorationProgress(sessionId.trim());
            
            log.info("成功获取探索进度: sessionId={}, completion={}%", 
                    sessionId, progress.getCompletionPercentage());
            
            return ResponseEntity.ok(ApiResponse.success(progress, "获取成功"));
            
        } catch (IllegalArgumentException e) {
            log.warn("获取探索进度参数错误: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
                    
        } catch (Exception e) {
            log.error("获取探索进度失败: sessionId={}", sessionId, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取探索进度失败: " + e.getMessage()));
        }
    }
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", 
               description = "检查灵魂探索服务是否健康")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        log.debug("灵魂探索服务健康检查");
        
        try {
            return ResponseEntity.ok(ApiResponse.success("OK", "灵魂探索服务运行正常"));
            
        } catch (Exception e) {
            log.error("灵魂探索服务健康检查失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("灵魂探索服务异常: " + e.getMessage()));
        }
    }
}