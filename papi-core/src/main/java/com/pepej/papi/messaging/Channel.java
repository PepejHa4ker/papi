package com.pepej.papi.messaging;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.interfaces.TypeAware;
import com.pepej.papi.messaging.codec.Codec;
import com.pepej.papi.promise.Promise;
import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * Represents an individual messaging channel.
 *
 * <p>Channels can be subscribed to through a {@link ChannelAgent}.</p>
 *
 * @param <T> the channel message type
 */
public interface Channel<T> extends TypeAware<T> {

    /**
     * Gets the name of the channel.
     *
     * @return the channel name
     */
    @NonNull
    String getName();

    /**
     * Gets the channels message type.
     *
     * @return the channels message type.
     */
    @Override
    @NonNull
    TypeToken<T> getType();

    /**
     * Gets the channels codec.
     *
     * @return the codec
     */
    @NonNull
    Codec<T> getCodec();

    /**
     * Creates a new {@link ChannelAgent} for this channel.
     *
     * @return a new channel agent.
     */
    @NonNull
    ChannelAgent<T> newAgent();

    /**
     * Creates a new {@link ChannelAgent} for this channel, and immediately
     * adds the given {@link ChannelListener} to it.
     *
     * @param listener the listener to register
     * @return the resultant agent
     */
    @NonNull
    default ChannelAgent<T> newAgent(ChannelListener<T> listener) {
        ChannelAgent<T> agent = newAgent();
        agent.addListener(listener);
        return agent;
    }

    /**
     * Sends a new message to the channel.
     *
     * <p>This method will return immediately, and the future will be completed
     * once the message has been sent.</p>
     *
     * @param message the message to dispatch
     * @return a promise which will complete when the message has sent.
     */
    @NonNull
    Promise<Void> sendMessage(@NonNull T message);

}
