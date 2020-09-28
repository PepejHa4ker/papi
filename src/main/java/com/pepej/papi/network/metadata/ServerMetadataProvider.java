package com.pepej.papi.network.metadata;

/**
 * Provides metadata about the current server instance to be broadcasted to the
 * network.
 */
@FunctionalInterface
public interface ServerMetadataProvider {

    /**
     * Provides the metadata.
     *
     * @return the metadata
     */
    Iterable<ServerMetadata> provide();

}
