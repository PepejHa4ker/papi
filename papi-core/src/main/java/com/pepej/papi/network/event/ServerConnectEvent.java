package com.pepej.papi.network.event;

import com.pepej.papi.network.Server;

import java.util.Objects;

/**
 * Called when a server connects to the network
 */
public class ServerConnectEvent implements NetworkEvent {
    private final String id;
    private final Server server;

    public ServerConnectEvent(String id, Server server) {
        this.id = Objects.requireNonNull(id, "id");
        this.server = Objects.requireNonNull(server, "server");
    }

    /**
     * Gets the id of the server that connected.
     *
     * @return the server id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the server that connected.
     *
     * @return the server
     */
    public Server getServer() {
        return this.server;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerConnectEvent that = (ServerConnectEvent) o;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "ServerConnectEvent{id=" + this.id + '}';
    }
}
