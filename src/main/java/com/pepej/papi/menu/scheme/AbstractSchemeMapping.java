package com.pepej.papi.menu.scheme;

import com.google.common.collect.ImmutableMap;
import com.pepej.papi.menu.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;

/**
 * Implements {@link SchemeMapping} using an immutable map.
 */
public class AbstractSchemeMapping implements SchemeMapping {
    private final Map<Integer, Item> mapping;

    @Nonnull
    public static SchemeMapping of(@Nonnull Map<Integer, Item> mapping) {
        return new AbstractSchemeMapping(mapping);
    }

    private AbstractSchemeMapping(@Nonnull Map<Integer, Item> mapping) {
        Objects.requireNonNull(mapping, "mapping");
        this.mapping = ImmutableMap.copyOf(mapping);
    }

    @Override
    @Nullable
    public Item getNullable(int key) {
        return this.mapping.get(key);
    }

    @Override
    public boolean hasMappingFor(int key) {
        return this.mapping.containsKey(key);
    }

    @Nonnull
    @Override
    public SchemeMapping copy() {
        return this; // no need to make a copy, the backing data is immutable
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AbstractSchemeMapping && ((AbstractSchemeMapping) obj).mapping.equals(this.mapping);
    }

    @Override
    public int hashCode() {
        return this.mapping.hashCode();
    }
}
