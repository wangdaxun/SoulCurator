package com.soulcurator.backend.dto.exploration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

/**
 * 开始灵魂探索请求DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StartExplorationRequest {
    
    private String gatewayType;    // 灵魂之门入口类型
    
    // 可选字段
    private String sessionId;      // 已有会话ID（用于继续探索）
    private Map<String, Object> metadata; // 元数据
    
    /**
     * 验证请求是否有效
     */
    public boolean isValid() {
        return gatewayType != null && !gatewayType.trim().isEmpty();
    }
    
    /**
     * 获取清理后的gatewayType
     */
    public String getCleanGatewayType() {
        return gatewayType != null ? gatewayType.trim().toLowerCase() : null;
    }
    
    /**
     * 获取清理后的sessionId
     */
    public String getCleanSessionId() {
        return sessionId != null ? sessionId.trim() : null;
    }
}