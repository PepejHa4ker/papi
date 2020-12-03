package com.pepej.papi.metadata.type;

import com.pepej.papi.metadata.MetadataKey;
import com.pepej.papi.metadata.MetadataMap;
import com.pepej.papi.metadata.MetadataRegistry;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link World}s.
 */
public interface WorldMetadataRegistry extends MetadataRegistry<UUID> {

    /**
     * Produces a {@link MetadataMap} for the given world.
     *
     * @param world the world
     * @return a metadata map
     */
    @NonNull
    MetadataMap provide(@NonNull World world);

    /**
     * Gets a {@link MetadataMap} for the given world, if one already exists and has
     * been cached in this registry.
     *
     * @param world the world
     * @return a metadata map, if present
     */
    @NonNull
    Optional<MetadataMap> get(@NonNull World world);

    /**
     * Gets a map of the worlds with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of worlds to key value
     */
    @NonNull
    <K> Map<World, K> getAllWithKey(@NonNull MetadataKey<K> key);

}
