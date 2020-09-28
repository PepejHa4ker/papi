package com.pepej.papi.messaging;

import javax.annotation.Nonnull;
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
    @Nonnull
    String getId();

    /**
     * Gets the groups this server is a member of.
     *
     * @return this instance's groups
     */
    @Nonnull
    Set<String> getGroups();

}
