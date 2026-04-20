package com.soulcurator.backend.dto.exploration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.soulcurator.backend.dto.gateway.SoulGatewayResponse;
import lombok.Data;

import java.util.List;

/**
 * 开始灵魂探索响应DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartExplorationResponse {
    
    private String sessionId;      // 会话ID
    private String gatewayType;    // 入口类型
    
    private SoulGatewayResponse gatewayInfo; // 入口信息
    
    private List<QuestionResponse> questions; // 问题列表
    
    // 进度信息
    private ProgressInfo progress;
    
    // 时间戳
    private Long timestamp;
    
    /**
     * 进度信息
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ProgressInfo {
        private Integer currentStep;   // 当前步骤
        private Integer totalSteps;    // 总步骤数
        private Integer estimatedTime; // 预计完成时间（秒）
        
        public ProgressInfo(Integer currentStep, Integer totalSteps, Integer estimatedTime) {
            this.currentStep = currentStep;
            this.totalSteps = totalSteps;
            this.estimatedTime = estimatedTime;
        }
    }
    
    /**
     * 构建成功响应
     */
    public static StartExplorationResponse success(String sessionId, String gatewayType,
                                                   SoulGatewayResponse gatewayInfo,
                                                   List<QuestionResponse> questions) {
        StartExplorationResponse response = new StartExplorationResponse();
        response.setSessionId(sessionId);
        response.setGatewayType(gatewayType);
        response.setGatewayInfo(gatewayInfo);
        response.setQuestions(questions);
        response.setTimestamp(System.currentTimeMillis());
        
        // 设置进度信息
        if (questions != null && !questions.isEmpty()) {
            Integer totalSteps = questions.size();
            response.setProgress(new ProgressInfo(1, totalSteps, totalSteps * 30)); // 假设每个问题30秒
        }
        
        return response;
    }
}