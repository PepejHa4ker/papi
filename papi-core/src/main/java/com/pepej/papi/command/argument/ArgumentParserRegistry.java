package com.pepej.papi.command.argument;
import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Optional;

/**
 * A collection of {@link ArgumentParser}s
 */
public interface ArgumentParserRegistry {

    /**
     * Tries to find an argument parser for the given type
     *
     * @param type the argument type
     * @param <T> the type
     * @return an argument, if one was found
     */
    @NonNull
    <T> Optional<ArgumentParser<T>> find(@NonNull TypeToken<T> type);

    /**
     * Tries to find an argument parser for the given class
     *
     * @param clazz the argument class
     * @param <T> the class type
     * @return an argument, if one was found
     */
    @NonNull
    default <T> Optional<ArgumentParser<T>> find(@NonNull Class<T> clazz) {
        return find(TypeToken.of(clazz));
    }

    /**
     * Finds all known parsers for a given type
     *
     * @param type the argument type
     * @param <T> the type
     * @return a collection of argument parsers
     */
    @NonNull
    <T> Collection<ArgumentParser<T>> findAll(@NonNull TypeToken<T> type);

    /**
     * Finds all known parsers for a given class
     *
     * @param clazz the argument class
     * @param <T> the class type
     * @return a collection of argument parsers
     */
    @NonNull
    default <T> Collection<ArgumentParser<T>> findAll(@NonNull Class<T> clazz) {
        return findAll(TypeToken.of(clazz));
    }

    /**
     * Registers a new parser with the registry
     *
     * @param type the argument type
     * @param parser the parser
     * @param <T> the type
     */
    <T> void register(@NonNull TypeToken<T> type, @NonNull ArgumentParser<T> parser);

    /**
     * Registers a new parser with the registry
     *
     * @param clazz the argument class
     * @param parser the parser
     * @param <T> the class type
     */
    default <T> void register(@NonNull Class<T> clazz, @NonNull ArgumentParser<T> parser) {
        register(TypeToken.of(clazz), parser);
    }
}
