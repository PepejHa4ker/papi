package com.pepej.papi.metadata;

import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

final class MetadataKeyImpl<T> implements MetadataKey<T> {

    private final String id;
    private final TypeToken<T> type;

    MetadataKeyImpl(String id, TypeToken<T> type) {
        this.id = id.toLowerCase();
        this.type = type;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @NonNull
    @Override
    public TypeToken<T> getType() {
        return this.type;
    }

    @Override
    public T cast(Object object) throws ClassCastException {
        Objects.requireNonNull(object, "object");
        //noinspection unchecked
        return (T) this.type.getRawType().cast(object);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MetadataKeyImpl && ((MetadataKeyImpl) obj).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
}
