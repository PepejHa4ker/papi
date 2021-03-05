package com.pepej.papi.menu.scheme;

import com.google.common.collect.Range;
import com.pepej.papi.menu.Item;
import lombok.EqualsAndHashCode;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Implements {@link SchemeMapping} using a function.
 */
@EqualsAndHashCode(of = {"function", "validRange"})
public final class FunctionalSchemeMapping implements SchemeMapping {
    private final IntFunction<Item> function;
    private final Range<Integer> validRange;

    @NonNull
    public static SchemeMapping of(@NonNull IntFunction<Item> function, @NonNull Range<Integer> validRange) {
        return new FunctionalSchemeMapping(function, validRange);
    }

    private FunctionalSchemeMapping(@NonNull IntFunction<Item> function, @NonNull Range<Integer> validRange) {
        this.function = Objects.requireNonNull(function, "function");
        this.validRange = Objects.requireNonNull(validRange, "validRange");
    }

    @Override
    @Nullable
    public Item getNullable(int key) {
        if (!hasMappingFor(key)) {
            return null;
        }
        return function.apply(key);
    }

    @Override
    public boolean hasMappingFor(int key) {
        return validRange.contains(key);
    }

    @NonNull
    @Override
    public SchemeMapping copy() {
        return this; // no need to make a copy, the backing data is immutable
    }

}