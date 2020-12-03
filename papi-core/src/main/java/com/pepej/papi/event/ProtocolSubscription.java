package com.pepej.papi.event;

import com.comphenix.protocol.PacketType;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Set;

/**
 * Represents a subscription to a set of packet events.
 */
public interface ProtocolSubscription extends Subscription {

    /**
     * Gets the packet types handled by this subscription.
     *
     * @return the types
     */
    @NonNull
    Set<PacketType> getPackets();

}
