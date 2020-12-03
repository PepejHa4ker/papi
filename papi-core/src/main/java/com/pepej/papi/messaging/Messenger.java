package com.pepej.papi.messaging;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.messaging.conversation.ConversationChannel;
import com.pepej.papi.messaging.conversation.ConversationMessage;
import com.pepej.papi.messaging.conversation.SimpleConversationChannel;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

/**
 * Represents an object which manages messaging {@link Channel}s.
 */
public interface Messenger {

    /**
     * Gets a channel by name.
     *
     * @param name the name of the channel.
     * @param type the channel message typetoken
     * @param <T> the channel message type
     * @return a channel
     */
    @NonNull
    <T> Channel<T> getChannel(@NonNull String name, @NonNull TypeToken<T> type);


    /**
     * Gets a conversation channel by name.
     *
     * @param name the name of the channel
     * @param type the channel outgoing message typetoken
     * @param replyType the channel incoming (reply) message typetoken
     * @param <T> the channel message type
     * @param <R> the channel reply type
     * @return a conversation channel
     */
    @NonNull
    default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(@NonNull String name, @NonNull TypeToken<T> type, @NonNull TypeToken<R> replyType) {
        return new SimpleConversationChannel<>(this, name, type, replyType);
    }

    /**
     * Gets a channel by name.
     *
     * @param name the name of the channel.
     * @param clazz the channel message class
     * @param <T> the channel message type
     * @return a channel
     */
    @NonNull
    default <T> Channel<T> getChannel(@NonNull String name, @NonNull Class<T> clazz) {
        return getChannel(name, TypeToken.of(Objects.requireNonNull(clazz)));
    }

    /**
     * Gets a conversation channel by name.
     *
     * @param name the name of the channel
     * @param clazz the channel outgoing message class
     * @param replyClazz the channel incoming (reply) message class
     * @param <T> the channel message type
     * @param <R> the channel reply type
     * @return a conversation channel
     */
    @NonNull
    default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(@NonNull String name, @NonNull Class<T> clazz, @NonNull Class<R> replyClazz) {
        return getConversationChannel(name, TypeToken.of(Objects.requireNonNull(clazz)), TypeToken.of(Objects.requireNonNull(replyClazz)));
    }

}
