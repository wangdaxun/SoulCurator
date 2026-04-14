package com.soulcurator.backend.config;

import com.soulcurator.backend.model.entity.TagEntity;
import com.soulcurator.backend.model.entity.WorkEntity;
import com.soulcurator.backend.model.enums.WorkType;
import com.soulcurator.backend.repository.TagRepository;
import com.soulcurator.backend.repository.WorkRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * 测试数据初始化
 * 仅在开发环境运行
 */
@Slf4j
@Component
@Profile("dev")
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private WorkRepository workRepository;
    
    @Autowired
    private SimpleTagRepository tagRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化测试数据...");
        
        // 初始化情感标签
        initializeEmotionTags();
        
        // 初始化主题标签
        initializeThemeTags();
        
        // 初始化示例作品
        initializeSampleWorks();
        
        log.info("测试数据初始化完成");
    }
    
    private void initializeEmotionTags() {
        if (tagRepository.count() > 0) {
            return;
        }
        
        String[][] emotions = {
            {"孤独", "LONELINESS", "#4A90E2"},
            {"希望", "HOPE", "#7ED321"},
            {"绝望", "DESPAIR", "#D0021B"},
            {"爱", "LOVE", "#FF6B6B"},
            {"怀旧", "NOSTALGIA", "#F5A623"},
            {"敬畏", "AWE", "#9013FE"},
            {"忧郁", "MELANCHOLY", "#8B572A"},
            {"狂喜", "EUPHORIA", "#FFD700"},
            {"救赎", "REDEMPTION", "#50E3C2"},
            {"存在焦虑", "EXISTENTIAL_DREAD", "#4A4A4A"}
        };
        
        for (String[] emotion : emotions) {
            TagEntity tag = new TagEntity();
            tag.setName(emotion[0]);
            tag.setCategory(TagEntity.TagCategory.EMOTION);
            tag.setDescription("情感标签: " + emotion[0]);
            tag.setEmotionType(emotion[1]);
            tag.setColorCode(emotion[2]);
            tag.setAiGenerated(false);
            tagRepository.save(tag);
        }
        
        log.info("初始化了 {} 个情感标签", emotions.length);
    }
    
    private void initializeThemeTags() {
        String[][] themes = {
            {"人性", "HUMAN_NATURE", "#FF6B6B"},
            {"时间", "TIME", "#4A90E2"},
            {"记忆", "MEMORY", "#7ED321"},
            {"存在", "EXISTENCE", "#4A4A4A"},
            {"科技", "TECHNOLOGY", "#9013FE"},
            {"人工智能", "AI", "#50E3C2"},
            {"成长", "COMING_OF_AGE", "#F5A623"},
            {"救赎", "REDEMPTION", "#8B572A"},
            {"社会", "SOCIETY", "#D0021B"},
            {"家庭", "FAMILY", "#FFD700"}
        };
        
        for (String[] theme : themes) {
            TagEntity tag = new TagEntity();
            tag.setName(theme[0]);
            tag.setCategory(TagEntity.TagCategory.THEME);
            tag.setDescription("主题标签: " + theme[0]);
            tag.setThemeType(theme[1]);
            tag.setColorCode(theme[2]);
            tag.setAiGenerated(false);
            tagRepository.save(tag);
        }
        
        log.info("初始化了 {} 个主题标签", themes.length);
    }
    
    private void initializeSampleWorks() {
        if (workRepository.count() > 0) {
            return;
        }
        
        // 获取一些标签用于关联
        TagEntity loneliness = tagRepository.findByName("孤独").orElse(null);
        TagEntity hope = tagRepository.findByName("希望").orElse(null);
        TagEntity time = tagRepository.findByName("时间").orElse(null);
        TagEntity existence = tagRepository.findByName("存在").orElse(null);
        TagEntity humanNature = tagRepository.findByName("人性").orElse(null);
        
        // 示例1: 《最后的生还者》
        WorkEntity tlou = new WorkEntity();
        tlou.setTitle("最后的生还者");
        tlou.setType(WorkType.GAME);
        tlou.setDescription("在末日后的美国，乔尔和艾莉必须穿越废墟，面对感染者和幸存者的威胁。");
        tlou.setCoverUrl("https://example.com/tlou-cover.jpg");
        tlou.setReleaseYear(2013);
        tlou.setExternalSource("STEAM");
        tlou.setExternalId("tlou_12345");
        tlou.setRatingScore(9.5);
        tlou.setRatingCount(150000);
        tlou.setDepthScore(8.7);
        
        Set<TagEntity> tlouEmotions = new HashSet<>();
        if (loneliness != null) tlouEmotions.add(loneliness);
        if (hope != null) tlouEmotions.add(hope);
        tlou.setEmotions(tlouEmotions);
        
        Set<TagEntity> tlouThemes = new HashSet<>();
        if (humanNature != null) tlouThemes.add(humanNature);
        tlou.setThemes(tlouThemes);
        
        workRepository.save(tlou);
        
        // 示例2: 《星际穿越》
        WorkEntity interstellar = new WorkEntity();
        interstellar.setTitle("星际穿越");
        interstellar.setType(WorkType.MOVIE);
        interstellar.setDescription("一群探险家利用新发现的虫洞，超越人类太空旅行的极限，在广袤的宇宙中进行星际航行。");
        interstellar.setCoverUrl("https://example.com/interstellar-cover.jpg");
        interstellar.setReleaseYear(2014);
        interstellar.setExternalSource("DOUBAN");
        interstellar.setExternalId("interstellar_67890");
        interstellar.setRatingScore(9.3);
        interstellar.setRatingCount(850000);
        interstellar.setDepthScore(9.1);
        
        Set<TagEntity> interstellarEmotions = new HashSet<>();
        if (loneliness != null) interstellarEmotions.add(loneliness);
        interstellar.setEmotions(interstellarEmotions);
        
        Set<TagEntity> interstellarThemes = new HashSet<>();
        if (time != null) interstellarThemes.add(time);
        if (existence != null) interstellarThemes.add(existence);
        interstellar.setThemes(interstellarThemes);
        
        workRepository.save(interstellar);
        
        // 示例3: 《三体》
        WorkEntity threeBody = new WorkEntity();
        threeBody.setTitle("三体");
        threeBody.setType(WorkType.BOOK);
        threeBody.setDescription("文化大革命时期，天文学家叶文洁向宇宙发出信号，改变了人类的命运。");
        threeBody.setCoverUrl("https://example.com/threebody-cover.jpg");
        threeBody.setReleaseYear(2008);
        threeBody.setExternalSource("DOUBAN");
        threeBody.setExternalId("threebody_54321");
        threeBody.setRatingScore(9.0);
        threeBody.setRatingCount(320000);
        threeBody.setDepthScore(9.5);
        
        Set<TagEntity> threeBodyThemes = new HashSet<>();
        if (existence != null) threeBodyThemes.add(existence);
        if (humanNature != null) threeBodyThemes.add(humanNature);
        threeBody.setThemes(threeBodyThemes);
        
        workRepository.save(threeBody);
        
        log.info("初始化了 3 个示例作品");
    }
}