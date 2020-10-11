package com.pepej.papi.bossbar;

/**
 * Represents the color of a {@link BossBar}.
 */
public enum BossBarColor {

    PINK,
    BLUE,
    RED,
    GREEN,
    YELLOW,
    PURPLE,
    WHITE;

    public static BossBarColor defaultColor() {
        return PURPLE;
    }

}
