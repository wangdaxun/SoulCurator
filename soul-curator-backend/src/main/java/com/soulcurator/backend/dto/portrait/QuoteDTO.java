package com.soulcurator.backend.dto.portrait;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 引用DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteDTO {
    
    /**
     * 引用文本
     */
    @JsonProperty("text")
    private String text;
    
    /**
     * 作者
     */
    @JsonProperty("author")
    private String author;
}