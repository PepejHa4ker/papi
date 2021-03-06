package com.pepej.papi.messaging.conversation;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an object listening for replies sent on the conversation channel.
 *
 * @param <R> the reply type
 */
public interface ConversationReplyListener<R extends ConversationMessage> {

    static <R extends ConversationMessage> ConversationReplyListener<R> of(Function<? super R, RegistrationAction> onReply) {
        return new ConversationReplyListener<R>() {
            @NonNull
            @Override
            public RegistrationAction onReply(@NonNull R reply) {
                return onReply.apply(reply);
            }

            @Override
            public void onTimeout(@NonNull List<R> replies) {

            }
        };
    }

    static <R extends ConversationMessage> ConversationReplyListener<R> ofWithTimeout(Function<? super R, RegistrationAction> onReply, Consumer<? super List<R>> repliesConsumer) {
        return new ConversationReplyListener<R>() {
            @NonNull
            @Override
            public RegistrationAction onReply(@NonNull R reply) {
                return onReply.apply(reply);
            }

            @Override
            public void onTimeout(@NonNull List<R> replies) {
                repliesConsumer.accept(replies);
            }
        };
    }

    /**
     * Called when a message is posted to this listener.
     *
     * <p>This method is called asynchronously.</p>
     *
     * @param reply the reply message
     * @return the action to take
     */
    @NonNull
    RegistrationAction onReply(@NonNull R reply);

    /**
     * Called when the listener times out.
     *
     * <p>A listener times out if the "timeout wait period" passes before the listener is
     * unregistered by other means.</p>
     *
     * <p>"unregistered by other means" refers to the listener being stopped after a message was
     * passed to {@link #onReply(ConversationMessage)} and {@link RegistrationAction#STOP_LISTENING} being
     * returned.</p>
     *
     * @param replies the replies which have been received
     */
    void onTimeout(@NonNull List<R> replies);

    /**
     * Defines the actions to take after receiving a reply in a {@link ConversationReplyListener}.
     */
    enum RegistrationAction {

        /**
         * Marks that the listener should continue listening for replies
         */
        CONTINUE_LISTENING,

        /**
         * Marks that the listener should stop listening for replies
         */
        STOP_LISTENING

    }
}