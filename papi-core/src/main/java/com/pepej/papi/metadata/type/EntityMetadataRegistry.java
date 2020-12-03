package com.pepej.papi.metadata.type;

import com.pepej.papi.metadata.MetadataKey;
import com.pepej.papi.metadata.MetadataMap;
import com.pepej.papi.metadata.MetadataRegistry;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Entity}s.
 */
public interface EntityMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given entity.
     *
     * @param entity the entity
     * @return a metadata map
     */
    @NonNull
    MetadataMap provide(@NonNull Entity entity);

    /**
     * Gets a {@link MetadataMap} for the given entity, if one already exists and has
     * been cached in this registry.
     *
     * @param entity the entity
     * @return a metadata map, if present
     */
    @NonNull
    Optional<MetadataMap> get(@NonNull Entity entity);

    /**
     * Gets a map of the entities with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of entities to key value
     */
    @NonNull
    <K> Map<Entity, K> getAllWithKey(@NonNull MetadataKey<K> key);

}
