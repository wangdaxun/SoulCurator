package com.soulcurator.backend.model.enums;

/**
 * 主题类型枚举
 * 用于标记作品的核心主题
 */
public enum ThemeType {
    // 人性主题
    HUMAN_NATURE("人性"),
    GOOD_VS_EVIL("善与恶"),
    FREE_WILL("自由意志"),
    IDENTITY("身份认同"),
    MORALITY("道德"),
    
    // 社会主题
    SOCIETY("社会"),
    POWER("权力"),
    JUSTICE("正义"),
    REBELLION("反抗"),
    UTOPIA_DYSTOPIA("乌托邦/反乌托邦"),
    
    // 存在主题
    EXISTENCE("存在"),
    MEANING_OF_LIFE("生命意义"),
    DEATH("死亡"),
    TIME("时间"),
    MEMORY("记忆"),
    
    // 科技主题
    TECHNOLOGY("科技"),
    AI("人工智能"),
    VIRTUAL_REALITY("虚拟现实"),
    TRANSHUMANISM("超人类主义"),
    
    // 关系主题
    LOVE_RELATIONSHIPS("爱情关系"),
    FAMILY("家庭"),
    FRIENDSHIP("友谊"),
    LONELINESS("孤独"),
    
    // 成长主题
    COMING_OF_AGE("成长"),
    SELF_DISCOVERY("自我发现"),
    REDEMPTION("救赎"),
    TRANSFORMATION("转变"),
    
    // 文艺特定
    ART_CREATION("艺术创作"),
    BEAUTY("美"),
    TRUTH("真理"),
    ILLUSION("幻觉");

    private final String description;

    ThemeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}