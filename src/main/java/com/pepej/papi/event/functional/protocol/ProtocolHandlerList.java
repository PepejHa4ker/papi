package com.pepej.papi.event.functional.protocol;

import com.comphenix.protocol.events.PacketEvent;
import com.pepej.papi.event.ProtocolSubscription;
import com.pepej.papi.event.functional.FunctionalHandlerList;
import com.pepej.papi.utils.Delegates;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ProtocolHandlerList extends FunctionalHandlerList<PacketEvent, ProtocolSubscription> {

    @Nonnull
    @Override
    default ProtocolHandlerList consumer(@Nonnull Consumer<? super PacketEvent> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Nonnull
    @Override
    ProtocolHandlerList biConsumer(@Nonnull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler);

}
