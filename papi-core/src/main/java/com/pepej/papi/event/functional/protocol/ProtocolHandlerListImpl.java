package com.pepej.papi.event.functional.protocol;

import com.comphenix.protocol.events.PacketEvent;
import com.pepej.papi.event.ProtocolSubscription;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

class ProtocolHandlerListImpl implements ProtocolHandlerList {
    private final ProtocolSubscriptionBuilderImpl builder;
    private final List<BiConsumer<ProtocolSubscription, ? super PacketEvent>> handlers = new ArrayList<>(1);

    ProtocolHandlerListImpl(@NonNull ProtocolSubscriptionBuilderImpl builder) {
        this.builder = builder;
    }

    @NonNull
    @Override
    public ProtocolHandlerList biConsumer(@NonNull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @NonNull
    @Override
    public ProtocolSubscription register() {
        return new PapiProtocolListener(builder, handlers);
    }
}
