package com.pepej.papi.network.metadata;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.pepej.papi.gson.GsonProvider;

import java.util.Objects;

public final class ServerMetadata {

    public static ServerMetadata of(String key, JsonElement data) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(data, "data");
        return new ServerMetadata(key, data);
    }

    public static <T> ServerMetadata of(String key, T data, Class<T> type) {
        Objects.requireNonNull(type, "type");
        return of(key, data, TypeToken.of(type));
    }

    public static <T> ServerMetadata of(String key, T data, TypeToken<T> type) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(data, "data");
        Objects.requireNonNull(type, "type");

        JsonElement json = GsonProvider.standard().toJsonTree(data, type.getType());
        return new ServerMetadata(key, json);
    }

    private final String key;
    private final JsonElement data;

    private ServerMetadata(String key, JsonElement data) {
        this.key = key;
        this.data = data;
    }

    public String key() {
        return this.key;
    }

    public JsonElement data() {
        return this.data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerMetadata that = (ServerMetadata) o;
        return this.key.equals(that.key) &&
                this.data.equals(that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.data);
    }

    @Override
    public String toString() {
        return "ServerMetadata{key=" + this.key + ", data=" + this.data + '}';
    }
}