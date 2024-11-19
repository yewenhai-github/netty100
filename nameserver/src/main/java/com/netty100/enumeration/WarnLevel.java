package com.netty100.enumeration;

/**
 * @Description 预警等级
 * @Author lxk
 * @Version 1.0.0
 * @Since 1.0
 * @Date 2022/4/13
 */
public enum WarnLevel {
    MOST_URGENT(1, "非常紧急"),
    USUAL_URGENT(2, "紧急"),
    SERIOUS(3, "严重"),
    COMMON(4, "一般");

    private int level;
    private String title;

    WarnLevel(int level, String title) {
        this.level = level;
        this.title = title;
    }

    public int getLevel() {
        return level;
    }

    public String getTitle() {
        return title;
    }
}
