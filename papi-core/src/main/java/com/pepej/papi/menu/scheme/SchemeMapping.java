package com.pepej.papi.menu.scheme;

import com.pepej.papi.menu.Item;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * Represents a mapping to be used in a {@link MenuScheme}
 */
public interface SchemeMapping {

    /**
     * Gets an item from the mapping which represents the given key.
     *
     * @param key the mapping key
     * @return an item if present, otherwise an empty optional
     */
    @NonNull
    default Optional<Item> get(int key) {
        return Optional.ofNullable(getNullable(key));
    }

    /**
     * Gets an item from the mapping which represents the given key.
     *
     * @param key the mapping key
     * @return an item if present, otherwise null
     */
    @Nullable
    Item getNullable(int key);

    /**
     * Gets if this scheme has a mapping for a given key
     *
     * @param key the mapping key
     * @return true if the scheme has a mapping for the key
     */
    boolean hasMappingFor(int key);

    /**
     * Makes a copy of this scheme mapping.
     *
     * @return a copy of this mapping.
     */
    @NonNull
    SchemeMapping copy();

}
