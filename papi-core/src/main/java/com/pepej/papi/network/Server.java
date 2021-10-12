package com.pepej.papi.network;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.pepej.papi.gson.GsonProvider;
import com.pepej.papi.messaging.InstanceData;
import com.pepej.papi.profiles.Profile;

import java.util.Map;
import java.util.UUID;

/**
 * Represents an individual server within a {@link Network}.
 */
public interface Server extends InstanceData {

    /**
     * Gets if the server is currently online
     *
     * @return if the server is online
     */
    boolean isOnline();

    /**
     * Gets the time the last ping was received from this server.
     *
     * @return the time of the last time, as a unix timestamp in milliseconds
     */
    long getLastPing();

    /**
     * Gets the players known to be online on this server.
     *
     * @return the online players.
     */
    Map<UUID, Profile> getOnlinePlayers();

    /**
     * Sends message to all players, connected to this server
     * @param message the message to send
     */
    void broadcast(String message);

    /**
     * Gets the maximum amount of players allowed on this server.
     *
     * @return the max players
     */
    int getMaxPlayers();

    /**
     * Gets whether the server is currently whitelisted.
     *
     * @return if the server is whitelisted
     */
    boolean isWhitelisted();

    Map<String, JsonElement> getRawMetadata();

    default <T> T getMetadata(String key, Class<T> type) {
        return getMetadata(key, TypeToken.of(type));
    }

    default <T> T getMetadata(String key, TypeToken<T> type) {
        JsonElement data = getRawMetadata().get(key);
        if (data == null) {
            return null;
        }
        return GsonProvider.standard().fromJson(data, type.getType());
    }
}
