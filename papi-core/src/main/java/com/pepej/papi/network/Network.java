package com.pepej.papi.network;

import com.pepej.papi.Services;
import com.pepej.papi.event.bus.EventBus;
import com.pepej.papi.messaging.InstanceData;
import com.pepej.papi.messaging.Messenger;
import com.pepej.papi.network.event.NetworkEvent;
import com.pepej.papi.network.metadata.ServerMetadataProvider;
import com.pepej.papi.profiles.Profile;
import com.pepej.papi.terminable.Terminable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Represents the interface for a network.
 */
public interface Network extends Terminable {

    /**
     * Creates a new {@link Network} instance. These should be shared if possible.
     *
     * @param messenger the messenger
     * @param instanceData the instance data
     * @return the new network
     */
    static Network create(Messenger messenger, InstanceData instanceData) {
        return new AbstractNetwork(messenger, instanceData);
    }

    /**
     * Tries to obtain an instance of network from the services manager, falling
     * back to given supplier if one is not already present.
     *
     * @param ifElse the supplier
     * @return the network instance
     */
    static Network obtain(Supplier<Network> ifElse) {
        Network network = Services.get(Network.class).orElse(null);
        if (network == null) {
            network = ifElse.get();
            Services.provide(Network.class, network);
        }
        return network;
    }

    /**
     * Gets the known servers in the network
     *
     * @return the known servers
     */
    Map<String, Server> getServers();

    /**
     * Gets the players known to be online in the network.
     *
     * @return the known online players
     */
    Map<UUID, Profile> getOnlinePlayers();

    /**
     * Gets the overall player count
     *
     * @return the player count
     */
    int getOverallPlayerCount();

    /**
     * Registers a metadata provider for this server with the network.
     *
     * @param metadataProvider the provider
     */
    void registerMetadataProvider(ServerMetadataProvider metadataProvider);

    /**
     * Gets the network event bus.
     *
     * @return the event bus
     */
    EventBus<NetworkEvent> getEventBus();

    @Override
    void close();
}
