package com.soulcurator.backend.service.portrait;

import com.soulcurator.backend.dto.portrait.*;
import com.soulcurator.backend.model.portrait.SoulPortrait;
import com.soulcurator.backend.model.prototype.SoulPrototype;
import com.soulcurator.backend.model.prototype.DescriptionVariable;
import com.soulcurator.backend.model.selection.UserSelection;
import com.soulcurator.backend.repository.portrait.SoulPortraitRepository;
import com.soulcurator.backend.repository.prototype.SoulPrototypeRepository;
import com.soulcurator.backend.repository.prototype.DescriptionVariableRepository;
import com.soulcurator.backend.repository.selection.UserSelectionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 规则引擎灵魂画像生成服务
 * 从数据库读取灵魂原型和描述变量
 * 新增功能：生成AI提示词并保存到数据库
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleBasedSoulPortraitService {

  private final UserSelectionRepository userSelectionRepository;
  private final SoulPrototypeRepository soulPrototypeRepository;
  private final DescriptionVariableRepository descriptionVariableRepository;
  private final SoulPortraitRepository soulPortraitRepository;
  private final AIPromptGenerator aiPromptGenerator;
  private final ObjectMapper objectMapper;

  /**
   * 生成灵魂画像（包含AI提示词）
   */
  public SoulPortraitDTO generatePortrait(String sessionId) {
    log.info("开始生成灵魂画像，sessionId: {}", sessionId);

    // 1. 获取用户选择记录
    List<UserSelection> selections = userSelectionRepository.findBySessionId(sessionId);
    if (selections.isEmpty()) {
      throw new IllegalArgumentException("未找到用户选择记录，sessionId: " + sessionId);
    }

    // 2. 从数据库加载原型
    List<SoulPrototype> prototypes = soulPrototypeRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    if (prototypes.isEmpty()) {
      throw new IllegalStateException("未找到可用的灵魂原型");
    }

    // 3. 简单匹配：选择第一个原型（实际应该根据用户选择计算）
    SoulPrototype matchedPrototype = prototypes.get(0);
    log.info("匹配到的灵魂原型: {}", matchedPrototype.getName());

    // 4. 构建画像DTO
    SoulPortraitDTO portraitDTO = buildPortraitDTO(matchedPrototype, selections);

    // 5. 生成AI提示词
    String aiPrompt = aiPromptGenerator.generateAIPrompt(portraitDTO, matchedPrototype, selections);
    log.info("生成AI提示词，长度: {} 字符", aiPrompt.length());

    // 6. 保存到数据库（包含AI提示词）
    try {
      savePortraitToDatabase(sessionId, portraitDTO, matchedPrototype, aiPrompt, selections);
      log.info("灵魂画像和AI提示词已保存到数据库");
    } catch (Exception e) {
      log.error("保存灵魂画像到数据库失败，但继续返回画像数据", e);
      // 不抛出异常，允许继续返回DTO
    }

    // 7. 设置元数据
    portraitDTO.setMetadata(buildMetadata(sessionId));

    log.info("灵魂画像生成完成，类型: {}", portraitDTO.getSoulType());
    return portraitDTO;
  }

  /**
   * 构建灵魂画像DTO
   */
  private SoulPortraitDTO buildPortraitDTO(SoulPrototype prototype, List<UserSelection> selections) {
    try {
      // 解析JSON字段
      Map<String, Integer> dimensionWeights = objectMapper.readValue(
          prototype.getDimensionWeights(),
          new TypeReference<Map<String, Integer>>() {
          });

      List<TraitDTO> traits = objectMapper.readValue(
          prototype.getTraits(),
          new TypeReference<List<TraitDTO>>() {
          });

      List<QuoteDTO> quotes = prototype.getQuotes() != null ? objectMapper.readValue(prototype.getQuotes(),
          new TypeReference<List<QuoteDTO>>() {
          }) : new ArrayList<>();

      // 生成描述
      String description = generateDescription(prototype.getDescriptionTemplate());

      // 选择引用
      QuoteDTO quote = null;
      if (!quotes.isEmpty()) {
        Random random = new Random();
        quote = quotes.get(random.nextInt(quotes.size()));
      }

      return SoulPortraitDTO.builder()
          .soulType(prototype.getName())
          .description(description)
          .traits(traits)
          .quote(quote)
          .recommendations(generateRecommendations(prototype))
          .build();

    } catch (Exception e) {
      log.error("构建灵魂画像失败", e);
      throw new RuntimeException("构建灵魂画像失败", e);
    }
  }

  /**
   * 保存灵魂画像到数据库
   */
  private void savePortraitToDatabase(String sessionId,
      SoulPortraitDTO portraitDTO,
      SoulPrototype prototype,
      String aiPrompt,
      List<UserSelection> selections) throws Exception {
    SoulPortrait portrait = new SoulPortrait();

    // 设置基本信息
    portrait.setSessionId(sessionId);
    portrait.setSoulType(portraitDTO.getSoulType());
    portrait.setDescription(portraitDTO.getDescription());

    // 设置特质和引用（JSON格式）
    if (portraitDTO.getTraits() != null) {
      portrait.setTraits(objectMapper.writeValueAsString(portraitDTO.getTraits()));
    }

    if (portraitDTO.getQuote() != null) {
      portrait.setQuote(objectMapper.writeValueAsString(portraitDTO.getQuote()));
    }

    // 设置维度得分（从原型获取）
    portrait.setDimensionScores(prototype.getDimensionWeights());
    
    // 设置其他必要字段的默认值
    portrait.setTotalQuestions(5);
    portrait.setCompletedQuestions(selections.size());
    portrait.setTotalTimeSeconds(300); // 假设5分钟
    
    // 设置top_dimensions（从原型权重计算）
    Map<String, Integer> dimensionWeights = objectMapper.readValue(
        prototype.getDimensionWeights(),
        new TypeReference<Map<String, Integer>>() {
        });
    
    // 获取前3个维度
    List<String> topDimensions = dimensionWeights.entrySet().stream()
        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
        .limit(3)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
    
    portrait.setTopDimensions(objectMapper.writeValueAsString(topDimensions));
    
    // 计算匹配度
    double matchScore = calculateMatchScore(dimensionWeights, selections);
    portrait.setMatchScore(matchScore);

    // 设置生成方法
    portrait.setGenerationMethod("rule_engine_with_ai");

    // 设置AI提示词
    portrait.setAiPrompt(aiPrompt);

    // 设置元数据
    Map<String, Object> metadata = new HashMap<>();
    metadata.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
    metadata.put("sessionId", sessionId);
    metadata.put("selectionsCount", selections.size());
    metadata.put("prototypeName", prototype.getName());
    portrait.setMetadata(objectMapper.writeValueAsString(metadata));

    // 保存到数据库
    soulPortraitRepository.save(portrait);

    log.info("灵魂画像保存成功，ID: {}, AI提示词已存储", portrait.getId());
  }

  /**
   * 计算匹配度分数（简化版）
   */
  private double calculateMatchScore(Map<String, Integer> prototypeWeights,
      List<UserSelection> selections) {
    // 简化计算：根据选择数量和质量评分
    int selectionCount = selections.size();
    double baseScore = Math.min(selectionCount * 10.0, 80.0); // 最多80分

    // 随机添加一些变化
    Random random = new Random();
    double variation = random.nextDouble() * 20.0;

    return Math.min(baseScore + variation, 100.0);
  }

  /**
   * 生成描述（简化版）
   */
  private String generateDescription(String template) {
    // 简单替换变量
    return template
        .replace("${adjective}", "深邃的")
        .replace("${time}", "万籁俱寂")
        .replace("${theme}", "宇宙");
  }

  /**
   * 生成推荐作品（简化版）
   */
  private List<RecommendationDTO> generateRecommendations(SoulPrototype prototype) {
    return Arrays.asList(
        RecommendationDTO.builder()
            .type("movie")
            .title("《星际穿越》")
            .description("唯有爱与时间能超越维度。")
            .imageUrl("https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=600")
            .build(),
        RecommendationDTO.builder()
            .type("music")
            .title("Moonlight Sonata")
            .description("贝多芬的月光奏鸣曲。")
            .imageUrl("https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&q=80&w=600")
            .build(),
        RecommendationDTO.builder()
            .type("book")
            .title("《尤利西斯》")
            .description("意识流的巅峰之作。")
            .imageUrl("https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=600")
            .build());
  }

  /**
   * 构建元数据
   */
  private MetadataDTO buildMetadata(String sessionId) {
    return MetadataDTO.builder()
        .generatedAt(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
        .soulId(generateSoulId(sessionId))
        .node("深空节点")
        .build();
  }

  /**
   * 生成灵魂ID
   */
  private String generateSoulId(String sessionId) {
    int hash = sessionId.hashCode();
    return String.format("0x%08X", hash & 0xFFFFFFFFL);
  }

  /**
   * 获取已保存的AI提示词
   */
  public Optional<String> getAIPrompt(String sessionId) {
    return soulPortraitRepository.findBySessionId(sessionId)
        .map(SoulPortrait::getAiPrompt);
  }

  /**
   * 获取完整的灵魂画像（包含数据库中的AI提示词）
   */
  public Optional<SoulPortraitDTO> getPortraitWithAIPrompt(String sessionId) {
    return soulPortraitRepository.findBySessionId(sessionId)
        .map(portrait -> {
          try {
            SoulPortraitDTO dto = buildPortraitDTOFromEntity(portrait);

            // 添加AI提示词到元数据
            MetadataDTO metadata = dto.getMetadata();
            if (metadata != null && portrait.getAiPrompt() != null) {
              // 可以在这里添加AI提示词信息
              log.debug("为画像添加AI提示词信息，长度: {}", portrait.getAiPrompt().length());
            }

            return dto;
          } catch (Exception e) {
            log.error("从实体构建DTO失败", e);
            return null;
          }
        });
  }

  /**
   * 从实体构建DTO
   */
  private SoulPortraitDTO buildPortraitDTOFromEntity(SoulPortrait portrait) throws Exception {
    List<TraitDTO> traits = portrait.getTraits() != null
        ? objectMapper.readValue(portrait.getTraits(), new TypeReference<List<TraitDTO>>() {
        })
        : new ArrayList<>();

    QuoteDTO quote = portrait.getQuote() != null ? objectMapper.readValue(portrait.getQuote(), QuoteDTO.class) : null;

    return SoulPortraitDTO.builder()
        .soulType(portrait.getSoulType())
        .description(portrait.getDescription())
        .traits(traits)
        .quote(quote)
        .recommendations(generateRecommendationsFromEntity(portrait))
        .metadata(MetadataDTO.builder()
            .generatedAt(portrait.getGeneratedAt().format(DateTimeFormatter.ISO_DATE_TIME))
            .soulId(generateSoulId(portrait.getSessionId()))
            .node("数据库节点")
            .build())
        .build();
  }

  /**
   * 从实体生成推荐作品
   */
  private List<RecommendationDTO> generateRecommendationsFromEntity(SoulPortrait portrait) {
    // 简化：返回固定推荐
    return generateRecommendations(null);
  }
}