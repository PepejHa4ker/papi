package com.pepej.papi.messaging.conversation;

import com.pepej.papi.messaging.codec.Codec;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.UUID;

/**
 * Represents a message sent via a {@link ConversationChannel}.
 *
 * <p>The conversation id should be serialised by the messages {@link Codec}.</p>
 */
public interface ConversationMessage {

    /**
     * Gets the ID of the conversation
     *
     * @return the conversation id
     */
    @NonNull
    UUID getConversationId();

}