package com.pepej.papi.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketContainer;
import com.pepej.papi.event.functional.protocol.ProtocolSubscriptionBuilder;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

/**
 * Utilities for working with ProtocolLib.
 */
public final class Protocol {

    /**
     * Makes a HandlerBuilder for the given packets
     *
     * @param packets the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @Nonnull
    public static ProtocolSubscriptionBuilder subscribe(@Nonnull PacketType... packets) {
        return ProtocolSubscriptionBuilder.newBuilder(packets);
    }

    /**
     * Makes a HandlerBuilder for the given packets
     *
     * @param priority   the priority to listen at
     * @param packets the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @Nonnull
    public static ProtocolSubscriptionBuilder subscribe(@Nonnull ListenerPriority priority, @Nonnull PacketType... packets) {
        return ProtocolSubscriptionBuilder.newBuilder(priority, packets);
    }

    /**
     * Gets the protocol manager.
     *
     * @return the protocol manager.
     */
    @Nonnull
    public static ProtocolManager manager() {
        return ProtocolLibrary.getProtocolManager();
    }

    /**
     * Sends a packet to the given player.
     *
     * @param player the player
     * @param packet the packet
     */
    public static void sendPacket(@Nonnull Player player, @Nonnull PacketContainer packet) {
        try {
            manager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a packet to all players connected to the server.
     *
     * @param packet the packet
     */
    public static void broadcastPacket(@Nonnull PacketContainer packet) {
        manager().broadcastServerPacket(packet);
    }

    /**
     * Sends a packet to each of the given players
     *
     * @param players the players
     * @param packet the packet
     */
    public static void broadcastPacket(@Nonnull Iterable<Player> players, @Nonnull PacketContainer packet) {
        for (Player player : players) {
            sendPacket(player, packet);
        }
    }

}
