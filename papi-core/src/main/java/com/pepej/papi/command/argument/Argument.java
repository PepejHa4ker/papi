package com.pepej.papi.command.argument;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.Commands;
import com.pepej.papi.command.CommandInterruptException;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Represents a command argument
 */
public interface Argument {

    /**
     * Gets the index of the argument
     *
     * @return the index
     */
    int index();

    /**
     * Gets the value of the argument
     *
     * @return the value
     */
    @Nonnull
    Optional<String> value();

    @Nonnull
    default <T> Optional<T> parse(@Nonnull ArgumentParser<T> parser) {
        return parser.parse(this);
    }

    @Nonnull
    default <T> T parseOrFail(@Nonnull ArgumentParser<T> parser) throws CommandInterruptException {
        return parser.parseOrFail(this);
    }

    @Nonnull
    default <T> Optional<T> parse(@Nonnull TypeToken<T> type) {
        return Commands.parserRegistry().find(type).flatMap(this::parse);
    }

    @Nonnull
    default <T> T parseOrFail(@Nonnull TypeToken<T> type) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(type).orElse(null);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + type);
        }
        return parseOrFail(parser);
    }

    @Nonnull
    default <T> Optional<T> parse(@Nonnull Class<T> clazz) {
        return Commands.parserRegistry().find(clazz).flatMap(this::parse);
    }

    @Nonnull
    default <T> T parseOrFail(@Nonnull Class<T> clazz) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(clazz).orElse(null);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + clazz);
        }
        return parseOrFail(parser);
    }

    /**
     * Gets if the argument is present
     *
     * @return true if present
     */
    boolean isPresent();

    /**
     * Asserts that the permission is present
     */
    default void assertPresent() throws CommandInterruptException {
        CommandInterruptException.makeAssertion(isPresent(), "Argument at index " + index() + " is not present.");
    }
}
