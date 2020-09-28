package com.pepej.papi.profiles;

import org.bukkit.entity.HumanEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a Player's profile
 */
public interface Profile {

    /**
     * Creates a new profile instance
     *
     * @param uniqueId the unique id
     * @param name the username
     * @return the profile
     */
    @Nonnull
    static Profile create(@Nonnull UUID uniqueId, @Nullable String name) {
        return new SimpleProfile(uniqueId, name);
    }

    /**
     * Creates a new profile instance
     *
     * @param player the player to create a profile for
     * @return the profile
     */
    @Nonnull
    static Profile create(HumanEntity player) {
        return new SimpleProfile(player.getUniqueId(), player.getName());
    }

    /**
     * Gets the unique id associated with this profile
     *
     * @return the unique id
     */
    @Nonnull
    UUID getUniqueId();

    /**
     * Gets the username associated with this profile
     *
     * @return the username
     */
    @Nonnull
    Optional<String> getName();

    /**
     * Gets the timestamp when this Profile was created or last updated.
     *
     * <p>The returned value is a unix timestamp in milliseconds.</p>
     *
     * @return the profiles last update time
     */
    long getTimestamp();

}