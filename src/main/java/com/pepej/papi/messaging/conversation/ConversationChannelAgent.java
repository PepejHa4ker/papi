package com.pepej.papi.messaging.conversation;

import com.pepej.papi.terminable.Terminable;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Represents an agent for interacting with a {@link ConversationChannel}s message streams.
 *
 * @param <T> the channel outgoing message type
 */
public interface ConversationChannelAgent<T extends ConversationMessage, R extends ConversationMessage> extends Terminable {

    /**
     * Gets the channel this agent is acting for.
     *
     * @return the parent channel
     */
    @Nonnull
    ConversationChannel<T, R> getChannel();

    /**
     * Gets an immutable copy of the listeners currently held by this agent.
     *
     * @return the active listeners
     */
    @Nonnull
    Set<ConversationChannelListener<T, R>> getListeners();

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
    boolean addListener(@Nonnull ConversationChannelListener<T, R> listener);

    /**
     * Removes a listener from the channel.
     *
     * @param listener the listener to remove
     * @return true if successful
     */
    boolean removeListener(@Nonnull ConversationChannelListener<T, R> listener);

    @Override
    void close();
}
