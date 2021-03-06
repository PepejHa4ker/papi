package com.pepej.papi.command.argument;

import com.pepej.papi.command.CommandInterruptException;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.function.Function;

/**
 * Parses an argument from a String
 *
 * @param <T> the value type
 */
public interface ArgumentParser<T> {

    static <T> ArgumentParser<T> of(Function<String, Optional<T>> parseFunction) {
        return parseFunction::apply;
    }

    static <T> ArgumentParser<T> of(Function<String, Optional<T>> parseFunction, Function<String, CommandInterruptException> generateExceptionFunction) {
        return new ArgumentParser<T>() {
            @Override
            public Optional<T> parse(@NonNull String t) {
                return parseFunction.apply(t);
            }

            @Override
            public CommandInterruptException generateException(@NonNull String s) {
                return generateExceptionFunction.apply(s);
            }
        };
    }

    /**
     * Parses the value from a string
     *
     * @param s the string
     * @return the value, if parsing was successful
     */
    Optional<T> parse(@NonNull String s);

    /**
     * Generates a {@link CommandInterruptException} for when parsing fails with the given content.
     *
     * @param s the string input
     * @return the exception
     */
    default CommandInterruptException generateException(@NonNull String s) {
        return new CommandInterruptException("Unable to parse argument: " + s);
    }

    /**
     * Generates a {@link CommandInterruptException} for when parsing fails due to the lack of an argument.
     *
     * @param missingArgumentIndex the missing argument index
     * @return the exception
     */
    default CommandInterruptException generateException(int missingArgumentIndex) {
        return new CommandInterruptException("Argument at index " + missingArgumentIndex + " is missing.");
    }

    /**
     * Parses the value from a string, throwing an interrupt exception if
     * parsing failed.
     *
     * @param s the string
     * @throws CommandInterruptException if command was interrupted
     * @return the value
     */
    @NonNull
    default T parseOrFail(@NonNull String s) throws CommandInterruptException {
        Optional<T> ret = parse(s);
        if (!ret.isPresent()) {
            throw generateException(s);
        }
        return ret.get();
    }

    /**
     * Tries to parse the value from the argument
     *
     * @param argument the argument
     * @return the value, if parsing was successful
     */
    @NonNull
    default Optional<T> parse(@NonNull Argument argument) {
        return argument.value().flatMap(this::parse);
    }

    /**
     * Parses the value from an argument, throwing an interrupt exception if
     * parsing failed.
     *
     * @param argument the argument
     * @throws CommandInterruptException if command was interrupted
     * @return the value
     */
    @NonNull
    default T parseOrFail(@NonNull Argument argument) throws CommandInterruptException {
        Optional<String> value = argument.value();
        if (!value.isPresent()) {
            throw generateException(argument.index());
        }
        return parseOrFail(value.get());
    }

    /**
     * Creates a new parser which first tries to obtain a value from
     * this parser, then from another if the former was not successful.
     *
     * @param other the other parser
     * @return the combined parser
     */
    @NonNull
    default ArgumentParser<T> thenTry(@NonNull ArgumentParser<T> other) {
        ArgumentParser<T> first = this;
        return t -> {
            Optional<T> ret = first.parse(t);
            return ret.isPresent() ? ret : other.parse(t);
        };
    }
}
