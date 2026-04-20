package com.soulcurator.backend.controller.portrait;

import com.soulcurator.backend.dto.portrait.GeneratePortraitRequest;
import com.soulcurator.backend.dto.portrait.SoulPortraitDTO;
import com.soulcurator.backend.service.portrait.RuleBasedSoulPortraitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 灵魂画像控制器
 * 处理灵魂画像的生成请求
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/portrait")
@RequiredArgsConstructor
public class SoulPortraitController {

  private final RuleBasedSoulPortraitService portraitService;

  /**
   * 生成灵魂画像
   * POST /api/v1/portrait/generate
   * 
   * @param request 生成请求，包含sessionId
   * @return 生成的灵魂画像
   */
  @PostMapping("/generate")
  public ResponseEntity<SoulPortraitDTO> generatePortrait(
      @Valid @RequestBody GeneratePortraitRequest request) {

    log.info("收到灵魂画像生成请求，sessionId: {}, userId: {}",
        request.getSessionId(), request.getUserId());

    try {
      SoulPortraitDTO portrait = portraitService.generatePortrait(request.getSessionId());
      log.info("灵魂画像生成成功，类型: {}", portrait.getSoulType());

      return ResponseEntity.ok(portrait);

    } catch (IllegalArgumentException e) {
      log.warn("生成灵魂画像失败: {}", e.getMessage());
      return ResponseEntity.badRequest().body(null);
    } catch (Exception e) {
      log.error("生成灵魂画像时发生错误", e);
      return ResponseEntity.internalServerError().body(null);
    }
  }

  /**
   * 测试接口：获取示例灵魂画像
   * GET /api/v1/portrait/example
   * 
   * @param type 示例类型（可选）：thinker/feeler/adventurer
   * @return 示例灵魂画像
   */
  @GetMapping("/example")
  public ResponseEntity<SoulPortraitDTO> getExamplePortrait(
      @RequestParam(required = false, defaultValue = "thinker") String type) {

    log.info("获取示例灵魂画像，类型: {}", type);

    // 创建示例数据（用于前端开发和测试）
    SoulPortraitDTO example = createExamplePortrait(type);

    return ResponseEntity.ok(example);
  }

  /**
   * 创建示例灵魂画像
   */
  private SoulPortraitDTO createExamplePortrait(String type) {
    // 这里创建硬编码的示例数据，用于前端测试
    switch (type.toLowerCase()) {
      case "feeler":
        return SoulPortraitDTO.builder()
            .soulType("感性体验者")
            .description("你的心灵在绚烂的色彩中舞蹈，于暮色苍茫的旋律里感受爱的温度与颤动。")
            .traits(java.util.Arrays.asList(
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("情感丰沛").icon("heart").color("#ef4444").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("艺术感知").icon("music").color("#ef4444").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("直觉敏锐").icon("eye").color("#ef4444").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("浪漫主义").icon("star").color("#ef4444").build()))
            .quote(com.soulcurator.backend.dto.portrait.QuoteDTO.builder()
                .text("心有猛虎，细嗅蔷薇。")
                .author("西格里夫·萨松")
                .build())
            .recommendations(java.util.Arrays.asList(
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("movie")
                    .title("《海上钢琴师》")
                    .description("在有限的琴键上，奏出无限的音乐。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1511379938547-c1f69419868d?auto=format&fit=crop&q=80&w=600")
                    .build(),
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("book")
                    .title("《小王子》")
                    .description("所有的大人都曾经是小孩，虽然只有少数人记得。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&q=80&w=600")
                    .build(),
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("music")
                    .title("Clair de Lune")
                    .description("德彪西的月光，如水般流淌。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&q=80&w=600")
                    .build()))
            .metadata(com.soulcurator.backend.dto.portrait.MetadataDTO.builder()
                .generatedAt("2026-04-20T18:30:00+08:00")
                .soulId("0xFE...88A")
                .node("心灵节点")
                .build())
            .build();

      case "adventurer":
        return SoulPortraitDTO.builder()
            .soulType("冒险探索者")
            .description("你的精神在浩瀚的山巅翱翔，于黄昏时刻的未知中追寻存在的边界与可能。")
            .traits(java.util.Arrays.asList(
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("无畏冒险").icon("mountain").color("#10b981").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("好奇求知").icon("globe").color("#10b981").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("开拓精神").icon("rocket").color("#10b981").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("自由灵魂").icon("wind").color("#10b981").build()))
            .quote(com.soulcurator.backend.dto.portrait.QuoteDTO.builder()
                .text("路漫漫其修远兮，吾将上下而求索。")
                .author("屈原")
                .build())
            .recommendations(java.util.Arrays.asList(
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("movie")
                    .title("《荒野生存》")
                    .description("有些人觉得他们不需要任何东西，有些人觉得他们需要全世界。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?auto=format&fit=crop&q=80&w=600")
                    .build(),
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("book")
                    .title("《禅与摩托车维修艺术》")
                    .description("我们常常太忙而没有时间好好聊聊。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&q=80&w=600")
                    .build(),
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("music")
                    .title("《勇敢的心》原声")
                    .description("自由与勇气的赞歌。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1518609878373-06d740f60d8b?auto=format&fit=crop&q=80&w=600")
                    .build()))
            .metadata(com.soulcurator.backend.dto.portrait.MetadataDTO.builder()
                .generatedAt("2026-04-20T18:35:00+08:00")
                .soulId("0xAD...VENT")
                .node("探索节点")
                .build())
            .build();

      default: // thinker
        return SoulPortraitDTO.builder()
            .soulType("深度思考者")
            .description("你的灵魂在静谧的星空下共鸣，于万籁俱寂之时寻找宇宙的逻辑与诗意。")
            .traits(java.util.Arrays.asList(
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("深度内省").icon("moon").color("#8b5cf6").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("理性极客").icon("cpu").color("#8b5cf6").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("孤独探索者").icon("compass").color("#8b5cf6").build(),
                com.soulcurator.backend.dto.portrait.TraitDTO.builder()
                    .name("隐秘艺术家").icon("palette").color("#8b5cf6").build()))
            .quote(com.soulcurator.backend.dto.portrait.QuoteDTO.builder()
                .text("不要温顺地走进那个良夜，激情不能被消沉的暮色所屈服。")
                .author("狄兰·托马斯")
                .build())
            .recommendations(java.util.Arrays.asList(
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("movie")
                    .title("《星际穿越》")
                    .description("唯有爱与时间能超越维度。你的灵魂与这部史诗在时空奇点交汇。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=600")
                    .build(),
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("music")
                    .title("Moonlight Sonata")
                    .description("贝多芬的月光奏鸣曲，在这寂静时刻，是脑海里最完美的背景乐。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1516280440614-37939bbacd81?auto=format&fit=crop&q=80&w=600")
                    .build(),
                com.soulcurator.backend.dto.portrait.RecommendationDTO.builder()
                    .type("book")
                    .title("《尤利西斯》")
                    .description("意识流的巅峰之作，正如你跳跃而深刻的思维，难以被定义。")
                    .imageUrl(
                        "https://images.unsplash.com/photo-1532012197267-da84d127e765?auto=format&fit=crop&q=80&w=600")
                    .build()))
            .metadata(com.soulcurator.backend.dto.portrait.MetadataDTO.builder()
                .generatedAt("2026-04-20T18:20:00+08:00")
                .soulId("0x7E...42C")
                .node("深空节点")
                .build())
            .build();
    }
  }
}