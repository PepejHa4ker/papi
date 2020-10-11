package com.pepej.papi.metadata;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nonnull;
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

    @Nonnull
    protected final LoadingCache<T, MetadataMap> cache = CacheBuilder.newBuilder().build(getLoader());

    @Nonnull
    @Override
    public MetadataMap provide(@Nonnull T id) {
        Objects.requireNonNull(id, "id");
        return this.cache.getUnchecked(id);
    }

    @Nonnull
    @Override
    public Optional<MetadataMap> get(@Nonnull T id) {
        Objects.requireNonNull(id, "id");
        return Optional.ofNullable(this.cache.getIfPresent(id));
    }

    @Override
    public void remove(@Nonnull T id) {
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
        public MetadataMap load(@Nonnull T key) {
            return MetadataMap.create();
        }
    }
}
