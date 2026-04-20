package com.soulcurator.backend.dto.exploration;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 选项响应DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionResponse {
    
    private String id;             // 选项ID
    private String title;          // 选项标题
    private String emoji;          // 表情符号
    private String description;    // 选项描述
    private List<String> references; // 参考作品
    
    // 维度信息
    private Map<String, Object> dimensionScores; // 维度得分
    
    // AI扩展字段（可选）
    private Map<String, Object> weight;          // 权重配置
    private String nextQuestionId;               // 下一个问题ID
    private String aiContext;                    // AI上下文
    
    /**
     * 从实体类转换
     */
    public static OptionResponse fromEntity(com.soulcurator.backend.model.option.Option entity) {
        if (entity == null) {
            return null;
        }
        
        OptionResponse response = new OptionResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setEmoji(entity.getEmoji());
        response.setDescription(entity.getDescription());
        
        // 解析JSON字段
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            
            // 解析workReferences (text[] 数组)
            if (entity.getWorkReferences() != null) {
                response.setReferences(Arrays.asList(entity.getWorkReferences()));
            }
            
            // 解析dimensionScores
            if (entity.getDimensionScores() != null) {
                response.setDimensionScores(mapper.readValue(entity.getDimensionScores(), Map.class));
            }
            
            // 解析weight
            if (entity.getWeight() != null) {
                response.setWeight(mapper.readValue(entity.getWeight(), Map.class));
            }
            
        } catch (Exception e) {
            // JSON解析失败，使用默认值
        }
        
        response.setNextQuestionId(entity.getNextQuestionId());
        response.setAiContext(entity.getAiContext());
        
        return response;
    }
}