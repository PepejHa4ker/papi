package com.pepej.papi.metadata;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Supplier;

/**
 * An object which represents nothing.
 *
 * <p>Used mostly by {@link MetadataKey}s, where the presence of the key in the map
 * is enough for a behaviour to apply. In other words, the value is not significant.</p>
 *
 * <p>Very similar to {@link Void}, except this class also provides an instance of the "empty" object.</p>
 */
public final class Empty {
    private static final Empty INSTANCE = new Empty();
    private static final Supplier<Empty> SUPPLIER = () -> INSTANCE;

    @NonNull
    public static Empty instance() {
        return INSTANCE;
    }

    @NonNull
    public static Supplier<Empty> supplier() {
        return SUPPLIER;
    }

    private Empty() {

    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }

    @Override
    public String toString() {
        return "Empty";
    }
}
