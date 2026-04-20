package com.soulcurator.backend.service.portrait;

import com.soulcurator.backend.dto.portrait.*;
import com.soulcurator.backend.model.selection.UserSelection;
import com.soulcurator.backend.repository.selection.UserSelectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 规则引擎灵魂画像生成服务
 * 基于用户选择数据，通过规则匹配生成灵魂画像
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RuleBasedSoulPortraitService {

  private final UserSelectionRepository userSelectionRepository;

  // 预定义的灵魂原型（可以从数据库加载，这里先硬编码）
  private final List<SoulPrototype> prototypes = Arrays.asList(
      SoulPrototype.builder()
          .name("深度思考者")
          .descriptionTemplate("你的灵魂在%s的星空下共鸣，于%s之时寻找%s的逻辑与诗意。")
          .icon("brain")
          .colorHex("#8b5cf6")
          .dimensionWeights(Map.of(
              "introspection", 8,
              "logic", 6,
              "art", 4,
              "adventure", 2))
          .traits(Arrays.asList(
              TraitDTO.builder().name("深度内省").icon("moon").color("#8b5cf6").build(),
              TraitDTO.builder().name("理性极客").icon("cpu").color("#8b5cf6").build(),
              TraitDTO.builder().name("孤独探索者").icon("compass").color("#8b5cf6").build(),
              TraitDTO.builder().name("隐秘艺术家").icon("palette").color("#8b5cf6").build()))
          .quotes(Arrays.asList(
              QuoteDTO.builder().text("不要温顺地走进那个良夜，激情不能被消沉的暮色所屈服。").author("狄兰·托马斯").build(),
              QuoteDTO.builder().text("我思故我在。").author("笛卡尔").build(),
              QuoteDTO.builder().text("未经审视的人生不值得度过。").author("苏格拉底").build()))
          .build(),

      SoulPrototype.builder()
          .name("感性体验者")
          .descriptionTemplate("你的心灵在%s的色彩中舞蹈，于%s的旋律里感受%s的温度与颤动。")
          .icon("heart")
          .colorHex("#ef4444")
          .dimensionWeights(Map.of(
              "emotion", 8,
              "art", 7,
              "introspection", 5,
              "logic", 3))
          .traits(Arrays.asList(
              TraitDTO.builder().name("情感丰沛").icon("heart").color("#ef4444").build(),
              TraitDTO.builder().name("艺术感知").icon("music").color("#ef4444").build(),
              TraitDTO.builder().name("直觉敏锐").icon("eye").color("#ef4444").build(),
              TraitDTO.builder().name("浪漫主义").icon("star").color("#ef4444").build()))
          .quotes(Arrays.asList(
              QuoteDTO.builder().text("心有猛虎，细嗅蔷薇。").author("西格里夫·萨松").build(),
              QuoteDTO.builder().text("人生如逆旅，我亦是行人。").author("苏轼").build(),
              QuoteDTO.builder().text("爱是恒久忍耐，又有恩慈。").author("圣经").build()))
          .build(),

      SoulPrototype.builder()
          .name("冒险探索者")
          .descriptionTemplate("你的精神在%s的山巅翱翔，于%s的未知中追寻%s的边界与可能。")
          .icon("compass")
          .colorHex("#10b981")
          .dimensionWeights(Map.of(
              "adventure", 9,
              "curiosity", 7,
              "logic", 5,
              "introspection", 3))
          .traits(Arrays.asList(
              TraitDTO.builder().name("无畏冒险").icon("mountain").color("#10b981").build(),
              TraitDTO.builder().name("好奇求知").icon("globe").color("#10b981").build(),
              TraitDTO.builder().name("开拓精神").icon("rocket").color("#10b981").build(),
              TraitDTO.builder().name("自由灵魂").icon("wind").color("#10b981").build()))
          .quotes(Arrays.asList(
              QuoteDTO.builder().text("路漫漫其修远兮，吾将上下而求索。").author("屈原").build(),
              QuoteDTO.builder().text("不要走在我后面，我可能不会引路；不要走在我前面，我可能不会跟随；请走在我的身边，做我的朋友。").author("阿尔贝·加缪").build(),
              QuoteDTO.builder().text("生活就像一盒巧克力，你永远不知道下一颗是什么味道。").author("阿甘正传").build()))
          .build());

  // 描述变量池
  private final Map<String, List<String>> descriptionVariables = Map.of(
      "adjective", Arrays.asList("静谧的", "深邃的", "神秘的", "璀璨的", "朦胧的", "浩瀚的"),
      "time", Arrays.asList("万籁俱寂", "晨光熹微", "暮色苍茫", "午夜时分", "午后慵懒", "黄昏时刻"),
      "theme", Arrays.asList("宇宙", "生命", "时间", "存在", "意识", "真理", "美", "爱"));

  /**
   * 生成灵魂画像
   * 
   * @param sessionId 用户会话ID
   * @return 灵魂画像DTO
   */
  public SoulPortraitDTO generatePortrait(String sessionId) {
    log.info("开始生成灵魂画像，sessionId: {}", sessionId);

    // 1. 获取用户选择记录
    List<UserSelection> selections = userSelectionRepository.findBySessionId(sessionId);
    if (selections.isEmpty()) {
      throw new IllegalArgumentException("未找到用户选择记录，sessionId: " + sessionId);
    }

    // 2. 计算维度权重
    Map<String, Integer> dimensionWeights = calculateDimensionWeights(selections);
    log.debug("计算出的维度权重: {}", dimensionWeights);

    // 3. 匹配灵魂原型
    SoulPrototype matchedPrototype = matchPrototype(dimensionWeights);
    log.info("匹配到的灵魂原型: {}", matchedPrototype.getName());

    // 4. 生成画像
    SoulPortraitDTO portrait = buildPortrait(matchedPrototype, dimensionWeights);

    // 5. 添加推荐作品（这里先使用硬编码，后续从数据库获取）
    portrait.setRecommendations(generateRecommendations(matchedPrototype));

    // 6. 设置元数据
    portrait.setMetadata(buildMetadata(sessionId));

    log.info("灵魂画像生成完成，类型: {}", portrait.getSoulType());
    return portrait;
  }

  /**
   * 计算维度权重
   */
  private Map<String, Integer> calculateDimensionWeights(List<UserSelection> selections) {
    Map<String, Integer> weights = new HashMap<>();

    // 这里简化处理：假设每个选择都有dimension_scores字段
    // 实际应该从options表的dimension_scores字段读取
    for (UserSelection selection : selections) {
      // 简化：根据选项ID判断维度
      String optionId = selection.getOptionId();

      // 这里应该从数据库查询option的dimension_scores
      // 暂时用硬编码映射
      Map<String, Integer> optionScores = getOptionDimensionScores(optionId);

      optionScores.forEach((dimension, score) -> weights.merge(dimension, score, Integer::sum));
    }

    return weights;
  }

  /**
   * 获取选项的维度得分（简化版）
   */
  private Map<String, Integer> getOptionDimensionScores(String optionId) {
    // 这里应该从数据库查询，暂时硬编码
    Map<String, Map<String, Integer>> optionScores = Map.of(
        "deep-reflection", Map.of("introspection", 3, "logic", 2),
        "emotional-response", Map.of("emotion", 3, "art", 2),
        "adventure-choice", Map.of("adventure", 3, "curiosity", 2),
        "logical-analysis", Map.of("logic", 3, "introspection", 1),
        "artistic-expression", Map.of("art", 3, "emotion", 2));

    return optionScores.getOrDefault(optionId, Map.of("unknown", 1));
  }

  /**
   * 匹配灵魂原型
   */
  private SoulPrototype matchPrototype(Map<String, Integer> dimensionWeights) {
    SoulPrototype bestMatch = prototypes.get(0);
    double bestScore = -1;

    for (SoulPrototype prototype : prototypes) {
      double score = calculateSimilarity(prototype, dimensionWeights);
      if (score > bestScore) {
        bestScore = score;
        bestMatch = prototype;
      }
    }

    log.debug("原型匹配得分: {} -> {}", bestMatch.getName(), bestScore);
    return bestMatch;
  }

  /**
   * 计算相似度得分
   */
  private double calculateSimilarity(SoulPrototype prototype, Map<String, Integer> userWeights) {
    double totalScore = 0;
    int matchedDimensions = 0;

    for (Map.Entry<String, Integer> entry : prototype.getDimensionWeights().entrySet()) {
      String dimension = entry.getKey();
      int prototypeWeight = entry.getValue();
      int userWeight = userWeights.getOrDefault(dimension, 0);

      if (userWeight >= prototypeWeight * 0.7) { // 达到阈值的70%就算匹配
        totalScore += (double) userWeight / prototypeWeight;
        matchedDimensions++;
      }
    }

    // 如果没有匹配的维度，返回0
    if (matchedDimensions == 0) {
      return 0;
    }

    // 计算平均得分，并考虑匹配维度数量
    double avgScore = totalScore / matchedDimensions;
    double dimensionBonus = matchedDimensions / (double) prototype.getDimensionWeights().size();

    return avgScore * 0.7 + dimensionBonus * 0.3; // 加权计算
  }

  /**
   * 构建画像
   */
  private SoulPortraitDTO buildPortrait(SoulPrototype prototype, Map<String, Integer> dimensionWeights) {
    // 生成描述
    String description = generateDescription(prototype, dimensionWeights);

    // 随机选择一个引用
    QuoteDTO quote = prototype.getQuotes().get(
        new Random().nextInt(prototype.getQuotes().size()));

    return SoulPortraitDTO.builder()
        .soulType(prototype.getName())
        .description(description)
        .traits(prototype.getTraits())
        .quote(quote)
        .build();
  }

  /**
   * 生成描述
   */
  private String generateDescription(SoulPrototype prototype, Map<String, Integer> dimensionWeights) {
    String template = prototype.getDescriptionTemplate();

    // 根据维度权重选择变量
    String adjective = selectVariable("adjective", dimensionWeights);
    String time = selectVariable("time", dimensionWeights);
    String theme = selectVariable("theme", dimensionWeights);

    return String.format(template, adjective, time, theme);
  }

  /**
   * 选择变量
   */
  private String selectVariable(String variableType, Map<String, Integer> dimensionWeights) {
    List<String> variables = descriptionVariables.get(variableType);
    if (variables == null || variables.isEmpty()) {
      return "未知的";
    }

    // 根据维度权重选择变量（简化：随机选择）
    Random random = new Random();
    return variables.get(random.nextInt(variables.size()));
  }

  /**
   * 生成推荐作品（简化版）
   */
  private List<RecommendationDTO> generateRecommendations(SoulPrototype prototype) {
    // 这里应该从数据库查询，暂时硬编码
    Map<String, List<RecommendationDTO>> prototypeRecommendations = Map.of(
        "深度思考者", Arrays.asList(
            RecommendationDTO.builder()
                .type("movie")
                .title("《星际穿越》")
                .description("唯有爱与时间能超越维度。你的灵魂与这部史诗在时空奇点交汇。")
                .imageUrl(
                    "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=600")
                .build(),
            RecommendationDTO.builder()
                .type("music")
                .title("Moonlight Sonata")
                .description("贝多芬的月光奏鸣曲，在这寂静时刻，是脑海里最完美的背景乐。")
                .imageUrl(
                    "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&q=80&w=600")
                .build(),
            RecommendationDTO.builder()
                .type("book")
                .title("《尤利西斯》")
                .description("意识流的巅峰之作，正如你跳跃而深刻的思维，难以被定义。")
                .imageUrl(
                    "https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=600")
                .build()),
        "感性体验者", Arrays.asList(
            RecommendationDTO.builder()
                .type("movie")
                .title("《海上钢琴师》")
                .description("在有限的琴键上，奏出无限的音乐。你的灵魂与1900一样，选择留在自己的船上。")
                .imageUrl(
                    "https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&q=80&w=600")
                .build(),
            RecommendationDTO.builder()
                .type("book")
                .title("《小王子》")
                .description("所有的大人都曾经是小孩，虽然只有少数人记得。")
                .imageUrl("https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=600")
                .build(),
            RecommendationDTO.builder()
                .type("music")
                .title("Clair de Lune")
                .description("德彪西的月光，如水般流淌，映照你细腻的情感世界。")
                .imageUrl(
                    "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&q=80&w=600")
                .build()),
        "冒险探索者", Arrays.asList(
            RecommendationDTO.builder()
                .type("movie")
                .title("《荒野生存》")
                .description("有些人觉得他们不需要任何东西，有些人觉得他们需要全世界。")
                .imageUrl(
                    "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&q=80&w=600")
                .build(),
            RecommendationDTO.builder()
                .type("book")
                .title("《禅与摩托车维修艺术》")
                .description("我们常常太忙而没有时间好好聊聊，结果日复一日地过着无聊的生活。")
                .imageUrl("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&q=80&w=600")
                .build(),
            RecommendationDTO.builder()
                .type("music")
                .title("《勇敢的心》原声")
                .description("自由与勇气的赞歌，伴随你探索未知的旅程。")
                .imageUrl(
                    "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?auto=format&fit=crop&q=80&w=600")
                .build()));

    return prototypeRecommendations.getOrDefault(prototype.getName(),
        prototypeRecommendations.get("深度思考者"));
  }

  /**
   * 构建元数据
   */
  private MetadataDTO buildMetadata(String sessionId) {
    String timestamp = LocalDateTime.now()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "+08:00";

    // 生成简单的灵魂ID（基于sessionId和时间的哈希）
    String soulId = "0x" + Integer.toHexString(
        (sessionId + System.currentTimeMillis()).hashCode()).toUpperCase().substring(0, 8) + "...";

    return MetadataDTO.builder()
        .generatedAt(timestamp)
        .soulId(soulId)
        .node("深空节点")
        .build();
  }

  /**
   * 灵魂原型内部类
   */
  @lombok.Builder
  @lombok.Data
  private static class SoulPrototype {
    private String name;
    private String descriptionTemplate;
    private String icon;
    private String colorHex;
    private Map<String, Integer> dimensionWeights;
    private List<TraitDTO> traits;
    private List<QuoteDTO> quotes;
  }
}