package com.pepej.papi.metadata.type;

import com.pepej.papi.metadata.MetadataKey;
import com.pepej.papi.metadata.MetadataMap;
import com.pepej.papi.metadata.MetadataRegistry;
import com.pepej.papi.serialize.BlockPosition;
import org.bukkit.block.Block;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Optional;

/**
 * A registry which provides and stores {@link MetadataMap}s for {@link Block}s.
 */
public interface BlockMetadataRegistry extends MetadataRegistry<BlockPosition> {

    /**
     * Produces a {@link MetadataMap} for the given block.
     *
     * @param block the block
     * @return a metadata map
     */
    @NonNull
    MetadataMap provide(@NonNull Block block);

    /**
     * Gets a {@link MetadataMap} for the given block, if one already exists and has
     * been cached in this registry.
     *
     * @param block the block
     * @return a metadata map, if present
     */
    @NonNull
    Optional<MetadataMap> get(@NonNull Block block);

    /**
     * Gets a map of the blocks with a given metadata key
     *
     * @param key the key
     * @param <K> the key type
     * @return an immutable map of blocks to key value
     */
    @NonNull
    <K> Map<BlockPosition, K> getAllWithKey(@NonNull MetadataKey<K> key);

}
