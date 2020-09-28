package com.pepej.papi.event.functional.single;

import com.pepej.papi.event.SingleSubscription;
import com.pepej.papi.internal.LoaderUtils;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

class SingleHandlerListImpl<T extends Event> implements SingleHandlerList<T> {
    private final SingleSubscriptionBuilderImpl<T> builder;
    private final List<BiConsumer<SingleSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

    SingleHandlerListImpl(@Nonnull SingleSubscriptionBuilderImpl<T> builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public SingleHandlerList<T> biConsumer(@Nonnull BiConsumer<SingleSubscription<T>, ? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @Nonnull
    @Override
    public SingleSubscription<T> register() {
        if (this.handlers.isEmpty()) {
            throw new IllegalStateException("No handlers have been registered");
        }

        PapiEventListener<T> listener = new PapiEventListener<>(this.builder, this.handlers);
        listener.register(LoaderUtils.getPlugin());
        return listener;
    }
}
