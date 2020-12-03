package com.pepej.papi.messaging;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

/**
 * Provides information about the current server instance.
 */
public interface InstanceData {

    /**
     * Gets the unique ID of the current server instance.
     *
     * @return the id of the server
     */
    @NonNull
    String getId();

    /**
     * Gets the groups this server is a member of.
     *
     * @return this instance's groups
     */
    @NonNull
    Set<String> getGroups();

}
