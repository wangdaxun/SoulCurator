package com.soulcurator.backend.dto.selection;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户选择记录请求DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSelectionRequest {
    
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    @NotBlank(message = "灵魂之门入口类型不能为空")
    private String gatewayType;
    
    @NotEmpty(message = "选择记录不能为空")
    @Valid
    private List<SelectionItem> selections;
    
    private Map<String, Object> metadata;
    
    /**
     * 单个选择项DTO
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SelectionItem {
        
        @NotNull(message = "问题ID不能为空")
        private Long questionId;
        
        @NotBlank(message = "选项ID不能为空")
        private String optionId;
        
        private Long selectedAt; // 选择时间戳（毫秒）
        
        private Integer stepNumber; // 步骤序号
        
        private Integer timeSpentSeconds; // 花费时间（秒）
        
        private Map<String, Object> itemMetadata; // 单个选项的元数据
    }
    
    /**
     * 验证请求是否有效
     */
    public boolean isValid() {
        return sessionId != null && !sessionId.trim().isEmpty() &&
               gatewayType != null && !gatewayType.trim().isEmpty() &&
               selections != null && !selections.isEmpty();
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