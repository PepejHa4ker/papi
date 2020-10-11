package com.pepej.papi.event.functional.protocol;

import com.comphenix.protocol.events.PacketEvent;
import com.pepej.papi.event.ProtocolSubscription;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

class ProtocolHandlerListImpl implements ProtocolHandlerList {
    private final ProtocolSubscriptionBuilderImpl builder;
    private final List<BiConsumer<ProtocolSubscription, ? super PacketEvent>> handlers = new ArrayList<>(1);

    ProtocolHandlerListImpl(@Nonnull ProtocolSubscriptionBuilderImpl builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public ProtocolHandlerList biConsumer(@Nonnull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @Nonnull
    @Override
    public ProtocolSubscription register() {
        return new PapiProtocolListener(builder, handlers);
    }
}
