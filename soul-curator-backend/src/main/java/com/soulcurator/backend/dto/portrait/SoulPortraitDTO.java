package com.soulcurator.backend.dto.portrait;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 灵魂画像生成结果DTO
 * 对应前端需要的JSON结构
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoulPortraitDTO {
    
    /**
     * 灵魂类型，如：深度思考者、感性体验者、冒险探索者
     */
    @JsonProperty("soulType")
    private String soulType;
    
    /**
     * 灵魂描述
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * 特质列表
     */
    @JsonProperty("traits")
    private List<TraitDTO> traits;
    
    /**
     * 引用
     */
    @JsonProperty("quote")
    private QuoteDTO quote;
    
    /**
     * 推荐作品列表
     */
    @JsonProperty("recommendations")
    private List<RecommendationDTO> recommendations;
    
    /**
     * 元数据
     */
    @JsonProperty("metadata")
    private MetadataDTO metadata;
}