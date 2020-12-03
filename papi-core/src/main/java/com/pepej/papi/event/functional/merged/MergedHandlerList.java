package com.pepej.papi.event.functional.merged;

import com.pepej.papi.event.MergedSubscription;
import com.pepej.papi.event.functional.FunctionalHandlerList;
import com.pepej.papi.utils.Delegates;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface MergedHandlerList<T> extends FunctionalHandlerList<T, MergedSubscription<T>> {

    @NonNull
    @Override
    default MergedHandlerList<T> consumer(@NonNull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @NonNull
    @Override
    MergedHandlerList<T> biConsumer(@NonNull BiConsumer<MergedSubscription<T>, ? super T> handler);
}
