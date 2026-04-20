package com.soulcurator.backend.dto.selection;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户选择记录响应DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSelectionResponse {
    
    private String sessionId;
    
    private String gatewayType;
    
    private Integer totalSelections;
    
    private Integer completedQuestions;
    
    private Integer totalQuestions;
    
    private Double completionPercentage;
    
    private LocalDateTime firstSelectionAt;
    
    private LocalDateTime lastSelectionAt;
    
    private Long explorationDurationMs;
    
    private List<SelectionSummary> selections;
    
    /**
     * 选择项摘要
     */
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SelectionSummary {
        
        private Long questionId;
        
        private String questionTitle;
        
        private String optionId;
        
        private String optionTitle;
        
        private Integer stepNumber;
        
        private LocalDateTime selectedAt;
        
        private Integer timeSpentSeconds;
    }
}