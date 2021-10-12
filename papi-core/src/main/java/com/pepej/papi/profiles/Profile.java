package com.pepej.papi.profiles;

import com.mojang.authlib.GameProfile;
import com.pepej.papi.shadow.bukkit.player.CraftPlayerShadow;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    static Profile create(@NotNull UUID uniqueId, @Nullable String name) {
        return new SimpleProfile(uniqueId, name);
    }

    /**
     * Creates a new profile instance
     *
     * @param player the player to create a profile for
     * @return the profile
     */
    @NotNull
    static Profile create(Player player) {
        return new SimpleProfile(player.getUniqueId(), player.getName());
    }

    /**
     * Gets the unique id associated with this profile
     *
     * @return the unique id
     */
    @NotNull
    UUID getUniqueId();


    /**
     * Gets the username associated with this profile
     *
     * @return the username
     */
    @NotNull
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