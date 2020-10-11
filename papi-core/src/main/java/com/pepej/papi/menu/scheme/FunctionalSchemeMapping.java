package com.pepej.papi.menu.scheme;

import com.google.common.collect.Range;
import com.pepej.papi.menu.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Implements {@link SchemeMapping} using a function.
 */
public final class FunctionalSchemeMapping implements SchemeMapping {
    private final IntFunction<Item> function;
    private final Range<Integer> validRange;

    public static @Nonnull
    SchemeMapping of(@Nonnull IntFunction<Item> function, @Nonnull Range<Integer> validRange) {
        return new FunctionalSchemeMapping(function, validRange);
    }

    private FunctionalSchemeMapping(@Nonnull IntFunction<Item> function, @Nonnull Range<Integer> validRange) {
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
        return this.validRange.contains(key);
    }

    @Nonnull
    @Override
    public SchemeMapping copy() {
        return this; // no need to make a copy, the backing data is immutable
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FunctionalSchemeMapping that = (FunctionalSchemeMapping) o;
        return function.equals(that.function) &&
                validRange.equals(that.validRange);
    }

    @Override
    public int hashCode() {
        return Objects.hash(function, validRange);
    }
}
