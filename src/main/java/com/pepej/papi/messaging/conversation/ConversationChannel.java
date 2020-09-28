package com.pepej.papi.messaging.conversation;

import com.pepej.papi.messaging.Channel;
import com.pepej.papi.promise.Promise;
import com.pepej.papi.terminable.Terminable;

import javax.annotation.Nonnull;
import java.util.concurrent.TimeUnit;

/**
 * An extension of {@link Channel} providing an abstraction for two-way "conversations".
 *
 * @param <T> the outgoing message type
 * @param <R> the reply message type
 */
public interface ConversationChannel<T extends ConversationMessage, R extends ConversationMessage> extends Terminable {

    /**
     * Gets the name of the channel.
     *
     * @return the channel name
     */
    @Nonnull
    String getName();

    /**
     * Gets the channel for primary outgoing messages.
     *
     * @return the outgoing channel
     */
    @Nonnull
    Channel<T> getOutgoingChannel();

    /**
     * Gets the channel replies are sent on.
     *
     * @return the reply channel
     */
    @Nonnull
    Channel<R> getReplyChannel();

    /**
     * Creates a new {@link ConversationChannelAgent} for this channel.
     *
     * @return a new channel agent.
     */
    @Nonnull
    ConversationChannelAgent<T, R> newAgent();

    /**
     * Creates a new {@link ConversationChannelAgent} for this channel, and
     * immediately adds the given {@link ConversationChannelListener} to it.
     *
     * @param listener the listener to register
     * @return the resultant agent
     */
    @Nonnull
    default ConversationChannelAgent<T, R> newAgent(ConversationChannelListener<T, R> listener) {
        ConversationChannelAgent<T, R> agent = newAgent();
        agent.addListener(listener);
        return agent;
    }

    /**
     * Sends a new message to the channel.
     *
     * <p>This method will return immediately, and the promise will be completed
     * once the message has been sent.</p>
     *
     * @param message the message to dispatch
     * @param replyListener the reply listener
     * @param timeoutDuration the timeout duration for the reply listener
     * @param unit the unit of timeoutDuration
     * @return a promise which will complete when the message has sent.
     */
    @Nonnull
    Promise<Void> sendMessage(@Nonnull T message, @Nonnull ConversationReplyListener<R> replyListener, long timeoutDuration, @Nonnull TimeUnit unit);

    @Override
    void close();
}