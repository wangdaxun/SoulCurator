package com.soulcurator.backend.dto.exploration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 问题响应DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionResponse {
    
    private Long id;               // 问题ID
    private Integer stepNumber;    // 步骤编号
    private String title;          // 问题标题
    private String subtitle;       // 问题副标题
    private String description;    // 问题描述
    
    private List<OptionResponse> options; // 选项列表
    
    // 维度映射（可选）
    private Map<String, Object> dimensionMapping;
    
    // AI扩展字段（可选）
    private String dimensionId;    // 关联维度ID
    private String aiPrompt;       // AI提示词
    
    /**
     * 从实体类转换
     */
    public static QuestionResponse fromEntity(com.soulcurator.backend.model.question.Question entity) {
        if (entity == null) {
            return null;
        }
        
        QuestionResponse response = new QuestionResponse();
        response.setId(entity.getId());
        response.setStepNumber(entity.getStepNumber());
        response.setTitle(entity.getTitle());
        response.setSubtitle(entity.getSubtitle());
        response.setDescription(entity.getDescription());
        response.setDimensionId(entity.getDimensionId());
        response.setAiPrompt(entity.getAiPrompt());
        
        // 解析dimensionMapping
        try {
            if (entity.getDimensionMapping() != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                response.setDimensionMapping(mapper.readValue(entity.getDimensionMapping(), Map.class));
            }
        } catch (Exception e) {
            // JSON解析失败，忽略
        }
        
        return response;
    }
}