package com.pepej.papi.network.redirect;

import com.pepej.papi.profiles.Profile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface PlayerRedirector {

    /**
     * Requests that a certain player is moved to the given server.
     *
     * @param serverId the id of the server
     * @param profile the player to redirect
     */
    void redirectPlayer(@NotNull String serverId, @NotNull Profile profile);

    /**
     * Requests that a certain player is moved to the given server.
     *
     * @param serverId the id of the server
     * @param player the player to redirect
     */
    default void redirectPlayer(@NotNull String serverId, @NotNull Player player) {
        redirectPlayer(serverId, Profile.create(player));
    }

}
