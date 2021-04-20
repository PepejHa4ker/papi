package com.pepej.papi.utils;

import com.pepej.papi.Papi;
import com.pepej.papi.function.Predicates;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public final class TabHandlers {

    private TabHandlers() {
        throw new UnsupportedOperationException("This class cannot be initialized");
    }


    public static List<String> of(String arg, String... values) {
        return finalize(Arrays.stream(values), arg);

    }

    public static List<String> worlds(String arg) {
        return finalize(Papi.server().getWorlds()
                            .stream()
                            .map(World::getName), arg);

    }

    public static List<String> players(String arg) {
        return finalize(Players.stream().map(Player::getName), arg);
    }

    public static List<String> onOff(String arg) {
        return finalize(Arrays.asList("on", "off"), arg);
    }

    public static List<String> enableDisable(String arg) {
        return finalize(Arrays.asList("on", "off"), arg);
    }

    public static List<String> addRemove(String arg) {
        return finalize(Arrays.asList("add", "remove"), arg);
    }

    public static List<String> gamemodes(String arg) {
        return finalize(Arrays.stream(GameMode.values())
                              .map(GameMode::name), arg);
    }

    private static List<String> finalize(Collection<String> args, String arg) {
        return finalize(args.stream(), arg);
    }

    private static List<String> finalize(Stream<String> stream, String arg) {
        return stream.map(String::toLowerCase)
                     .filter(Predicates.startWith(arg.toLowerCase(Locale.ROOT)))
                     .collect(toList());
    }
}
