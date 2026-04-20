package com.soulcurator.backend.dto.portrait;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 元数据DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetadataDTO {
    
    /**
     * 生成时间（ISO 8601格式）
     */
    @JsonProperty("generatedAt")
    private String generatedAt;
    
    /**
     * 灵魂ID（唯一标识）
     */
    @JsonProperty("soulId")
    private String soulId;
    
    /**
     * 节点名称（如：深空节点、心灵节点等）
     */
    @JsonProperty("node")
    private String node;
}