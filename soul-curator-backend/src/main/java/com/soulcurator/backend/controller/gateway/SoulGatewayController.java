package com.soulcurator.backend.controller.gateway;

import com.soulcurator.backend.dto.ApiResponse;
import com.soulcurator.backend.dto.gateway.SoulGatewayResponse;
import com.soulcurator.backend.service.gateway.SoulGatewayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 灵魂之门入口控制器
 */
@RestController
@RequestMapping("/api/v1/soul-gateways")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "SoulGateways", description = "灵魂之门入口管理API")
public class SoulGatewayController {
    
    private final SoulGatewayService soulGatewayService;
    
    /**
     * 获取所有启用的灵魂之门入口
     */
    @GetMapping
    @Operation(summary = "获取所有启用的灵魂之门入口", 
               description = "返回所有启用的灵魂之门入口列表，按显示顺序排序")
    public ResponseEntity<ApiResponse<List<SoulGatewayResponse>>> getAllActiveGateways() {
        log.info("获取所有启用的灵魂之门入口");
        
        try {
            List<SoulGatewayResponse> gateways = soulGatewayService.getAllActiveGateways();
            
            if (gateways.isEmpty()) {
                log.warn("没有找到启用的灵魂之门入口");
                return ResponseEntity.ok(ApiResponse.success(gateways, "没有找到启用的灵魂之门入口"));
            }
            
            log.info("成功获取{}个灵魂之门入口", gateways.size());
            return ResponseEntity.ok(ApiResponse.success(gateways, "获取成功"));
            
        } catch (Exception e) {
            log.error("获取灵魂之门入口失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取灵魂之门入口失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取所有灵魂之门入口（包括未启用的）
     */
    @GetMapping("/all")
    @Operation(summary = "获取所有灵魂之门入口", 
               description = "返回所有灵魂之门入口列表（包括未启用的），按显示顺序排序")
    public ResponseEntity<ApiResponse<List<SoulGatewayResponse>>> getAllGateways() {
        log.info("获取所有灵魂之门入口");
        
        try {
            List<SoulGatewayResponse> gateways = soulGatewayService.getAllGateways();
            
            if (gateways.isEmpty()) {
                log.warn("没有找到灵魂之门入口");
                return ResponseEntity.ok(ApiResponse.success(gateways, "没有找到灵魂之门入口"));
            }
            
            log.info("成功获取{}个灵魂之门入口", gateways.size());
            return ResponseEntity.ok(ApiResponse.success(gateways, "获取成功"));
            
        } catch (Exception e) {
            log.error("获取所有灵魂之门入口失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取所有灵魂之门入口失败: " + e.getMessage()));
        }
    }
    
    /**
     * 根据入口类型获取灵魂之门入口
     */
    @GetMapping("/{gatewayType}")
    @Operation(summary = "根据入口类型获取灵魂之门入口", 
               description = "根据入口类型获取指定的灵魂之门入口信息")
    public ResponseEntity<ApiResponse<SoulGatewayResponse>> getGatewayByType(
            @Parameter(description = "入口类型（如：movie, literature, music, game）", required = true)
            @PathVariable String gatewayType) {
        
        log.info("根据入口类型获取灵魂之门入口: {}", gatewayType);
        
        try {
            return soulGatewayService.getGatewayByType(gatewayType)
                    .map(gateway -> {
                        log.info("成功获取灵魂之门入口: {}", gatewayType);
                        return ResponseEntity.ok(ApiResponse.success(gateway, "获取成功"));
                    })
                    .orElseGet(() -> {
                        log.warn("灵魂之门入口不存在: {}", gatewayType);
                        return ResponseEntity.ok(ApiResponse.error("灵魂之门入口不存在: " + gatewayType));
                    });
                    
        } catch (Exception e) {
            log.error("获取灵魂之门入口失败: {}", gatewayType, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取灵魂之门入口失败: " + e.getMessage()));
        }
    }
    
    /**
     * 检查入口类型是否启用
     */
    @GetMapping("/{gatewayType}/active")
    @Operation(summary = "检查入口类型是否启用", 
               description = "检查指定的灵魂之门入口是否启用")
    public ResponseEntity<ApiResponse<Boolean>> isGatewayActive(
            @Parameter(description = "入口类型（如：movie, literature, music, game）", required = true)
            @PathVariable String gatewayType) {
        
        log.info("检查入口类型是否启用: {}", gatewayType);
        
        try {
            boolean isActive = soulGatewayService.isGatewayActive(gatewayType);
            
            if (isActive) {
                log.info("入口类型已启用: {}", gatewayType);
                return ResponseEntity.ok(ApiResponse.success(true, "入口已启用"));
            } else {
                log.info("入口类型未启用或不存在: {}", gatewayType);
                return ResponseEntity.ok(ApiResponse.success(false, "入口未启用或不存在"));
            }
            
        } catch (Exception e) {
            log.error("检查入口类型是否启用失败: {}", gatewayType, e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("检查入口类型是否启用失败: " + e.getMessage()));
        }
    }
    
    /**
     * 获取入口统计信息
     */
    @GetMapping("/stats")
    @Operation(summary = "获取入口统计信息", 
               description = "获取灵魂之门入口的统计信息")
    public ResponseEntity<ApiResponse<SoulGatewayService.GatewayStats>> getGatewayStats() {
        log.info("获取入口统计信息");
        
        try {
            SoulGatewayService.GatewayStats stats = soulGatewayService.getGatewayStats();
            log.info("成功获取入口统计信息");
            return ResponseEntity.ok(ApiResponse.success(stats, "获取成功"));
            
        } catch (Exception e) {
            log.error("获取入口统计信息失败", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("获取入口统计信息失败: " + e.getMessage()));
        }
    }
}