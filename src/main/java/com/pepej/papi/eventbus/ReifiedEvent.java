package com.pepej.papi.eventbus;

import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;

/**
 * A type that knows its own type parameter.
 *
 * @param <T> the type
 */
public interface ReifiedEvent<T> {
    /**
     * Gets a type token representing the type parameter.
     *
     * @return a type token
     */
    @Nonnull
    TypeToken<T> type();
}

