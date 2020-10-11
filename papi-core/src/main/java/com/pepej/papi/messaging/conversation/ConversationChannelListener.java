package com.pepej.papi.messaging.conversation;

import javax.annotation.Nonnull;

/**
 * Represents an object listening to messages sent on the conversation channel.
 *
 * @param <T> the channel message type
 */
@FunctionalInterface
public interface ConversationChannelListener<T extends ConversationMessage, R extends ConversationMessage> {

    /**
     * Called when a message is posted to this listener.
     *
     * <p>This method is called asynchronously.</p>
     *
     * @param agent the agent which forwarded the message.
     * @param message the message
     */
    ConversationReply<R> onMessage(@Nonnull ConversationChannelAgent<T, R> agent, @Nonnull T message);

}
