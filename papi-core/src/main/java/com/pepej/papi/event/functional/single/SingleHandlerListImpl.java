package com.pepej.papi.event.functional.single;

import com.pepej.papi.event.SingleSubscription;
import com.pepej.papi.internal.LoaderUtils;
import org.bukkit.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

class SingleHandlerListImpl<T extends Event> implements SingleHandlerList<T> {
    private final SingleSubscriptionBuilderImpl<T> builder;
    private final List<BiConsumer<SingleSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

    SingleHandlerListImpl(@NonNull SingleSubscriptionBuilderImpl<T> builder) {
        this.builder = builder;
    }

    @NonNull
    @Override
    public SingleHandlerList<T> biConsumer(@NonNull BiConsumer<SingleSubscription<T>, ? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @NonNull
    @Override
    public SingleSubscription<T> register() {
        if (this.handlers.isEmpty()) {
            throw new IllegalStateException("No handlers have been registered");
        }

        PapiSingleEventListener<T> listener = new PapiSingleEventListener<>(this.builder, this.handlers);
        listener.register(LoaderUtils.getPlugin());
        return listener;
    }
}
