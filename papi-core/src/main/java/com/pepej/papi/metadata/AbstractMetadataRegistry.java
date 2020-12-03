package com.pepej.papi.metadata;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * A basic implementation of {@link MetadataRegistry} using a LoadingCache.
 *
 * @param <T> the type
 */
public class AbstractMetadataRegistry<T> implements MetadataRegistry<T> {

    private static final CacheLoader<?, MetadataMap> LOADER = new Loader<>();
    private static <T> CacheLoader<T, MetadataMap> getLoader() {
        //noinspection unchecked
        return (CacheLoader) LOADER;
    }

    @NonNull
    protected final LoadingCache<T, MetadataMap> cache = CacheBuilder.newBuilder().build(getLoader());

    @NonNull
    @Override
    public MetadataMap provide(@NonNull T id) {
        Objects.requireNonNull(id, "id");
        return this.cache.getUnchecked(id);
    }

    @NonNull
    @Override
    public Optional<MetadataMap> get(@NonNull T id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(this.cache.getIfPresent(id));
    }

    @Override
    public void remove(@NonNull T id) {
        MetadataMap map = this.cache.asMap().remove(id);
        if (map != null) {
            map.clear();
        }
    }

    @Override
    public void cleanup() {
        // MetadataMap#isEmpty also removes expired values
        this.cache.asMap().values().removeIf(MetadataMap::isEmpty);
    }

    private static final class Loader<T> extends CacheLoader<T, MetadataMap> {
        @Override
        public MetadataMap load(@NonNull T key) {
            return MetadataMap.create();
        }
    }
}
