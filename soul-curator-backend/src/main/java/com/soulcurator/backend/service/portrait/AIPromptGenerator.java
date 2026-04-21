package com.soulcurator.backend.service.portrait;

import com.soulcurator.backend.dto.portrait.SoulPortraitDTO;
import com.soulcurator.backend.dto.portrait.TraitDTO;
import com.soulcurator.backend.dto.portrait.RecommendationDTO;
import com.soulcurator.backend.model.prototype.SoulPrototype;
import com.soulcurator.backend.model.selection.UserSelection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI提示词生成器
 * 负责生成用于AI模型的提示词
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AIPromptGenerator {
    
    private final ObjectMapper objectMapper;
    
    /**
     * 生成AI提示词
     * 
     * @param portrait 灵魂画像
     * @param prototype 灵魂原型
     * @param selections 用户选择记录
     * @return AI提示词
     */
    public String generateAIPrompt(SoulPortraitDTO portrait, 
                                  SoulPrototype prototype, 
                                  List<UserSelection> selections) {
        try {
            // 解析原型的维度权重
            Map<String, Integer> dimensionWeights = objectMapper.readValue(
                prototype.getDimensionWeights(), 
                new TypeReference<Map<String, Integer>>() {});
            
            // 获取主要维度
            List<String> topDimensions = getTopDimensions(dimensionWeights, 3);
            
            // 获取特质列表
            List<TraitDTO> traits = portrait.getTraits();
            String traitsText = traits.stream()
                .map(TraitDTO::getName)
                .collect(Collectors.joining("、"));
            
            // 获取推荐作品
            List<RecommendationDTO> recommendations = portrait.getRecommendations();
            String recommendationsText = recommendations.stream()
                .map(rec -> String.format("%s《%s》", getTypeChinese(rec.getType()), rec.getTitle()))
                .collect(Collectors.joining("、"));
            
            // 构建提示词
            StringBuilder prompt = new StringBuilder();
            
            // 1. 角色设定
            prompt.append("你是一个").append(portrait.getSoulType()).append("。\n\n");
            
            // 2. 特质描述
            prompt.append("## 你的特质\n");
            prompt.append("你具有以下特质：").append(traitsText).append("。\n\n");
            
            // 3. 思维倾向
            prompt.append("## 思维倾向\n");
            prompt.append("你的思维主要倾向于：").append(String.join("、", topDimensions)).append("。\n\n");
            
            // 4. 灵魂描述
            prompt.append("## 灵魂描述\n");
            prompt.append(portrait.getDescription()).append("\n\n");
            
            // 5. 引用启示
            if (portrait.getQuote() != null) {
                prompt.append("## 启示\n");
                prompt.append("「").append(portrait.getQuote().getText()).append("」");
                prompt.append(" —— ").append(portrait.getQuote().getAuthor()).append("\n\n");
            }
            
            // 6. 推荐作品
            prompt.append("## 推荐作品\n");
            prompt.append("适合你的作品包括：").append(recommendationsText).append("。\n\n");
            
            // 7. AI任务指令
            prompt.append("## 任务指令\n");
            prompt.append("基于以上信息，请：\n");
            prompt.append("1. 生成一段个性化的自我描述\n");
            prompt.append("2. 分析你的思维模式特点\n");
            prompt.append("3. 推荐适合你的其他内容（书籍、电影、音乐等）\n");
            prompt.append("4. 提供个人成长的建议\n\n");
            
            // 8. 输出格式
            prompt.append("## 输出格式\n");
            prompt.append("请用中文回复，保持文艺而深刻的风格。\n");
            
            return prompt.toString();
            
        } catch (Exception e) {
            log.error("生成AI提示词失败", e);
            return generateDefaultPrompt(portrait);
        }
    }
    
    /**
     * 获取主要维度
     */
    private List<String> getTopDimensions(Map<String, Integer> dimensionWeights, int topN) {
        return dimensionWeights.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(topN)
            .map(entry -> translateDimension(entry.getKey()))
            .collect(Collectors.toList());
    }
    
    /**
     * 翻译维度名称
     */
    private String translateDimension(String dimension) {
        Map<String, String> dimensionMap = Map.of(
            "introspection", "内省思考",
            "logic", "逻辑分析", 
            "art", "艺术感知",
            "adventure", "冒险探索",
            "emotion", "情感体验",
            "curiosity", "好奇求知"
        );
        return dimensionMap.getOrDefault(dimension, dimension);
    }
    
    /**
     * 获取类型中文名
     */
    private String getTypeChinese(String type) {
        Map<String, String> typeMap = Map.of(
            "movie", "电影",
            "book", "书籍", 
            "music", "音乐",
            "game", "游戏"
        );
        return typeMap.getOrDefault(type, type);
    }
    
    /**
     * 生成默认提示词
     */
    private String generateDefaultPrompt(SoulPortraitDTO portrait) {
        return String.format(
            "你是一个%s。%s\n\n" +
            "基于你的灵魂画像，请生成个性化的内容，包括：\n" +
            "1. 自我描述\n" +
            "2. 思维模式分析\n" +
            "3. 内容推荐\n" +
            "4. 成长建议\n\n" +
            "请用中文回复，保持文艺而深刻的风格。",
            portrait.getSoulType(),
            portrait.getDescription()
        );
    }
    
    /**
     * 从原型模板生成AI提示词
     */
    public String generateFromTemplate(SoulPrototype prototype, SoulPortraitDTO portrait) {
        try {
            if (prototype.getAiPromptTemplate() != null && !prototype.getAiPromptTemplate().isEmpty()) {
                // 解析特质
                List<TraitDTO> traits = portrait.getTraits();
                String traitsText = traits.stream()
                    .map(TraitDTO::getName)
                    .collect(Collectors.joining("、"));
                
                // 解析维度权重
                Map<String, Integer> dimensionWeights = objectMapper.readValue(
                    prototype.getDimensionWeights(), 
                    new TypeReference<Map<String, Integer>>() {});
                
                // 获取主要维度
                List<String> topDimensions = getTopDimensions(dimensionWeights, 2);
                String primaryDimension = topDimensions.size() > 0 ? topDimensions.get(0) : "思考";
                String secondaryDimension = topDimensions.size() > 1 ? topDimensions.get(1) : "感知";
                
                // 替换模板变量
                String prompt = prototype.getAiPromptTemplate()
                    .replace("{traits}", traitsText)
                    .replace("{primary_dimension}", primaryDimension)
                    .replace("{secondary_dimension}", secondaryDimension);
                
                return prompt;
            }
        } catch (Exception e) {
            log.error("从模板生成AI提示词失败", e);
        }
        
        // 回退到通用生成
        return generateDefaultPrompt(portrait);
    }
}