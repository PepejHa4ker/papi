package com.pepej.papi.messaging.conversation;

import javax.annotation.Nonnull;
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
    @Nonnull
    UUID getConversationId();

}