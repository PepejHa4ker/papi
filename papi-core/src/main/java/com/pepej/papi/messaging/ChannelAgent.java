package com.pepej.papi.messaging;

import com.pepej.papi.terminable.Terminable;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Represents an agent for interacting with a {@link Channel}s message streams.
 *
 * @param <T> the channel message type
 */
public interface ChannelAgent<T> extends Terminable {

    /**
     * Gets the channel this agent is acting for.
     *
     * @return the parent channel
     */
    @Nonnull
    Channel<T> getChannel();

    /**
     * Gets an immutable copy of the listeners currently held by this agent.
     *
     * @return the active listeners
     */
    @Nonnull
    Set<ChannelListener<T>> getListeners();

    /**
     * Gets if this agent has any active listeners.
     *
     * @return true if this agent has listeners
     */
    boolean hasListeners();

    /**
     * Adds a new listener to the channel;
     *
     * @param listener the listener to add
     * @return true if successful
     */
    boolean addListener(@Nonnull ChannelListener<T> listener);

    /**
     * Removes a listener from the channel.
     *
     * @param listener the listener to remove
     * @return true if successful
     */
    boolean removeListener(@Nonnull ChannelListener<T> listener);

    @Override
    void close();
}
