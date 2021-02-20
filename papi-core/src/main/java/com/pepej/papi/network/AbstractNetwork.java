package com.pepej.papi.network;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.cooldown.Cooldown;
import com.pepej.papi.event.bus.api.EventBus;
import com.pepej.papi.event.bus.api.EventSubscriber;
import com.pepej.papi.event.bus.api.PostResult;
import com.pepej.papi.event.bus.api.SimpleEventBus;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.messaging.Channel;
import com.pepej.papi.messaging.InstanceData;
import com.pepej.papi.messaging.Messenger;
import com.pepej.papi.network.event.NetworkEvent;
import com.pepej.papi.network.event.ServerConnectEvent;
import com.pepej.papi.network.event.ServerDisconnectEvent;
import com.pepej.papi.network.event.ServerStatusEvent;
import com.pepej.papi.network.metadata.ServerMetadata;
import com.pepej.papi.network.metadata.ServerMetadataProvider;
import com.pepej.papi.network.metadata.TpsMetadataProvider;
import com.pepej.papi.profiles.Profile;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.utils.Players;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class AbstractNetwork implements Network {
    protected final CompositeTerminable compositeTerminable = CompositeTerminable.create();

    protected final Messenger messenger;
    protected final InstanceData instanceData;

    private final EventBus<NetworkEvent> eventBus = new SimpleEventBus<>(NetworkEvent.class);
    private final List<ServerMetadataProvider> metadataProviders = new CopyOnWriteArrayList<>();
    private final Map<String, ServerImpl> servers = new ConcurrentHashMap<>();

    public AbstractNetwork(Messenger messenger, InstanceData instanceData) {
        this.messenger = messenger;
        this.instanceData = instanceData;

        /*
         * Handle connect / disconnect messages.
         * These "events" are sent via the 'pnet-events' channel.
         */

        Channel<EventMessage> eventsChannel = messenger.getChannel("pnet-events", EventMessage.class);
        // incoming
        eventsChannel.newAgent((agent, message) -> {
            switch (message.type) {
                case "connect":
                    postEvent(new ServerConnectEvent(message.id, handleIncomingStatusMessage(message.status)));
                    break;
                case "disconnect":
                    if (!instanceData.getId().equals(message.id)) {
                        postEvent(new ServerDisconnectEvent(message.id, message.reason));
                    }
                    break;
            }
        }).bindWith(this.compositeTerminable);

        // outgoing (disconnect)
        EventSubscriber<ServerDisconnectEvent> disconnectListener = new EventSubscriber<ServerDisconnectEvent>() {
            @Override
            public void invoke(@NonNull ServerDisconnectEvent event) {
                if (!event.getId().equals(instanceData.getId())) {
                    return;
                }

                EventMessage message = new EventMessage();
                message.id = event.getId();
                message.type = "disconnect";
                message.reason = event.getReason();
                eventsChannel.sendMessage(message);

                // only ever send one disconnect message.
                AbstractNetwork.this.eventBus.unregister(this);
            }
        };
        this.eventBus.register(ServerDisconnectEvent.class, disconnectListener);
        LoaderUtils.getPlugin().bind(() -> postEvent(new ServerDisconnectEvent(instanceData.getId(), "stopping")));

        // outgoing (connect)
        registerMetadataProviders();
        EventMessage connectionMessage = new EventMessage();
        connectionMessage.id = instanceData.getId();
        connectionMessage.type = "connect";
        connectionMessage.status = produceStatusMessage();
        eventsChannel.sendMessage(connectionMessage);


        /*
         * Handle status messages.
         * These are sent via the 'pnet-status' channel.
         */

        Channel<StatusMessage> statusChannel = messenger.getChannel("pnet-status", StatusMessage.class);
        // incoming
        statusChannel.newAgent((agent, message) -> handleIncomingStatusMessage(message)).bindWith(this.compositeTerminable);
        // outgoing
        Schedulers.builder()
                  .async()
                  .afterAndEvery(3, TimeUnit.SECONDS)
                  .run(() -> {
                      StatusMessage msg = produceStatusMessage();
                      statusChannel.sendMessage(msg);
                  })
                  .bindWith(this.compositeTerminable);
    }

    protected void registerMetadataProviders() {
        // register default providers
        registerMetadataProvider(TpsMetadataProvider.INSTANCE);
    }

    protected void postEvent(NetworkEvent event) {
        try {
            this.eventBus.post(event).raise();
        } catch (PostResult.CompositeException e) {
            throw new RuntimeException(e);
        }
    }

    private StatusMessage produceStatusMessage() {
        StatusMessage msg = new StatusMessage();
        msg.time = System.currentTimeMillis();
        msg.id = this.instanceData.getId();
        msg.groups = new ArrayList<>(this.instanceData.getGroups());

        msg.players = new HashMap<>();
        Players.forEach(p -> msg.players.put(p.getUniqueId(), p.getName()));

        msg.maxPlayers = Bukkit.getMaxPlayers();
        msg.whitelisted = Bukkit.hasWhitelist();

        // collect server metadata
        msg.metadata = new HashMap<>();
        for (ServerMetadataProvider metadataProvider : this.metadataProviders) {
            try {
                for (ServerMetadata metadata : metadataProvider.provide()) {
                    msg.metadata.put(metadata.key(), metadata.data());
                }
            } catch (Exception e) {
                new RuntimeException("Exception calling ServerMetadataProvider " + metadataProvider, e).printStackTrace();
            }
        }

        return msg;
    }

    private ServerImpl handleIncomingStatusMessage(StatusMessage message) {
        ServerImpl server = this.servers.computeIfAbsent(message.id, ServerImpl::new);
        server.loadData(message);
        postEvent(new ServerStatusEvent(server));
        return server;
    }

    @Override
    public Map<String, Server> getServers() {
        return Collections.unmodifiableMap(this.servers);
    }

    @Override
    public Map<UUID, Profile> getOnlinePlayers() {
        Map<UUID, Profile> players = new HashMap<>();
        for (Server server : this.servers.values()) {
            players.putAll(server.getOnlinePlayers());
        }
        return Collections.unmodifiableMap(players);
    }

    @Override
    public int getOverallPlayerCount() {
        return this.servers.values().stream().mapToInt(s -> s.getOnlinePlayers().size()).sum();
    }

    @Override
    public void registerMetadataProvider(ServerMetadataProvider metadataProvider) {
        this.metadataProviders.add(metadataProvider);
    }

    @Override
    public EventBus<NetworkEvent> getEventBus() {
        return this.eventBus;
    }

    @Override
    public void close() {
        this.compositeTerminable.closeAndReportException();
    }

    private static final class ServerImpl implements Server {
        private static final long TIME_SYNC_THRESHOLD = TimeUnit.SECONDS.toMillis(2);

        private final String id;

        private long lastPing = 0;
        private Set<String> groups = ImmutableSet.of();
        private Map<UUID, Profile> players = ImmutableMap.of();
        private int maxPlayers = 0;
        private boolean whitelisted = false;
        private Map<String, JsonElement> metadata;

        private final Cooldown timeSyncWarningCooldown = Cooldown.of(5, TimeUnit.SECONDS);

        ServerImpl(String id) {
            this.id = id;
        }

        private void checkTimeSync(long messageTimestamp) {
            long systemTime = System.currentTimeMillis();
            long timeDifference = Math.abs(systemTime - messageTimestamp);
            if (timeDifference > TIME_SYNC_THRESHOLD && timeSyncWarningCooldown.test()) {
                LoaderUtils.getPlugin().getLogger().warning(
                        "[network] Server '" + id + "' appears to have a system time difference of " + timeDifference + " ms. " +
                                "time now = " + systemTime + ", " +
                                "message timestamp = " + messageTimestamp + " - " +
                                "Check NTP is running? Is network stable?"
                );
            }
        }

        private void loadData(StatusMessage msg) {
            checkTimeSync(msg.time);

            this.lastPing = msg.time;
            this.groups = ImmutableSet.copyOf(msg.groups);

            ImmutableMap.Builder<UUID, Profile> players = ImmutableMap.builder();
            for (Map.Entry<UUID, String> p : msg.players.entrySet()) {
                players.put(p.getKey(), Profile.create(p.getKey(), p.getValue()));
            }
            this.players = players.build();
            this.maxPlayers = msg.maxPlayers;
            this.whitelisted = msg.whitelisted;
            this.metadata = ImmutableMap.copyOf(msg.metadata);
        }

        @NonNull
        @Override
        public String getId() {
            return this.id;
        }

        @NonNull
        @Override
        public Set<String> getGroups() {
            return this.groups;
        }

        @Override
        public boolean isOnline() {
            long diff = System.currentTimeMillis() - this.lastPing;
            return diff < TimeUnit.SECONDS.toMillis(5);
        }

        @Override
        public long getLastPing() {
            return this.lastPing;
        }

        @Override
        public Map<UUID, Profile> getOnlinePlayers() {
            if (!isOnline()) {
                return ImmutableMap.of();
            }
            return this.players;
        }

        @Override
        public int getMaxPlayers() {
            if (!isOnline()) {
                return 0;
            }
            return this.maxPlayers;
        }

        @Override
        public boolean isWhitelisted() {
            return this.whitelisted;
        }

        @Override
        public Map<String, JsonElement> getRawMetadata() {
            if (!isOnline()) {
                return ImmutableMap.of();
            }
            return this.metadata;
        }
    }

    private static final class StatusMessage {
        private String id;
        private List<String> groups;
        private long time;
        private Map<UUID, String> players;
        private int maxPlayers;
        private boolean whitelisted;
        private Map<String, JsonElement> metadata;
    }

    private static final class EventMessage {
        private String id;
        private String type;
        private String reason; // only used by disconnect
        private StatusMessage status; // only used by connect
    }
}
