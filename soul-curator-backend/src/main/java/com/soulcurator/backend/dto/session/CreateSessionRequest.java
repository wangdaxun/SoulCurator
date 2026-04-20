package com.soulcurator.backend.dto.session;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建会话请求")
public class CreateSessionRequest {
    
    @NotBlank(message = "入口类型不能为空")
    @Schema(description = "灵魂之门入口类型", example = "movie", required = true)
    private String gatewayType;
    
    @Schema(description = "屏幕分辨率", example = "1920x1080")
    private String screenResolution;
}