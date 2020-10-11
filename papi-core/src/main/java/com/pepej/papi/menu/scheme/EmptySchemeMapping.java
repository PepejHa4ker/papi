package com.pepej.papi.menu.scheme;

import com.pepej.papi.menu.Item;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * An empty menu scheme.
 */
final class EmptySchemeMapping implements SchemeMapping {

    @Nonnull
    @Override
    public Optional<Item> get(int key) {
        return Optional.empty();
    }

    @Override
    public Item getNullable(int key) {
        return null;
    }

    @Override
    public boolean hasMappingFor(int key) {
        return false;
    }

    @Nonnull
    @Override
    public SchemeMapping copy() {
        return this; // no need to make a copy, this class is a singleton
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }
}
