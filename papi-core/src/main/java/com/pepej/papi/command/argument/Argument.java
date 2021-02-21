package com.pepej.papi.command.argument;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.command.Commands;
import com.pepej.papi.command.CommandInterruptException;
import org.checkerframework.checker.nullness.qual.NonNull;

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
    @NonNull
    Optional<String> value();

    @NonNull
    default <T> Optional<T> parse(@NonNull ArgumentParser<T> parser) {
        return parser.parse(this);
    }

    @NonNull
    default <T> T parseOrFail(@NonNull ArgumentParser<T> parser) throws CommandInterruptException {
        return parser.parseOrFail(this);
    }

    @NonNull
    default <T> Optional<T> parse(@NonNull TypeToken<T> type) {
        return Commands.parserRegistry().find(type).flatMap(this::parse);
    }

    @NonNull
    default <T> T parseOrFail(@NonNull TypeToken<T> type) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(type).orElse(null);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + type);
        }
        return parseOrFail(parser);
    }

    @NonNull
    default <T> Optional<T> parse(@NonNull Class<T> clazz) {
        return Commands.parserRegistry().find(clazz).flatMap(this::parse);
    }

    @NonNull
    default <T> T parseOrFail(@NonNull Class<T> clazz) throws CommandInterruptException {
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
     * @throws CommandInterruptException if command was interrupted
     */
    default void assertPresent() throws CommandInterruptException {
        CommandInterruptException.makeAssertion(isPresent(), "Argument at index " + index() + " is not present.");
    }
}
