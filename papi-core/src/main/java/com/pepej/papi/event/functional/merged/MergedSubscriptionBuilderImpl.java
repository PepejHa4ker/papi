package com.pepej.papi.event.functional.merged;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.event.MergedSubscription;
import com.pepej.papi.event.functional.ExpiryTestStage;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

class MergedSubscriptionBuilderImpl<T> implements MergedSubscriptionBuilder<T> {
    final TypeToken<T> handledClass;
    final Map<Class<? extends Event>, MergedHandlerMapping<T, ? extends Event>> mappings = new HashMap<>();

    BiConsumer<? super Event, Exception> exceptionConsumer = DEFAULT_EXCEPTION_CONSUMER;

    final List<Predicate<T>> filters = new ArrayList<>();
    final List<BiPredicate<MergedSubscription<T>, T>> preExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<MergedSubscription<T>, T>> midExpiryTests = new ArrayList<>(0);
    final List<BiPredicate<MergedSubscription<T>, T>> postExpiryTests = new ArrayList<>(0);

    MergedSubscriptionBuilderImpl(TypeToken<T> handledClass) {
        this.handledClass = handledClass;
    }

    @NonNull
    @Override
    public <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@NonNull Class<E> eventClass, @NonNull Function<E, T> function) {
        return bindEvent(eventClass, EventPriority.NORMAL, function);
    }

    @NonNull
    @Override
    public <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@NonNull Class<E> eventClass, @NonNull EventPriority priority, @NonNull Function<E, T> function) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(function, "function");

        this.mappings.put(eventClass, new MergedHandlerMapping<>(priority, function));
        return this;
    }

    @NonNull
    @Override
    public MergedSubscriptionBuilder<T> expireIf(@NonNull BiPredicate<MergedSubscription<T>, T> predicate, @NonNull ExpiryTestStage... testPoints) {
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

    @NonNull
    @Override
    public MergedSubscriptionBuilder<T> filter(@NonNull Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.filters.add(predicate);
        return this;
    }

    @NonNull
    @Override
    public MergedSubscriptionBuilder<T> filterNot(@NonNull Predicate<T> predicate) {
        Objects.requireNonNull(predicate, "predicate");
        this.filters.add(predicate.negate());
        return this;
    }

    @NonNull
    @Override
    public MergedSubscriptionBuilder<T> exceptionConsumer(@NonNull BiConsumer<Event, Exception> exceptionConsumer) {
        Objects.requireNonNull(exceptionConsumer, "exceptionConsumer");
        this.exceptionConsumer = exceptionConsumer;
        return this;
    }

    @NonNull
    @Override
    public MergedHandlerList<T> handlers() {
        if (this.mappings.isEmpty()) {
            throw new IllegalStateException("No mappings were created");
        }

        return new MergedHandlerListImpl<>(this);
    }

}
