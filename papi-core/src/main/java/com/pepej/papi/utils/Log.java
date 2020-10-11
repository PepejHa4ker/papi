package com.pepej.papi.utils;

import com.pepej.papi.internal.LoaderUtils;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;

/**
 * Utility for quickly accessing a logger instance without using {@link Bukkit#getLogger()}
 */
public final class Log {

    public static void info(@Nonnull String s) {
        LoaderUtils.getPlugin().getLogger().info(s);
    }

    public static void warn(@Nonnull String s) {
        LoaderUtils.getPlugin().getLogger().warning(s);
    }

    public static void severe(@Nonnull String s) {
        LoaderUtils.getPlugin().getLogger().severe(s);
    }

    private Log() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
