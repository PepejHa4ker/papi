package com.pepej.papi.event.bus;

import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;


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
    @NonNull
    TypeToken<T> type();
}

