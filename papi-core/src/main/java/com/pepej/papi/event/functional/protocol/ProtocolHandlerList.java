package com.pepej.papi.event.functional.protocol;

import com.comphenix.protocol.events.PacketEvent;
import com.pepej.papi.event.ProtocolSubscription;
import com.pepej.papi.event.functional.FunctionalHandlerList;
import com.pepej.papi.utils.Delegates;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface ProtocolHandlerList extends FunctionalHandlerList<PacketEvent, ProtocolSubscription> {

    @NonNull
    @Override
    default ProtocolHandlerList consumer(@NonNull Consumer<? super PacketEvent> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @NonNull
    @Override
    ProtocolHandlerList biConsumer(@NonNull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler);

}
