package com.pepej.papi.metadata;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

/**
 * A registry which provides and stores {@link MetadataMap}s for a given type.
 *
 * @param <T> the type
 */
public interface MetadataRegistry<T> {

    /**
     * Produces a {@link MetadataMap} for the given object.
     *
     * @param id the object
     * @return a metadata map
     */
    @NonNull
    MetadataMap provide(@NonNull T id);

    /**
     * Gets a {@link MetadataMap} for the given object, if one already exists and has
     * been cached in this registry.
     *
     * @param id the object
     * @return a metadata map, if present
     */
    @NonNull
    Optional<MetadataMap> get(@NonNull T id);

    /**
     * Deletes the {@link MetadataMap} and all contained {@link MetadataKey}s for
     * the given object.
     *
     * @param id the object
     */
    void remove(@NonNull T id);

    /**
     * Performs cache maintenance to remove empty map instances and expired transient values.
     */
    void cleanup();

}
