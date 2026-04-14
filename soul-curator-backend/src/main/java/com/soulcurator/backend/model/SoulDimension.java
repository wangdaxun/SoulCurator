package com.soulcurator.backend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 灵魂维度定义实体类
 * 对应数据库表: soul_dimensions
 */
@Entity
@Table(name = "soul_dimensions")
@Data
public class SoulDimension {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    /**
     * 维度名称（中文）
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    
    /**
     * 维度英文名称
     */
    @Column(name = "english_name", nullable = false, length = 50)
    private String englishName;
    
    /**
     * 维度描述
     */
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    
    /**
     * 图标名称
     */
    @Column(name = "icon", length = 50)
    private String icon;
    
    /**
     * 颜色代码
     */
    @Column(name = "color_hex", length = 7)
    private String colorHex;
    
    /**
     * 分类
     * 可选值: perception, thinking, emotion, social, order, reality, expression
     */
    @Column(name = "category", length = 50)
    private String category;
    
    /**
     * 显示顺序
     */
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // 构造方法
    public SoulDimension() {}
    
    public SoulDimension(String id, String name, String englishName, String description) {
        this.id = id;
        this.name = name;
        this.englishName = englishName;
        this.description = description;
    }
    
    public SoulDimension(String id, String name, String englishName, String description, 
                        String icon, String colorHex, String category) {
        this.id = id;
        this.name = name;
        this.englishName = englishName;
        this.description = description;
        this.icon = icon;
        this.colorHex = colorHex;
        this.category = category;
    }
    
    // 便捷方法
    
    /**
     * 获取分类的中文显示
     */
    public String getCategoryDisplay() {
        if (category == null) {
            return "未分类";
        }
        
        return switch (category) {
            case "perception" -> "感知维度";
            case "thinking" -> "思维维度";
            case "emotion" -> "情感维度";
            case "social" -> "社会维度";
            case "order" -> "秩序维度";
            case "reality" -> "现实维度";
            case "expression" -> "表达维度";
            default -> category;
        };
    }
    
    /**
     * 获取分类的图标
     */
    public String getCategoryIcon() {
        if (category == null) {
            return "📌";
        }
        
        return switch (category) {
            case "perception" -> "👁️";
            case "thinking" -> "🧠";
            case "emotion" -> "💖";
            case "social" -> "👥";
            case "order" -> "📐";
            case "reality" -> "🌍";
            case "expression" -> "🗣️";
            default -> "📌";
        };
    }
    
    /**
     * 获取完整的维度名称
     */
    public String getFullName() {
        return name + " (" + englishName + ")";
    }
    
    /**
     * 获取带图标的维度名称
     */
    public String getNameWithIcon() {
        if (icon != null && !icon.isEmpty()) {
            // 这里需要根据icon名称转换为实际emoji
            // 简化处理，直接返回icon
            return icon + " " + name;
        }
        return name;
    }
    
    /**
     * 获取简化的描述（截断）
     */
    public String getShortDescription(int maxLength) {
        if (description.length() <= maxLength) {
            return description;
        }
        return description.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * 获取颜色代码，带默认值
     */
    public String getSafeColorHex() {
        if (colorHex != null && !colorHex.isEmpty()) {
            return colorHex;
        }
        
        // 根据分类返回默认颜色
        if (category != null) {
            return switch (category) {
                case "perception" -> "#3B82F6"; // 蓝色
                case "thinking" -> "#6366F1";   // 靛蓝色
                case "emotion" -> "#EC4899";    // 粉色
                case "social" -> "#10B981";     // 绿色
                case "order" -> "#F59E0B";      // 橙色
                case "reality" -> "#8B5CF6";    // 紫色
                case "expression" -> "#06B6D4"; // 青色
                default -> "#6B7280";           // 灰色
            };
        }
        return "#6B7280"; // 默认灰色
    }
    
    /**
     * 获取RGB颜色值
     */
    public int[] getRgbColor() {
        String hex = getSafeColorHex();
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        
        try {
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);
            return new int[]{r, g, b};
        } catch (Exception e) {
            return new int[]{107, 114, 128}; // 默认灰色
        }
    }
    
    /**
     * 获取CSS颜色字符串
     */
    public String getCssColor() {
        return getSafeColorHex();
    }
    
    /**
     * 获取CSS渐变颜色
     */
    public String getCssGradient() {
        int[] rgb = getRgbColor();
        // 创建稍微亮一点的版本
        int[] lighterRgb = new int[]{
            Math.min(rgb[0] + 30, 255),
            Math.min(rgb[1] + 30, 255),
            Math.min(rgb[2] + 30, 255)
        };
        
        String hex1 = String.format("#%02x%02x%02x", rgb[0], rgb[1], rgb[2]);
        String hex2 = String.format("#%02x%02x%02x", lighterRgb[0], lighterRgb[1], lighterRgb[2]);
        
        return String.format("linear-gradient(135deg, %s, %s)", hex1, hex2);
    }
    
    /**
     * 判断是否是核心维度（前8个）
     */
    public boolean isCoreDimension() {
        return displayOrder != null && displayOrder < 8;
    }
    
    /**
     * 判断是否是感知维度
     */
    public boolean isPerceptionDimension() {
        return "perception".equals(category);
    }
    
    /**
     * 判断是否是思维维度
     */
    public boolean isThinkingDimension() {
        return "thinking".equals(category);
    }
    
    /**
     * 判断是否是情感维度
     */
    public boolean isEmotionDimension() {
        return "emotion".equals(category);
    }
    
    /**
     * 获取维度得分等级
     */
    public String getScoreLevel(int score) {
        if (score >= 8) return "极高";
        if (score >= 6) return "高";
        if (score >= 4) return "中";
        if (score >= 2) return "低";
        return "极低";
    }
    
    /**
     * 获取维度得分颜色
     */
    public String getScoreColor(int score) {
        if (score >= 8) return "#10B981"; // 绿色
        if (score >= 6) return "#3B82F6"; // 蓝色
        if (score >= 4) return "#F59E0B"; // 橙色
        if (score >= 2) return "#EF4444"; // 红色
        return "#6B7280"; // 灰色
    }
    
    /**
     * 获取维度得分的进度条宽度（0-100）
     */
    public int getScoreProgressWidth(int score) {
        // 分数范围是0-10，转换为0-100
        return Math.min(score * 10, 100);
    }
    
    @Override
    public String toString() {
        return String.format("SoulDimension{id='%s', name='%s', category='%s'}", 
                id, name, category);
    }
}