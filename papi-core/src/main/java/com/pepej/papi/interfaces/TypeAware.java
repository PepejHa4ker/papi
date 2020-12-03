package com.pepej.papi.interfaces;

import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object that knows it's own type parameter.
 *
 * @param <T> the type
 */
public interface TypeAware<T> {

    @NonNull
    TypeToken<T> getType();

}