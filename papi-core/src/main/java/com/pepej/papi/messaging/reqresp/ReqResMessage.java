package com.pepej.papi.messaging.reqresp;

import com.pepej.papi.messaging.conversation.ConversationMessage;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.objectmapping.meta.Required;

import java.util.UUID;

/**
 * A {@link ConversationMessage} used by the {@link ReqRespChannel}.
 *
 * @param <T> the body type
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class ReqResMessage<T> implements ConversationMessage {
    private final UUID id;
    @Getter
    private final T body;

    @NonNull
    @Override
    public UUID getConversationId() {
        return this.id;
    }
}