package com.soulcurator.backend.dto.portrait;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成灵魂画像请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePortraitRequest {
    
    /**
     * 用户会话ID
     */
    @JsonProperty("sessionId")
    private String sessionId;
    
    /**
     * 用户ID（可选，匿名用户可能没有）
     */
    @JsonProperty("userId")
    private String userId;
}