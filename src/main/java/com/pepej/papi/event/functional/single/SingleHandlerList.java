package com.pepej.papi.event.functional.single;

import com.pepej.papi.event.SingleSubscription;
import com.pepej.papi.event.functional.FunctionalHandlerList;
import com.pepej.papi.utils.Delegates;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface SingleHandlerList<T extends Event> extends FunctionalHandlerList<T, SingleSubscription<T>> {

    @Nonnull
    @Override
    default SingleHandlerList<T> consumer(@Nonnull Consumer<? super T> handler) {
        Objects.requireNonNull(handler, "handler");
        return biConsumer(Delegates.consumerToBiConsumerSecond(handler));
    }

    @Nonnull
    @Override
    SingleHandlerList<T> biConsumer(@Nonnull BiConsumer<SingleSubscription<T>, ? super T> handler);
}
