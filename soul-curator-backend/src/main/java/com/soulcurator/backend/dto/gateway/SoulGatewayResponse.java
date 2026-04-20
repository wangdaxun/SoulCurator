package com.soulcurator.backend.dto.gateway;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 灵魂之门入口响应DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SoulGatewayResponse {
    
    private String type;           // 入口类型：movie/literature/music/game
    private String color;          // 颜色代码
    private String icon;           // 图标名称
    private String title;          // 入口标题
    private String description;    // 入口描述
    
    // 扩展字段（可选）
    private String category;       // 分类
    private Integer displayOrder;  // 显示顺序
    private Boolean isActive;      // 是否启用
    private Integer popularityScore; // 热度评分
    
    /**
     * 从实体类转换
     */
    public static SoulGatewayResponse fromEntity(com.soulcurator.backend.model.gateway.SoulGateway entity) {
        if (entity == null) {
            return null;
        }
        
        SoulGatewayResponse response = new SoulGatewayResponse();
        response.setType(entity.getGatewayType());
        response.setColor(entity.getColorHex());
        response.setIcon(entity.getIcon());
        response.setTitle(entity.getName());
        response.setDescription(entity.getDescription());
        response.setCategory(entity.getCategory());
        response.setDisplayOrder(entity.getDisplayOrder());
        response.setIsActive(entity.getIsActive());
        response.setPopularityScore(entity.getPopularityScore());
        
        return response;
    }
}