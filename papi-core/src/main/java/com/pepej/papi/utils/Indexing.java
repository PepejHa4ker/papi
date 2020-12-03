package com.pepej.papi.utils;

import com.google.common.collect.ImmutableMap;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.function.Function;

/**
 * An indexing utility.
 */
public final class Indexing {

    private Indexing() {

    }

    /**
     * Builds an index for the given values, using the indexing function.
     *
     * @param values the values to index
     * @param indexFunction the index function
     * @param <I> the index type
     * @param <R> the actual (value) type
     * @return the index
     */
    public static <I, R> Map<I, R> build(@NonNull final Iterable<? extends R> values, @NonNull final Function<? super R, ? extends I> indexFunction) {
        Objects.requireNonNull(indexFunction, "indexFunction");
        return buildMultiple(values, r -> Collections.singleton(indexFunction.apply(r)));
    }

    /**
     * Builds an index for the given values, using the indexing function.
     *
     * @param values the values to index
     * @param indexFunction the index function
     * @param <I> the index type
     * @param <R> the actual (value) type
     * @return the index
     */
    public static <I, R> Map<I, R> buildMultiple(@NonNull final Iterable<? extends R> values, @NonNull final Function<? super R, ? extends Iterable<? extends I>> indexFunction) {
        Objects.requireNonNull(values, "values");
        Objects.requireNonNull(indexFunction, "indexFunction");

        Map<I, R> map = new HashMap<>();
        for (R value : values) {
            Iterable<? extends I> indexes = indexFunction.apply(value);
            for (I index : indexes) {
                R prev = map.put(index, value);
                if (prev != null) {
                    throw new IllegalStateException("An index for " + value + " (" + index + ") was already associated with " + prev);
                }
            }
        }
        return ImmutableMap.copyOf(map);
    }

    /**
     * Builds an index for the given values, using the indexing function.
     *
     * @param values the values to index
     * @param indexFunction the index function
     * @param <I> the index type
     * @param <R> the actual (value) type
     * @return the index
     */
    public static <I, R> Map<I, R> build(@NonNull final R[] values, @NonNull final Function<? super R, ? extends I> indexFunction) {
        Objects.requireNonNull(values, "values");
        return build(Arrays.asList(values), indexFunction);
    }

    /**
     * Builds an index for the given values, using the indexing function.
     *
     * @param values the values to index
     * @param indexFunction the index function
     * @param <I> the index type
     * @param <R> the actual (value) type
     * @return the index
     */
    public static <I, R> Map<I, R> buildMultiple(@NonNull final R[] values, @NonNull final Function<? super R, ? extends Iterable<? extends I>> indexFunction) {
        Objects.requireNonNull(values, "values");
        return buildMultiple(Arrays.asList(values), indexFunction);
    }

    /**
     * Builds an index for the given enum using {@link Enum#name()}.
     *
     * @param enumClass the enum class
     * @param <R> the enum type
     * @return the index
     */
    public static <R extends Enum<?>> Map<String, R> buildFromEnumName(@NonNull final Class<? extends R> enumClass) {
        Objects.requireNonNull(enumClass, "enumClass");

        R[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null) {
            throw new IllegalArgumentException("Type is not an enum: " + enumClass);
        }
        return build(enumConstants, Enum::name);
    }
}
