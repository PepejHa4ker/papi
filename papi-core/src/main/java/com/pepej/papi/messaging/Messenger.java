package com.pepej.papi.messaging;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.messaging.conversation.ConversationChannel;
import com.pepej.papi.messaging.conversation.ConversationMessage;
import com.pepej.papi.messaging.conversation.SimpleConversationChannel;
import com.pepej.papi.messaging.reqresp.ReqRespChannel;
import com.pepej.papi.messaging.reqresp.SimpleReqRespChannel;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
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
    @Nonnull
    <T> Channel<T> getChannel(@Nonnull String name, @Nonnull TypeToken<T> type);


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
    default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(@Nonnull String name, @Nonnull TypeToken<T> type, @Nonnull TypeToken<R> replyType) {
        return new SimpleConversationChannel<>(this, name, type, replyType);
    }

    /**
     * Gets a req/resp channel by name.
     *
     * @param name the name of the channel
     * @param reqType the request typetoken
     * @param respType the response typetoken
     * @param <Req> the request type
     * @param <Resp> the response type
     * @return the req/resp channel
     */
    @NonNull
    default <Req, Resp> ReqRespChannel<Req, Resp> getReqRespChannel(@Nonnull String name, @Nonnull TypeToken<Req> reqType, @Nonnull TypeToken<Resp> respType) {
        return new SimpleReqRespChannel<>(this, name, reqType, respType);
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
    default <T> Channel<T> getChannel(@Nonnull String name, @Nonnull Class<T> clazz) {
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
    default <T extends ConversationMessage, R extends ConversationMessage> ConversationChannel<T, R> getConversationChannel(@Nonnull String name, @Nonnull Class<T> clazz, @Nonnull Class<R> replyClazz) {
        return getConversationChannel(name, TypeToken.of(Objects.requireNonNull(clazz)), TypeToken.of(Objects.requireNonNull(replyClazz)));
    }

    /**
     * Gets a req/resp channel by name.
     *
     * @param name the name of the channel
     * @param reqClass the request class
     * @param respClass the response class
     * @param <Req> the request type
     * @param <Resp> the response type
     * @return the req/resp channel
     */
    @NonNull
    default <Req, Resp> ReqRespChannel<Req, Resp> getReqRespChannel(@Nonnull String name, @Nonnull Class<Req> reqClass, @Nonnull Class<Resp> respClass) {
        return getReqRespChannel(name, TypeToken.of(Objects.requireNonNull(reqClass)), TypeToken.of(Objects.requireNonNull(respClass)));
    }
}