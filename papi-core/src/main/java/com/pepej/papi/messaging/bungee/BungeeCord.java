package com.pepej.papi.messaging.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.pepej.papi.promise.Promise;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * API interface to encapsulate the BungeeCord Plugin Messaging API.
 *
 * <p>The returned futures should never be {@link Promise#join() joined} or waited for on
 * the Server thread.</p>
 */
public interface BungeeCord {

    /**
     * Server name to represent all servers on the proxy
     */
    String ALL_SERVERS = "ALL";

    /**
     * Server name to represent only the online servers on the proxy
     */
    String ONLINE_SERVERS = "ONLINE";

    /**
     * Connects a player to said subserver
     *
     * @param player the player to connect
     * @param serverName the name of the server to connect to
     */
    void connect(@Nonnull Player player, @Nonnull String serverName);

    /**
     * Connects a named player to said subserver
     *
     * @param playerName the username of the player to connect
     * @param serverName the name of the server to connect to
     */
    void connectOther(@Nonnull String playerName, @Nonnull String serverName);

    /**
     * Get the real IP of a player
     *
     * @param player the player to get the IP of
     * @return a future
     */
    @Nonnull
    Promise<Map.Entry<String, Integer>> ip(@Nonnull Player player);

    /**
     * Gets the amount of players on a certain server, or all servers
     *
     * @param serverName the name of the server to get the player count for. Use {@link #ALL_SERVERS} to get the global count
     * @return a future
     */
    @Nonnull
    Promise<Integer> playerCount(@Nonnull String serverName);

    /**
     * Gets a list of players connected on a certain server, or all servers.
     *
     * @param serverName the name of the server to get the player list for. Use {@link #ALL_SERVERS} to get the global list
     * @return a future
     */
    @Nonnull
    Promise<List<String>> playerList(@Nonnull String serverName);

    /**
     * Get a list of server name strings, as defined in the BungeeCord config
     *
     * @return a future
     */
    @Nonnull
    Promise<List<String>> getServers();

    /**
     * Send a message (as in chat message) to the specified player
     *
     * @param playerName the username of the player to send the message to
     * @param message the message to send
     */
    void message(@Nonnull String playerName, @Nonnull String message);

    /**
     * Gets this servers name, as defined in the BungeeCord config
     *
     * @return a future
     */
    @Nonnull
    Promise<String> getServer();

    /**
     * Get the UUID of a player
     *
     * @param player the player to get the uuid of
     * @return a future
     */
    @Nonnull
    Promise<UUID> uuid(@Nonnull Player player);

    /**
     * Get the UUID of any player connected to the proxy
     *
     * @param playerName the username of the player to get the uuid of
     * @return a future
     */
    @Nonnull
    Promise<UUID> uuidOther(@Nonnull String playerName);

    /**
     * Get the IP of any server connected to the proxy
     *
     * @param serverName the name of the server to get the ip of
     * @return a future
     */
    @Nonnull
    Promise<Map.Entry<String, Integer>> serverIp(@Nonnull String serverName);

    /**
     * Kick a player from the proxy
     *
     * @param playerName the username of the player to kick
     * @param reason the reason to display to the player when they are kicked
     */
    void kickPlayer(@Nonnull String playerName, @Nonnull String reason);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallbackRaw(String, Predicate)} to register listeners on a given subchannel.</p>
     *
     * @param serverName the name of the server to send to. use {@link #ALL_SERVERS} to send to all servers, or {@link #ONLINE_SERVERS} to only send to servers which are online.
     * @param channelName the name of the subchannel
     * @param data the data to send
     */
    void forward(@Nonnull String serverName, @Nonnull String channelName, @Nonnull byte[] data);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallback(String, Predicate)} to register listeners on a given subchannel.</p>
     *
     * @param serverName the name of the server to send to. use {@link #ALL_SERVERS} to send to all servers, or {@link #ONLINE_SERVERS} to only send to servers which are online.
     * @param channelName the name of the subchannel
     * @param data the data to send
     */
    void forward(@Nonnull String serverName, @Nonnull String channelName, @Nonnull ByteArrayDataOutput data);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallbackRaw(String, Predicate)} to register listeners on a given subchannel.</p>
     *
     * @param playerName the username of a player. BungeeCord will send the forward message to their server.
     * @param channelName the name of the subchannel
     * @param data the data to send
     */
    void forwardToPlayer(@Nonnull String playerName, @Nonnull String channelName, @Nonnull byte[] data);

    /**
     * Sends a custom plugin message to a given server.
     *
     * <p>You can use {@link #registerForwardCallback(String, Predicate)} to register listeners on a given subchannel.</p>
     *
     * @param playerName the username of a player. BungeeCord will send the forward message to their server.
     * @param channelName the name of the subchannel
     * @param data the data to send
     */
    void forwardToPlayer(@Nonnull String playerName, @Nonnull String channelName, @Nonnull ByteArrayDataOutput data);

    /**
     * Registers a callback to listen for messages sent on forwarded subchannels.
     *
     * <p>Use {@link #forward(String, String, byte[])} to dispatch messages.</p>
     *
     * @param channelName the name of the channel to listen on
     * @param callback the callback. the predicate should return true when the callback should be unregistered.
     */
    void registerForwardCallbackRaw(@Nonnull String channelName, @Nonnull Predicate<byte[]> callback);

    /**
     * Registers a callback to listen for messages sent on forwarded subchannels.
     *
     * <p>Use {@link #forward(String, String, ByteArrayDataOutput)} to dispatch messages.</p>
     *
     * @param channelName the name of the channel to listen on
     * @param callback the callback. the predicate should return true when the callback should be unregistered.
     */
    void registerForwardCallback(@Nonnull String channelName, @Nonnull Predicate<ByteArrayDataInput> callback);

}
