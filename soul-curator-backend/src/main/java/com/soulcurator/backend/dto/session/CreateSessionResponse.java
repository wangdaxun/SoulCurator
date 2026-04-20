package com.soulcurator.backend.dto.session;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "创建会话响应")
public class CreateSessionResponse {
    
    @Schema(description = "会话ID", example = "soul_session_1776422453930_abc123def456")
    private String sessionId;
    
    @Schema(description = "灵魂之门入口类型", example = "movie")
    private String gatewayType;
    
    @Schema(description = "创建时间戳", example = "1776422453930")
    private Long createdAt;
}