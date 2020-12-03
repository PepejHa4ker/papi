package com.pepej.papi.event.functional.merged;

import com.pepej.papi.event.MergedSubscription;
import com.pepej.papi.internal.LoaderUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

class MergedHandlerListImpl<T> implements MergedHandlerList<T> {
    private final MergedSubscriptionBuilderImpl<T> builder;
    private final List<BiConsumer<MergedSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

    MergedHandlerListImpl(@NonNull MergedSubscriptionBuilderImpl<T> builder) {
        this.builder = builder;
    }

    @NonNull
    @Override
    public MergedHandlerList<T> biConsumer(@NonNull BiConsumer<MergedSubscription<T>, ? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @NonNull
    @Override
    public MergedSubscription<T> register() {
        if (this.handlers.isEmpty()) {
            throw new IllegalStateException("No handlers have been registered");
        }

        PapiMergedEventListener<T> listener = new PapiMergedEventListener<>(this.builder, this.handlers);
        listener.register(LoaderUtils.getPlugin());
        return listener;
    }
}
