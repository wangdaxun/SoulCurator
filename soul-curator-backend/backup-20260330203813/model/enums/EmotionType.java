package com.soulcurator.backend.model.enums;

/**
 * 情感类型枚举
 * 用于标记作品中的核心情感
 */
public enum EmotionType {
    // 基础情感
    JOY("喜悦"),
    SADNESS("悲伤"),
    ANGER("愤怒"),
    FEAR("恐惧"),
    SURPRISE("惊讶"),
    DISGUST("厌恶"),
    
    // 复杂情感
    NOSTALGIA("怀旧"),
    MELANCHOLY("忧郁"),
    EUPHORIA("狂喜"),
    DESPAIR("绝望"),
    HOPE("希望"),
    LONELINESS("孤独"),
    
    // 社会情感
    LOVE("爱"),
    FRIENDSHIP("友情"),
    BETRAYAL("背叛"),
    SACRIFICE("牺牲"),
    REDEMPTION("救赎"),
    REVENGE("复仇"),
    
    // 存在主义情感
    EXISTENTIAL_DREAD("存在焦虑"),
    MEANINGLESSNESS("虚无"),
    AWE("敬畏"),
    TRANSCENDENCE("超越");

    private final String description;

    EmotionType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}