package com.pepej.papi;

import com.pepej.papi.command.argument.ArgumentParserRegistry;
import com.pepej.papi.command.argument.SimpleParserRegistry;
import com.pepej.papi.command.functional.FunctionalCommandBuilder;
import com.pepej.papi.function.Numbers;
import com.pepej.papi.time.DurationParser;
import com.pepej.papi.utils.Players;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * A functional command handling utility.
 */
public final class Commands {

    // Global argument parsers
    private static final ArgumentParserRegistry PARSER_REGISTRY;

    @Nonnull
    public static ArgumentParserRegistry parserRegistry() {
        return PARSER_REGISTRY;
    }

    static {
        PARSER_REGISTRY = new SimpleParserRegistry();

        // setup default argument parsers
        PARSER_REGISTRY.register(String.class, Optional::of);
        PARSER_REGISTRY.register(Number.class, Numbers::parse);
        PARSER_REGISTRY.register(Integer.class, Numbers::parseIntegerOpt);
        PARSER_REGISTRY.register(Long.class, Numbers::parseLongOpt);
        PARSER_REGISTRY.register(Float.class, Numbers::parseFloatOpt);
        PARSER_REGISTRY.register(Double.class, Numbers::parseDoubleOpt);
        PARSER_REGISTRY.register(Byte.class, Numbers::parseByteOpt);
        PARSER_REGISTRY.register(Boolean.class, s -> s.equalsIgnoreCase("true") ? Optional.of(true) : s.equalsIgnoreCase("false") ? Optional.of(false) : Optional.empty());
        PARSER_REGISTRY.register(UUID.class, s -> {
            try {
                return Optional.of(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
        PARSER_REGISTRY.register(Player.class, s -> {
            try {
                return Players.get(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return Players.get(s);
            }
        });
        PARSER_REGISTRY.register(OfflinePlayer.class, s -> {
            try {
                return Players.getOffline(UUID.fromString(s));
            } catch (IllegalArgumentException e) {
                return Players.getOffline(s);
            }
        });
        PARSER_REGISTRY.register(World.class, Papi::world);
        PARSER_REGISTRY.register(Duration.class, DurationParser::parseSafely);
    }

    /**
     * Creates and returns a new command builder
     *
     * @return a command builder
     */
    public static FunctionalCommandBuilder<CommandSender> create() {
        return FunctionalCommandBuilder.newBuilder();
    }

    private Commands() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
