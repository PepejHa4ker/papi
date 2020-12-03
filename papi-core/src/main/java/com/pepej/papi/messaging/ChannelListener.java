package com.pepej.papi.messaging;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object listening to messages sent on the channel.
 *
 * @param <T> the channel message type
 */
@FunctionalInterface
public interface ChannelListener<T> {

    /**
     * Called when a message is posted to this listener.
     *
     * <p>This method is called asynchronously.</p>
     *
     * @param agent the agent which forwarded the message.
     * @param message the message
     */
    void onMessage(@NonNull ChannelAgent<T> agent, @NonNull T message);

}
