package com.pepej.papi.network.event;

import com.pepej.papi.network.Server;

import java.util.Objects;

/**
 * Called when a status update is received for a server.
 */
public class ServerStatusEvent implements NetworkEvent {
    private final Server server;

    public ServerStatusEvent(Server server) {
        Objects.requireNonNull(server, "server");
        this.server = server;
    }

    /**
     * Gets the server.
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
        ServerStatusEvent that = (ServerStatusEvent) o;
        return this.server.equals(that.server);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.server);
    }

    @Override
    public String toString() {
        return "ServerStatusEvent{server=" + this.server + '}';
    }
}
