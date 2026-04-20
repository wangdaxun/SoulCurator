package com.soulcurator.backend.config;

import com.soulcurator.backend.model.gateway.SoulGateway;
import com.soulcurator.backend.model.question.Question;
import com.soulcurator.backend.model.option.Option;
import com.soulcurator.backend.repository.gateway.SoulGatewayRepository;
import com.soulcurator.backend.repository.question.QuestionRepository;
import com.soulcurator.backend.repository.option.OptionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

/**
 * 测试数据初始化配置
 * 仅在开发环境运行
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class TestDataInitializer {
    
    private final SoulGatewayRepository soulGatewayRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ObjectMapper objectMapper;
    
    @Bean
    public CommandLineRunner initTestData() {
        return args -> {
            log.info("开始初始化测试数据...");
            
            // 1. 初始化灵魂之门入口
            initSoulGateways();
            
            // 2. 初始化问题
            initQuestions();
            
            // 3. 初始化选项
            initOptions();
            
            log.info("测试数据初始化完成");
        };
    }
    
    private void initSoulGateways() {
        if (soulGatewayRepository.count() > 0) {
            log.info("灵魂之门入口数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化灵魂之门入口数据...");
        
        // 电影之门
        SoulGateway movieGateway = new SoulGateway();
        movieGateway.setId("movie_gateway");
        movieGateway.setName("电影之门");
        movieGateway.setDescription("在光影编织的梦境里\n寻找另一个自己");
        movieGateway.setColorHex("#8B5CF6");
        movieGateway.setIcon("clapperboard");
        movieGateway.setGatewayType("movie");
        movieGateway.setCategory("entertainment");
        movieGateway.setDisplayOrder(1);
        movieGateway.setIsActive(true);
        movieGateway.setPopularityScore(0);
        
        // 文学之门
        SoulGateway literatureGateway = new SoulGateway();
        literatureGateway.setId("literature_gateway");
        literatureGateway.setName("文学之门");
        literatureGateway.setDescription("于纸墨留存的呼吸间\n触碰永恒的真理");
        literatureGateway.setColorHex("#3B82F6");
        literatureGateway.setIcon("book-open");
        literatureGateway.setGatewayType("literature");
        literatureGateway.setCategory("knowledge");
        literatureGateway.setDisplayOrder(2);
        literatureGateway.setIsActive(true);
        literatureGateway.setPopularityScore(0);
        
        // 音乐之门
        SoulGateway musicGateway = new SoulGateway();
        musicGateway.setId("music_gateway");
        musicGateway.setName("音乐之门");
        musicGateway.setDescription("由旋律激起的共振中\n释放压抑的灵魂");
        musicGateway.setColorHex("#F59E0B");
        musicGateway.setIcon("music");
        musicGateway.setGatewayType("music");
        musicGateway.setCategory("art");
        musicGateway.setDisplayOrder(3);
        musicGateway.setIsActive(true);
        musicGateway.setPopularityScore(0);
        
        // 游戏之门
        SoulGateway gameGateway = new SoulGateway();
        gameGateway.setId("game_gateway");
        gameGateway.setName("游戏之门");
        gameGateway.setDescription("在维度交错的交互里\n重塑世界的法则");
        gameGateway.setColorHex("#10B981");
        gameGateway.setIcon("gamepad-2");
        gameGateway.setGatewayType("game");
        gameGateway.setCategory("entertainment");
        gameGateway.setDisplayOrder(4);
        gameGateway.setIsActive(true);
        gameGateway.setPopularityScore(0);
        
        soulGatewayRepository.saveAll(Arrays.asList(movieGateway, literatureGateway, musicGateway, gameGateway));
        log.info("初始化4个灵魂之门入口完成");
    }
    
    private void initQuestions() {
        if (questionRepository.count() > 0) {
            log.info("问题数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化问题数据...");
        
        // 为每个入口类型创建5个问题
        String[] gatewayTypes = {"movie", "literature", "music", "game"};
        
        for (int g = 0; g < gatewayTypes.length; g++) {
            String gatewayType = gatewayTypes[g];
            
            for (int i = 1; i <= 5; i++) {
                Question question = new Question();
                question.setStepNumber(i);
                question.setTitle(getQuestionTitle(gatewayType, i));
                question.setSubtitle(getQuestionSubtitle(gatewayType, i));
                question.setDescription(getQuestionDescription(gatewayType, i));
                question.setGatewayType(gatewayType);
                question.setIsActive(true);
                question.setDisplayOrder(i);
                
                try {
                    question.setDimensionMapping(objectMapper.writeValueAsString(
                            getDimensionMapping(gatewayType, i)
                    ));
                } catch (Exception e) {
                    log.error("设置问题维度映射失败", e);
                }
                
                questionRepository.save(question);
            }
            
            log.info("为{}入口初始化5个问题完成", gatewayType);
        }
        
        log.info("总共初始化{}个问题完成", gatewayTypes.length * 5);
    }
    
    private void initOptions() {
        if (optionRepository.count() > 0) {
            log.info("选项数据已存在，跳过初始化");
            return;
        }
        
        log.info("初始化选项数据...");
        
        // 获取所有问题
        List<Question> questions = questionRepository.findAll();
        
        for (Question question : questions) {
            // 为每个问题创建4个选项
            for (int i = 1; i <= 4; i++) {
                Option option = new Option();
                option.setId(question.getGatewayType() + "_q" + question.getStepNumber() + "_opt" + i);
                option.setQuestionId(question.getId());
                option.setTitle(getOptionTitle(question.getGatewayType(), question.getStepNumber(), i));
                option.setEmoji(getOptionEmoji(i));
                option.setDescription(getOptionDescription(question.getGatewayType(), question.getStepNumber(), i));
                option.setDisplayOrder(i);
                
                try {
                    // 设置workReferences (String[])
                    List<String> referencesList = getOptionReferences(question.getGatewayType(), i);
                    option.setWorkReferences(referencesList.toArray(new String[0]));
                    
                    // 设置dimensionScores
                    option.setDimensionScores(objectMapper.writeValueAsString(
                            getDimensionScores(question.getGatewayType(), i)
                    ));
                    
                } catch (Exception e) {
                    log.error("设置选项JSON字段失败", e);
                }
                
                optionRepository.save(option);
            }
        }
        
        log.info("初始化选项数据完成，共{}个选项", questions.size() * 4);
    }
    
    // 辅助方法：生成问题标题
    private String getQuestionTitle(String gatewayType, int step) {
        String[][] titles = {
            {"你希望在电影中找到什么样的情感共鸣？", "什么样的电影叙事风格最吸引你？", "你更看重电影的哪个方面？", "你通常在什么场景下看电影？", "你如何看待电影中的艺术表达？"},
            {"你希望通过阅读获得什么？", "什么样的文学风格最吸引你？", "你更看重文学作品的哪个方面？", "你通常在什么场景下阅读？", "你如何看待文学中的思想深度？"},
            {"你希望通过音乐获得什么情感体验？", "什么样的音乐风格最吸引你？", "你更看重音乐的哪个方面？", "你通常在什么场景下听音乐？", "你如何看待音乐中的艺术表达？"},
            {"你希望通过游戏获得什么体验？", "什么样的游戏类型最吸引你？", "你更看重游戏的哪个方面？", "你通常在什么场景下玩游戏？", "你如何看待游戏中的交互设计？"}
        };
        
        int typeIndex = getGatewayTypeIndex(gatewayType);
        return titles[typeIndex][step - 1];
    }
    
    // 辅助方法：生成问题副标题
    private String getQuestionSubtitle(String gatewayType, int step) {
        return "这决定了你灵魂探索的方向";
    }
    
    // 辅助方法：生成问题描述
    private String getQuestionDescription(String gatewayType, int step) {
        return "请根据你的真实感受和偏好进行选择";
    }
    
    // 辅助方法：生成选项标题
    private String getOptionTitle(String gatewayType, int questionStep, int optionIndex) {
        String[][][] optionTitles = {
            { // movie
                {"深刻的人生反思", "紧张的智力挑战", "温暖的心灵治愈", "黑暗的人性探索"},
                {"线性叙事", "非线性叙事", "多线叙事", "实验性叙事"},
                {"视觉效果", "剧情深度", "演员表演", "音乐配乐"},
                {"独自一人", "与朋友一起", "在电影院", "随时随地"},
                {"艺术至上", "娱乐为主", "思想深度", "情感共鸣"}
            },
            { // literature
                {"知识获取", "情感共鸣", "思想启发", "娱乐消遣"},
                {"现实主义", "浪漫主义", "现代主义", "后现代主义"},
                {"语言美感", "思想深度", "情节设计", "人物塑造"},
                {"安静书房", "咖啡厅", "公共交通", "睡前阅读"},
                {"哲学思考", "社会批判", "人性探索", "美学追求"}
            },
            { // music
                {"情感宣泄", "精神放松", "灵感激发", "社交互动"},
                {"古典音乐", "流行音乐", "摇滚音乐", "电子音乐"},
                {"旋律优美", "歌词深刻", "节奏感强", "编曲复杂"},
                {"工作学习", "运动健身", "休闲放松", "社交聚会"},
                {"艺术表达", "情感共鸣", "文化传承", "娱乐功能"}
            },
            { // game
                {"挑战成就感", "故事体验", "社交互动", "创意表达"},
                {"角色扮演", "策略游戏", "动作游戏", "模拟游戏"},
                {"游戏性", "画面表现", "剧情深度", "社交功能"},
                {"独自游玩", "在线合作", "竞技对战", "家庭聚会"},
                {"艺术设计", "技术创新", "叙事深度", "社交价值"}
            }
        };
        
        int typeIndex = getGatewayTypeIndex(gatewayType);
        return optionTitles[typeIndex][questionStep - 1][optionIndex - 1];
    }
    
    // 辅助方法：生成选项表情
    private String getOptionEmoji(int optionIndex) {
        String[] emojis = {"🤔", "💡", "❤️", "🎭"};
        return emojis[optionIndex - 1];
    }
    
    // 辅助方法：生成选项描述
    private String getOptionDescription(String gatewayType, int questionStep, int optionIndex) {
        return "这是一个示例选项描述，实际项目中应该从数据库或配置中获取";
    }
    
    // 辅助方法：生成选项参考作品
    private List<String> getOptionReferences(String gatewayType, int optionIndex) {
        String[][] references = {
            {"《星际穿越》", "《活着》"},
            {"《盗梦空间》", "《记忆碎片》"},
            {"《心灵捕手》", "《当幸福来敲门》"},
            {"《小丑》", "《教父》"}
        };
        return Arrays.asList(references[optionIndex - 1]);
    }
    
    // 辅助方法：生成维度映射
    private Object getDimensionMapping(String gatewayType, int step) {
        return new Object() {
            public final String primary = "emotional";
            public final double intensity = 0.8;
        };
    }
    
    // 辅助方法：生成维度得分
    private Object getDimensionScores(String gatewayType, int optionIndex) {
        String[] dimensions = {"philosophical", "emotional", "intellectual", "aesthetic"};
        return new Object() {
            public final double philosophical = optionIndex == 1 ? 0.8 : 0.2;
            public final double emotional = optionIndex == 2 ? 0.8 : 0.2;
            public final double intellectual = optionIndex == 3 ? 0.8 : 0.2;
            public final double aesthetic = optionIndex == 4 ? 0.8 : 0.2;
        };
    }
    
    // 辅助方法：获取入口类型索引
    private int getGatewayTypeIndex(String gatewayType) {
        switch (gatewayType) {
            case "movie": return 0;
            case "literature": return 1;
            case "music": return 2;
            case "game": return 3;
            default: return 0;
        }
    }
}