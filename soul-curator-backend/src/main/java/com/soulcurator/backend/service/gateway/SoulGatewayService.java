package com.soulcurator.backend.service.gateway;

import com.soulcurator.backend.dto.gateway.SoulGatewayResponse;
import com.soulcurator.backend.model.gateway.SoulGateway;
import com.soulcurator.backend.repository.gateway.SoulGatewayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 灵魂之门入口服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SoulGatewayService {
    
    private final SoulGatewayRepository soulGatewayRepository;
    
    /**
     * 获取所有启用的灵魂之门入口
     */
    public List<SoulGatewayResponse> getAllActiveGateways() {
        log.debug("获取所有启用的灵魂之门入口");
        
        List<SoulGateway> gateways = soulGatewayRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
        
        return gateways.stream()
                .map(SoulGatewayResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取所有灵魂之门入口（包括未启用的）
     */
    public List<SoulGatewayResponse> getAllGateways() {
        log.debug("获取所有灵魂之门入口");
        
        List<SoulGateway> gateways = soulGatewayRepository.findAllByOrderByDisplayOrderAsc();
        
        return gateways.stream()
                .map(SoulGatewayResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据入口类型获取灵魂之门入口
     */
    public Optional<SoulGatewayResponse> getGatewayByType(String gatewayType) {
        log.debug("根据入口类型获取灵魂之门入口: {}", gatewayType);
        
        if (gatewayType == null || gatewayType.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return soulGatewayRepository.findByGatewayType(gatewayType.trim())
                .map(SoulGatewayResponse::fromEntity);
    }
    
    /**
     * 检查入口类型是否存在且启用
     */
    public boolean isGatewayActive(String gatewayType) {
        log.debug("检查入口类型是否启用: {}", gatewayType);
        
        if (gatewayType == null || gatewayType.trim().isEmpty()) {
            return false;
        }
        
        return soulGatewayRepository.isGatewayActive(gatewayType.trim());
    }
    
    /**
     * 增加入口的热度评分
     */
    public void incrementPopularity(String gatewayType) {
        log.debug("增加入口热度评分: {}", gatewayType);
        
        if (gatewayType == null || gatewayType.trim().isEmpty()) {
            return;
        }
        
        try {
            soulGatewayRepository.incrementPopularityScore(gatewayType.trim(), 1);
            log.info("入口热度评分增加成功: {}", gatewayType);
        } catch (Exception e) {
            log.error("增加入口热度评分失败: {}", gatewayType, e);
        }
    }
    
    /**
     * 获取入口统计信息
     */
    public GatewayStats getGatewayStats() {
        log.debug("获取入口统计信息");
        
        List<SoulGateway> allGateways = soulGatewayRepository.findAll();
        List<SoulGateway> activeGateways = soulGatewayRepository.findByIsActiveTrue();
        
        GatewayStats stats = new GatewayStats();
        stats.setTotalGateways(allGateways.size());
        stats.setActiveGateways(activeGateways.size());
        stats.setInactiveGateways(allGateways.size() - activeGateways.size());
        
        // 计算平均热度
        double avgPopularity = allGateways.stream()
                .mapToInt(SoulGateway::getPopularityScore)
                .average()
                .orElse(0.0);
        stats.setAveragePopularity(avgPopularity);
        
        // 获取最热门的入口
        allGateways.stream()
                .max((g1, g2) -> Integer.compare(g1.getPopularityScore(), g2.getPopularityScore()))
                .ifPresent(gateway -> {
                    stats.setMostPopularGateway(gateway.getGatewayType());
                    stats.setMostPopularScore(gateway.getPopularityScore());
                });
        
        return stats;
    }
    
    /**
     * 入口统计信息
     */
    @lombok.Data
    public static class GatewayStats {
        private int totalGateways;
        private int activeGateways;
        private int inactiveGateways;
        private double averagePopularity;
        private String mostPopularGateway;
        private Integer mostPopularScore;
    }
}