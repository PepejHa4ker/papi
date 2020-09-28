package com.pepej.papi.event.functional.merged;

import com.pepej.papi.event.MergedSubscription;
import com.pepej.papi.internal.LoaderUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

class MergedHandlerListImpl<T> implements MergedHandlerList<T> {
    private final MergedSubscriptionBuilderImpl<T> builder;
    private final List<BiConsumer<MergedSubscription<T>, ? super T>> handlers = new ArrayList<>(1);

    MergedHandlerListImpl(@Nonnull MergedSubscriptionBuilderImpl<T> builder) {
        this.builder = builder;
    }

    @Nonnull
    @Override
    public MergedHandlerList<T> biConsumer(@Nonnull BiConsumer<MergedSubscription<T>, ? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        this.handlers.add(handler);
        return this;
    }

    @Nonnull
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
