package com.pepej.papi.network.event;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

/**
 * Called when a server disconnects from the network
 */
public class ServerDisconnectEvent implements NetworkEvent {
    private final String id;
    private final String reason;

    public ServerDisconnectEvent(String id) {
        this(id, null);
    }

    public ServerDisconnectEvent(String id, String reason) {
        this.id = Objects.requireNonNull(id, "id");
        this.reason = reason;
    }

    /**
     * Gets the id of the server that disconnected.
     *
     * @return the server id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Gets the reason for the disconnection.
     *
     * @return the reason, nullable
     */
    @Nullable
    public String getReason() {
        return this.reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerDisconnectEvent that = (ServerDisconnectEvent) o;
        return this.id.equals(that.id) &&
                Objects.equals(this.reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.reason);
    }

    @Override
    public String toString() {
        return "ServerDisconnectEvent{id=" + this.id + ", reason=" + this.reason + '}';
    }
}
