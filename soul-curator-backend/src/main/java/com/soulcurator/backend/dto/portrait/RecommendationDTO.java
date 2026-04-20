package com.soulcurator.backend.dto.portrait;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推荐作品DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    
    /**
     * 作品类型：movie/music/book
     */
    @JsonProperty("type")
    private String type;
    
    /**
     * 作品标题
     */
    @JsonProperty("title")
    private String title;
    
    /**
     * 作品描述
     */
    @JsonProperty("description")
    private String description;
    
    /**
     * 图片URL
     */
    @JsonProperty("imageUrl")
    private String imageUrl;
}