package com.pepej.papi.utils;

import com.pepej.papi.Papi;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;

import static com.pepej.papi.text.Text.colorize;


/**
 * Utility for quickly accessing a logger instance without using {@link Bukkit#getLogger()}
 */
public final class Log {

    public static void info(@NonNull final String s, final Object... params) {
        Papi.console().sendMessage(String.format(colorize("&3[Papi] &a" + s), params));
    }

    public static void warn(@NonNull final String s, final Object... params) {
        Papi.console().sendMessage( String.format(colorize("&3[Papi] &6" + s), params));
    }

    public static void severe(@NonNull final String s, final Object... params) {
        Papi.console().sendMessage(String.format(colorize("&3[Papi] &c" + s), params));
    }

    private Log() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
