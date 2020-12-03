package com.pepej.papi.event.functional.single;

import com.pepej.papi.event.SingleSubscription;
import com.pepej.papi.event.functional.FunctionalHandlerList;
import com.pepej.papi.utils.Delegates;
import org.bukkit.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SingleHandlerList<T extends Event> extends FunctionalHandlerList<T, SingleSubscription<T>> {

    @NonNull
    @Override
    default SingleHandlerList<T> consumer(@NonNull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @NonNull
    @Override
    SingleHandlerList<T> biConsumer(@NonNull BiConsumer<SingleSubscription<T>, ? super T> handler);
}
