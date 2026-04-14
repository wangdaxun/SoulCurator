package com.soulcurator.backend.model.enums;

/**
 * 作品类型枚举
 */
public enum WorkType {
    GAME("游戏"),
    MOVIE("电影"),
    BOOK("书籍"),
    MUSIC("音乐"),
    TV_SERIES("电视剧"),
    ANIME("动漫"),
    COMIC("漫画"),
    OTHER("其他");

    private final String description;

    WorkType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}