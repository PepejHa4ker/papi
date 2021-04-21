package com.pepej.papi.messaging.bungee;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.plugin.PapiPlugin;
import com.pepej.papi.promise.Promise;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.utils.Players;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public final class BungeeCordImpl implements BungeeCord, PluginMessageListener {
    /**
     * The name of the BungeeCord plugin channel
     */
    private static final String CHANNEL = "BungeeCord";

    /**
     * The plugin instance
     */
    private final PapiPlugin plugin;

    /**
     * If the listener has been registered
     */
    private final AtomicBoolean setup = new AtomicBoolean(false);

    /**
     * The registered listeners
     */
    private final List<MessageCallback> listeners = new LinkedList<>();

    /**
     * Lock to guard the 'listeners' list
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Messages to be sent
     */
    private final Set<MessageAgent> queuedMessages = ConcurrentHashMap.newKeySet();

    public BungeeCordImpl() {
        this.plugin = LoaderUtils.getPlugin();
    }

    public BungeeCordImpl(PapiPlugin plugin) {
        this.plugin = plugin;
    }

    private void ensureSetup() {
        if (!this.setup.compareAndSet(false, true)) {
            return;
        }

        this.plugin.getServer().getMessenger().registerOutgoingPluginChannel(this.plugin, CHANNEL);
        this.plugin.getServer().getMessenger().registerIncomingPluginChannel(this.plugin, CHANNEL, this);

        this.plugin.bind(CompositeTerminable.create()
                                            .with(() -> {
                                                this.plugin.getServer().getMessenger().unregisterOutgoingPluginChannel(this.plugin, CHANNEL);
                                                this.plugin.getServer().getMessenger().unregisterIncomingPluginChannel(this.plugin, CHANNEL, this);
                                            })
                                            .with(Schedulers.builder()
                                                            .sync()
                                                            .afterAndEvery(3, TimeUnit.SECONDS)
                                                            .run(this::flushQueuedMessages)
                                            )
        );
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if (!channel.equals(CHANNEL)) {
            return;
        }

        // create an input stream from the recieved data
        ByteArrayInputStream byteIn = new ByteArrayInputStream(data);

        // create a data input instance
        ByteArrayDataInput in = ByteStreams.newDataInput(byteIn);

        // read the subchannel & mark the beginning of the stream at this point, so we can reset to this position later
        String subChannel = in.readUTF();
        byteIn.mark(/* ignored */ 0);

        // pass the incoming message to all registered listeners
        this.lock.lock();
        try {
            Iterator<MessageCallback> it = this.listeners.iterator();
            while (it.hasNext()) {
                MessageCallback e = it.next();

                // check if the subchannel is valid
                if (!e.getSubChannel().equals(subChannel)) {
                    continue;
                }

                // reset the inputstream to the start position
                byteIn.reset();

                // test if the data should be "passed" to the callback
                boolean accepted = e.testResponse(player, in);
                if (!accepted) {
                    continue;
                }

                // reset again
                byteIn.reset();

                // pass the data to the callback
                boolean shouldRemove = e.acceptResponse(player, in);
                if (shouldRemove) {
                    it.remove();
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * Sends (or queues the sending of) the message encapsulated by the given message agent
     *
     * @param agent the agent
     */
    private void sendMessage(MessageAgent agent) {
        // check if the agent has a specific player handle to use when sending the message
        Player player = agent.getHandle();
        if (player != null) {
            if (!player.isOnline()) {
                throw new IllegalStateException("Player not online");
            }

            sendToChannel(agent, player);
            return;
        }

        // try to find a player
        player = Iterables.getFirst(Players.all(), null);
        if (player != null) {
            sendToChannel(agent, player);
        }
        else {
            // no players online, queue the message
            this.queuedMessages.add(agent);
            ensureSetup();
        }
    }

    private void flushQueuedMessages() {
        if (this.queuedMessages.isEmpty()) {
            return;
        }

        Player p = Iterables.getFirst(Players.all(), null);
        if (p != null) {
            this.queuedMessages.removeIf(ma -> {
                sendToChannel(ma, p);
                return true;
            });
        }
    }

    private void sendToChannel(MessageAgent agent, Player player) {
        ensureSetup();

        // create a new data output stream for the message
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // write the channel
        out.writeUTF(agent.getSubChannel());
        // append the agents data
        agent.appendPayload(out);

        byte[] buf = out.toByteArray();
        player.sendPluginMessage(this.plugin, CHANNEL, buf);

        // if the agent is also a MessageCallback, register it
        if (agent instanceof MessageCallback) {
            MessageCallback callback = (MessageCallback) agent;
            registerCallback(callback);
        }
    }

    private void registerCallback(MessageCallback callback) {
        ensureSetup();

        this.lock.lock();
        try {
            this.listeners.add(callback);
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public void connect(@NonNull Player player, @NonNull String serverName) {
        sendMessage(new ConnectAgent(player, serverName));
    }

    @Override
    public void connectOther(@NonNull String playerName, @NonNull String serverName) {
        sendMessage(new ConnectOtherAgent(playerName, serverName));
    }

    @Override
    public @NonNull Promise<Map.Entry<String, Integer>> ip(@NonNull Player player) {
        Promise<Map.Entry<String, Integer>> fut = Promise.empty();
        sendMessage(new IPAgent(player, fut));
        return fut;
    }

    @Override
    public @NonNull Promise<Integer> playerCount(@NonNull String serverName) {
        Promise<Integer> fut = Promise.empty();
        sendMessage(new PlayerCountAgent(serverName, fut));
        return fut;
    }

    @Override
    public @NonNull Promise<List<String>> playerList(@NonNull String serverName) {
        Promise<List<String>> fut = Promise.empty();
        sendMessage(new PlayerListAgent(serverName, fut));
        return fut;
    }

    @Override
    public @NonNull Promise<List<String>> getServers() {
        Promise<List<String>> fut = Promise.empty();
        sendMessage(new GetServersAgent(fut));
        return fut;
    }

    @Override
    public void message(@NonNull String playerName, @NonNull String message) {
        sendMessage(new PlayerMessageAgent(playerName, message));
    }

    @Override
    public void messageRaw(@NonNull final String playerName, @NonNull final String messageRaw) {
        sendMessage(new PlayerMessageRawAgent(playerName, messageRaw));
    }

    @Override
    public @NonNull Promise<String> getServer() {
        Promise<String> fut = Promise.empty();
        sendMessage(new GetServerAgent(fut));
        return fut;
    }

    @Override
    public @NonNull Promise<UUID> uuid(@NonNull Player player) {
        Promise<UUID> fut = Promise.empty();
        sendMessage(new UUIDAgent(player, fut));
        return fut;
    }

    @Override
    public @NonNull Promise<UUID> uuidOther(@NonNull String playerName) {
        Promise<UUID> fut = Promise.empty();
        sendMessage(new UUIDOtherAgent(playerName, fut));
        return fut;
    }

    @Override
    public @NonNull Promise<Map.Entry<String, Integer>> serverIp(@NonNull String serverName) {
        Promise<Map.Entry<String, Integer>> fut = Promise.empty();
        sendMessage(new ServerIPAgent(serverName, fut));
        return fut;
    }

    @Override
    public void kickPlayer(@NonNull String playerName, @NonNull String reason) {
        sendMessage(new KickPlayerAgent(playerName, reason));
    }

    @Override
    public void forward(@NonNull String serverName, @NonNull String channelName, byte[] data) {
        sendMessage(new ForwardAgent(serverName, channelName, data));
    }

    @Override
    public void forward(@NonNull String serverName, @NonNull String channelName, @NonNull ByteArrayDataOutput data) {
        sendMessage(new ForwardAgent(serverName, channelName, data));
    }

    @Override
    public void forwardToPlayer(@NonNull String playerName, @NonNull String channelName, byte[] data) {
        sendMessage(new ForwardToPlayerAgent(playerName, channelName, data));
    }

    @Override
    public void forwardToPlayer(@NonNull String playerName, @NonNull String channelName, @NonNull ByteArrayDataOutput data) {
        sendMessage(new ForwardToPlayerAgent(playerName, channelName, data));
    }

    @Override
    public void registerForwardCallbackRaw(@NonNull String channelName, @NonNull Predicate<byte[]> callback) {
        ForwardCustomCallback customCallback = new ForwardCustomCallback(channelName, callback);
        registerCallback(customCallback);
    }

    @Override
    public void registerForwardCallback(@NonNull String channelName, @NonNull Predicate<ByteArrayDataInput> callback) {
        Objects.requireNonNull(callback, "callback");
        final Predicate<ByteArrayDataInput> cb = callback;
        ForwardCustomCallback customCallback = new ForwardCustomCallback(channelName, bytes -> cb.test(ByteStreams.newDataInput(bytes)));
        registerCallback(customCallback);
    }

    /**
     * Responsible for writing data to the output stream when the message is to be sent
     */
    private interface MessageAgent {

        /**
         * Gets the sub channel this message should be sent using
         *
         * @return the message channel
         */
        String getSubChannel();

        /**
         * Gets the player to send the message via
         *
         * @return the player to send the message via, or null if any player should be used
         */
        @Nullable
        default Player getHandle() {
            return null;
        }

        /**
         * Appends the data for this message to the output stream
         *
         * @param out the output stream
         */
        default void appendPayload(ByteArrayDataOutput out) {

        }
    }

    /**
     * Responsible for monitoring incoming messages, and passing on the callback data if applicable
     */
    private interface MessageCallback {

        /**
         * Gets the sub channel this callback is listening for
         *
         * @return the message channel
         */
        String getSubChannel();

        /**
         * Returns true if the incoming data applies to this callback
         *
         * @param receiver the player instance which received the data
         * @param in       the input data
         * @return true if the data is applicable
         */
        default boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return true;
        }

        /**
         * Accepts the incoming data, and returns true if this callback should now be de-registered
         *
         * @param receiver the player instance which received the data
         * @param in       the input data
         * @return if the callback should be de-registered
         */
        boolean acceptResponse(Player receiver, ByteArrayDataInput in);

    }

    private static final class ConnectAgent implements MessageAgent {
        private static final String CHANNEL = "Connect";

        private final Player player;
        private final String serverName;

        private ConnectAgent(Player player, String serverName) {
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(serverName, "serverName");
            this.player = player;
            this.serverName = serverName;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public Player getHandle() {
            return this.player;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }
    }

    private static final class ConnectOtherAgent implements MessageAgent {
        private static final String CHANNEL = "ConnectOther";

        private final String playerName;
        private final String serverName;

        private ConnectOtherAgent(String playerName, String serverName) {
            Objects.requireNonNull(playerName, "playerName");
            Objects.requireNonNull(serverName, "serverName");
            this.playerName = playerName;
            this.serverName = serverName;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.serverName);
        }
    }

    private static final class IPAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "IP";

        private final Player player;
        private final Promise<Map.Entry<String, Integer>> callback;

        private IPAgent(Player player, Promise<Map.Entry<String, Integer>> callback) {
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(callback, "callback");
            this.player = player;
            this.callback = callback;

        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public Player getHandle() {
            return this.player;
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return receiver.getUniqueId().equals(this.player.getUniqueId());
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            String ip = in.readUTF();
            int port = in.readInt();
            this.callback.supply(Maps.immutableEntry(ip, port));
            return true;
        }
    }

    private static final class PlayerCountAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "PlayerCount";

        private final String serverName;
        private final Promise<Integer> callback;

        private PlayerCountAgent(String serverName, Promise<Integer> callback) {
            Objects.requireNonNull(serverName, "serverName");
            Objects.requireNonNull(callback, "callback");
            this.serverName = serverName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.serverName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            int count = in.readInt();
            this.callback.supply(count);
            return true;
        }
    }

    private static final class PlayerListAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "PlayerList";

        private final String serverName;
        private final Promise<List<String>> callback;

        private PlayerListAgent(String serverName, Promise<List<String>> callback) {
            Objects.requireNonNull(serverName, "serverName");
            Objects.requireNonNull(callback, "callback");
            this.serverName = serverName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.serverName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            String csv = in.readUTF();

            if (csv.isEmpty()) {
                this.callback.supply(ImmutableList.of());
                return true;
            }

            this.callback.supply(ImmutableList.copyOf(Splitter.on(", ").splitToList(csv)));
            return true;
        }
    }

    private static final class GetServersAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "GetServers";

        private final Promise<List<String>> callback;

        private GetServersAgent(Promise<List<String>> callback) {
            Objects.requireNonNull(callback, "callback");
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            String csv = in.readUTF();

            if (csv.isEmpty()) {
                this.callback.supply(ImmutableList.of());
                return true;
            }

            this.callback.supply(ImmutableList.copyOf(Splitter.on(", ").splitToList(csv)));
            return true;
        }
    }

    private static final class PlayerMessageAgent implements MessageAgent {
        private static final String CHANNEL = "Message";

        private final String playerName;
        private final String message;

        private PlayerMessageAgent(String playerName, String message) {
            Objects.requireNonNull(playerName, "playerName");
            Objects.requireNonNull(message, "message");
            this.playerName = playerName;
            this.message = message;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.message);
        }
    }


    private static final class PlayerMessageRawAgent implements MessageAgent {
        private static final String CHANNEL = "MessageRaw";

        private final String playerName;
        private final String messageRaw;

        private PlayerMessageRawAgent(String playerName, String messageRaw) {
            Objects.requireNonNull(playerName, "playerName");
            Objects.requireNonNull(messageRaw, "messageRaw");
            this.playerName = playerName;
            this.messageRaw = messageRaw;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.messageRaw);
        }
    }

    private static final class GetServerAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "GetServer";

        private final Promise<String> callback;

        private GetServerAgent(Promise<String> callback) {
            Objects.requireNonNull(callback, "callback");
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            this.callback.supply(in.readUTF());
            return true;
        }
    }

    private static final class UUIDAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "UUID";

        private final Player player;
        private final Promise<UUID> callback;

        private UUIDAgent(Player player, Promise<UUID> callback) {
            Objects.requireNonNull(player, "player");
            Objects.requireNonNull(callback, "callback");
            this.player = player;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public Player getHandle() {
            return this.player;
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return receiver.getUniqueId().equals(this.player.getUniqueId());
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            String uuid = in.readUTF();
            this.callback.supply(UUID.fromString(uuid));
            return true;
        }
    }

    private static final class UUIDOtherAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "UUIDOther";

        private final String playerName;
        private final Promise<UUID> callback;

        private UUIDOtherAgent(String playerName, Promise<UUID> callback) {
            Objects.requireNonNull(playerName, "playerName");
            Objects.requireNonNull(callback, "callback");
            this.playerName = playerName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.playerName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            String uuid = in.readUTF();
            this.callback.supply(UUID.fromString(uuid));
            return true;
        }
    }

    private static final class ServerIPAgent implements MessageAgent, MessageCallback {
        private static final String CHANNEL = "ServerIP";

        private final String serverName;
        private final Promise<Map.Entry<String, Integer>> callback;

        private ServerIPAgent(String serverName, Promise<Map.Entry<String, Integer>> callback) {
            Objects.requireNonNull(serverName, "serverName");
            Objects.requireNonNull(callback, "callback");
            this.serverName = serverName;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
        }

        @Override
        public boolean testResponse(Player receiver, ByteArrayDataInput in) {
            return in.readUTF().equalsIgnoreCase(this.serverName);
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            in.readUTF();
            String ip = in.readUTF();
            int port = in.readInt();
            this.callback.supply(Maps.immutableEntry(ip, port));
            return true;
        }
    }

    private static final class KickPlayerAgent implements MessageAgent {
        private static final String CHANNEL = "KickPlayer";

        private final String playerName;
        private final String reason;

        private KickPlayerAgent(String playerName, String reason) {
            Objects.requireNonNull(playerName, "playerName");
            Objects.requireNonNull(reason, "reason");
            this.playerName = playerName;
            this.reason = reason;
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.reason);
        }
    }

    private static final class ForwardAgent implements MessageAgent {
        private static final String CHANNEL = "Forward";

        private final String serverName;
        private final String channelName;
        private final byte[] data;

        private ForwardAgent(String serverName, String channelName, byte[] data) {
            Objects.requireNonNull(serverName, "serverName");
            Objects.requireNonNull(channelName, "channelName");
            this.serverName = serverName;
            this.channelName = channelName;
            this.data = data;
        }

        private ForwardAgent(String serverName, String channelName, ByteArrayDataOutput data) {
            Objects.requireNonNull(serverName, "serverName");
            Objects.requireNonNull(channelName, "channelName");
            Objects.requireNonNull(data, "data");
            this.serverName = serverName;
            this.channelName = channelName;
            this.data = data.toByteArray();
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.serverName);
            out.writeUTF(this.channelName);
            out.writeShort(this.data.length);
            out.write(this.data);
        }
    }

    private static final class ForwardToPlayerAgent implements MessageAgent {
        private static final String CHANNEL = "ForwardToPlayer";

        private final String playerName;
        private final String channelName;
        private final byte[] data;

        private ForwardToPlayerAgent(String playerName, String channelName, byte[] data) {

            this.playerName = playerName;
            this.channelName = channelName;
            this.data = data;
        }

        private ForwardToPlayerAgent(String playerName, String channelName, ByteArrayDataOutput data) {
            Objects.requireNonNull(playerName, "playerName");
            Objects.requireNonNull(channelName, "channelName");
            Objects.requireNonNull(data, "data");
            this.playerName = playerName;
            this.channelName = channelName;
            this.data = data.toByteArray();
        }

        @Override
        public String getSubChannel() {
            return CHANNEL;
        }

        @Override
        public void appendPayload(ByteArrayDataOutput out) {
            out.writeUTF(this.playerName);
            out.writeUTF(this.channelName);
            out.writeShort(this.data.length);
            out.write(this.data);
        }
    }

    private static final class ForwardCustomCallback implements MessageCallback {
        private final String subChannel;
        private final Predicate<byte[]> callback;

        private ForwardCustomCallback(String subChannel, Predicate<byte[]> callback) {
            Objects.requireNonNull(subChannel, "subChannel");
            Objects.requireNonNull(callback, "callback");
            this.subChannel = subChannel;
            this.callback = callback;
        }

        @Override
        public String getSubChannel() {
            return this.subChannel;
        }

        @Override
        public boolean acceptResponse(Player receiver, ByteArrayDataInput in) {
            short len = in.readShort();
            byte[] data = new byte[len];
            in.readFully(data);

            return this.callback.test(data);
        }
    }

}
