package com.pepej.papi.event.functional.merged;

import com.pepej.papi.event.MergedSubscription;
import com.pepej.papi.event.functional.FunctionalHandlerList;
import com.pepej.papi.utils.Delegates;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MergedHandlerList<T> extends FunctionalHandlerList<T, MergedSubscription<T>> {

    @Nonnull
    @Override
    default MergedHandlerList<T> consumer(@Nonnull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Nonnull
    @Override
    MergedHandlerList<T> biConsumer(@Nonnull BiConsumer<MergedSubscription<T>, ? super T> handler);
}
