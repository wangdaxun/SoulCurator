package com.soulcurator.backend.dto.portrait;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 特质DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TraitDTO {
    
    /**
     * 特质名称
     */
    @JsonProperty("name")
    private String name;
    
    /**
     * 图标名称（前端使用的图标标识）
     */
    @JsonProperty("icon")
    private String icon;
    
    /**
     * 颜色代码（十六进制）
     */
    @JsonProperty("color")
    private String color;
}