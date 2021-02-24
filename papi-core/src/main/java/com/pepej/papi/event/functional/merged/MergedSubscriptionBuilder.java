package com.pepej.papi.event.functional.merged;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;
import com.pepej.papi.event.MergedSubscription;
import com.pepej.papi.event.functional.ExpiryTestStage;
import com.pepej.papi.event.functional.SubscriptionBuilder;
import com.pepej.papi.utils.Delegates;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.*;

/**
 * Functional builder for {@link MergedSubscription}s.
 *
 * @param <T> the handled type
 */
public interface MergedSubscriptionBuilder<T> extends SubscriptionBuilder<T> {

    /**
     * Makes a MergedHandlerBuilder for a given super type
     *
     * @param handledClass the super type of the event handler
     * @param <T>          the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    static <T> MergedSubscriptionBuilder<T> newBuilder(@NonNull Class<T> handledClass) {
        Objects.requireNonNull(handledClass, "handledClass");
        return new MergedSubscriptionBuilderImpl<>(TypeToken.of(handledClass));
    }

    /**
     * Makes a MergedHandlerBuilder for a given super type
     *
     * @param type the super type of the event handler
     * @param <T>  the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    static <T> MergedSubscriptionBuilder<T> newBuilder(@NonNull TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        return new MergedSubscriptionBuilderImpl<>(type);
    }

    /**
     * Makes a MergedHandlerBuilder for a super event class
     *
     * @param superClass   the abstract super event class
     * @param eventClasses the event classes to be bound to
     * @param <S>          the super class type
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    @SafeVarargs
    static <S extends Event> MergedSubscriptionBuilder<S> newBuilder(@NonNull Class<S> superClass, @NonNull Class<? extends S>... eventClasses) {
        return newBuilder(superClass, EventPriority.NORMAL, eventClasses);
    }

    /**
     * Makes a MergedHandlerBuilder for a super event class
     *
     * @param superClass   the abstract super event class
     * @param priority     the priority to listen at
     * @param eventClasses the event classes to be bound to
     * @param <S>          the super class type
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    @SafeVarargs
    static <S extends Event> MergedSubscriptionBuilder<S> newBuilder(@NonNull Class<S> superClass, @NonNull EventPriority priority, @NonNull Class<? extends S>... eventClasses) {
        Objects.requireNonNull(superClass, "superClass");
        Objects.requireNonNull(eventClasses, "eventClasses");
        Objects.requireNonNull(priority, "priority");
        if (eventClasses.length < 2) {
            throw new IllegalArgumentException("merge method used for only one subclass");
        }

        MergedSubscriptionBuilderImpl<S> h = new MergedSubscriptionBuilderImpl<>(TypeToken.of(superClass));
        for (Class<? extends S> clazz : eventClasses) {
            h.bindEvent(clazz, priority, e -> e);
        }
        return h;
    }

    // override return type - we return MergedSubscriptionBuilder, not SubscriptionBuilder

    @NonNull
    @Override
    default MergedSubscriptionBuilder<T> expireIf(@NonNull Predicate<T> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @NonNull
    @Override
    default MergedSubscriptionBuilder<T> expireAfter(long duration, @NonNull TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @NonNull
    @Override
    default MergedSubscriptionBuilder<T> expireAfter(long maxCalls) {
        Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
        return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    /**
     * Adds a filter to the handler.
     *
     * <p>An event will only be handled if it passes all filters. Filters are evaluated in the order they are
     * registered.
     *
     * @param predicate the filter
     * @return the builder instance
     */
    @NonNull
    @Override
    MergedSubscriptionBuilder<T> filter(@NonNull Predicate<T> predicate);


    /**
     * Adds a filter to the handler.
     *
     * <p>An event will only be handled if it not passes all filters. Filters are evaluated in the order they are
     * registered.
     *
     * @param predicate the filter
     * @return the builder instance
     */
    @NonNull
    @Override
    MergedSubscriptionBuilder<T> filterNot(@NonNull Predicate<T> predicate);


    /**
     * Add a expiry predicate.
     *
     * @param predicate  the expiry test
     * @param testPoints when to test the expiry predicate
     * @return ths builder instance
     */
    @NonNull
    MergedSubscriptionBuilder<T> expireIf(@NonNull BiPredicate<MergedSubscription<T>, T> predicate, @NonNull ExpiryTestStage... testPoints);

    /**
     * Binds this handler to an event
     *
     * @param eventClass the event class to bind to
     * @param function   the function to remap the event
     * @param <E>        the event class
     * @return the builder instance
     */
    @NonNull <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@NonNull Class<E> eventClass, @NonNull Function<E, T> function);

    /**
     * Binds this handler to an event
     *
     * @param eventClass the event class to bind to
     * @param priority   the priority to listen at
     * @param function   the function to remap the event
     * @param <E>        the event class
     * @return the builder instance
     */
    @NonNull <E extends Event> MergedSubscriptionBuilder<T> bindEvent(@NonNull Class<E> eventClass, @NonNull EventPriority priority, @NonNull Function<E, T> function);

    /**
     * Sets the exception consumer for the handler.
     *
     * <p> If an exception is thrown in the handler, it is passed to this consumer to be swallowed.
     *
     * @param consumer the consumer
     * @return the builder instance
     * @throws NullPointerException if the consumer is null
     */
    @NonNull
    MergedSubscriptionBuilder<T> exceptionConsumer(@NonNull BiConsumer<Event, Throwable> consumer);

    /**
     * Return the handler list builder to append handlers for the event.
     *
     * @return the handler list
     */
    @NonNull
    MergedHandlerList<T> handlers();

    /**
     * Builds and registers the Handler.
     *
     * @param handler the consumer responsible for handling the event.
     * @return a registered {@link MergedSubscription} instance.
     * @throws NullPointerException  if the handler is null
     * @throws IllegalStateException if no events have been bound to
     */
    @NonNull
    default MergedSubscription<T> handler(@NonNull Consumer<? super T> handler) {
        return handlers().consumer(handler).register();
    }

    /**
     * Builds and registers the Handler.
     *
     * @param handler the bi-consumer responsible for handling the event.
     * @return a registered {@link MergedSubscription} instance.
     * @throws NullPointerException  if the handler is null
     * @throws IllegalStateException if no events have been bound to
     */
    @NonNull
    default MergedSubscription<T> biHandler(@NonNull BiConsumer<MergedSubscription<T>, ? super T> handler) {
        return handlers().biConsumer(handler).register();
    }

}
