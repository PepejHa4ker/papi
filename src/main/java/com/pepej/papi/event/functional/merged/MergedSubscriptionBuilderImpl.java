package com.pepej.papi.event.functional.merged;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.event.MergedSubscription;
import com.pepej.papi.event.functional.ExpiryTestStage;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

class MergedSubscriptionBuilderImpl<T> implements MergedSubscriptionBuilder<T> {
    final TypeToken<T> handledClass;
    final Map<Class<? extends Event>, MergedHandlerMapping<T, ? extends Event>> mappings = new HashMap<>();

    BiConsumer<? super Event, Throwable> exceptionConsumer = DEFAULT_EXCEPTION_CONSUMER;

    final List<Predicate<T>> filters = new ArrayList<>();
    final List<BiPredicate<MergedSubscription<T>, T>> preExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<MergedSubscription<T>, T>> midExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<MergedSubscription<T>, T>> postExpiryTests = new ArrayList<>(0);

    MergedSubscriptionBuilderImpl(TypeToken<T> handledClass) {
        this.handledClass = handledClass;
    }

    @Nonnull
    @Override
    public <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@Nonnull Class<E> eventClass, @Nonnull Function<E, T> function) {
        return bindEvent(eventClass, EventPriority.NORMAL, function);
    }

    @Nonnull
    @Override
    public <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@Nonnull Class<E> eventClass, @Nonnull EventPriority priority, @Nonnull Function<E, T> function) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(function, "function");

        this.mappings.put(eventClass, new MergedHandlerMapping<>(priority, function));
        return this;
    }

    @Nonnull
    @Override
    public MergedSubscriptionBuilder<T> expireIf(@Nonnull BiPredicate<MergedSubscription<T>, T> predicate, @Nonnull ExpiryTestStage... testPoints) {
        Objects.requireNonNull(testPoints, "testPoints");
        Objects.requireNonNull(predicate, "predicate");
        for (ExpiryTestStage testPoint : testPoints) {
            switch (testPoint) {
                case PRE:
                    this.preExpiryTests.add(predicate);
                    break;
                case POST_FILTER:
                    this.midExpiryTests.add(predicate);
                    break;
                case POST_HANDLE:
                    this.postExpiryTests.add(predicate);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown ExpiryTestPoint: " + testPoint);
            }
        }
        return this;
    }

    @Nonnull
    @Override
    public MergedSubscriptionBuilder<T> filter(@Nonnull Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.filters.add(predicate);
        return this;
    }

    @Nonnull
    @Override
    public MergedSubscriptionBuilder<T> exceptionConsumer(@Nonnull BiConsumer<Event, Throwable> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer, "exceptionConsumer");
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    @Nonnull
    @Override
    public MergedHandlerList<T> handlers() {
        if (this.mappings.isEmpty()) {
            throw new IllegalStateException("No mappings were created");
        }

        return new MergedHandlerListImpl<>(this);
    }

}
