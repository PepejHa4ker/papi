package com.pepej.papi.event.functional.single;

import com.google.common.base.Preconditions;
import com.pepej.papi.event.SingleSubscription;
import com.pepej.papi.event.functional.ExpiryTestStage;
import com.pepej.papi.event.functional.SubscriptionBuilder;
import com.pepej.papi.utils.Delegates;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Functional builder for {@link SingleSubscription}s.
 *
 * @param <T> the event type
 */
public interface SingleSubscriptionBuilder<T extends Event> extends SubscriptionBuilder<T> {

    /**
     * Makes a HandlerBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass is null
     */
    @NonNull
    static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@NonNull Class<T> eventClass) {
        return newBuilder(eventClass, EventPriority.NORMAL);
    }

    /**
     * Makes a HandlerBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param priority   the priority to listen at
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass or priority is null
     */
    @NonNull
    static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@NonNull Class<T> eventClass, @NonNull EventPriority priority) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        return new SingleSubscriptionBuilderImpl<>(eventClass, priority);
    }

    // override return type - we return SingleSubscriptionBuilder, not SubscriptionBuilder

    @NonNull
    @Override
    default SingleSubscriptionBuilder<T> expireIf(@NonNull Predicate<T> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @NonNull
    @Override
    default SingleSubscriptionBuilder<T> expireAfter(long duration, @NonNull TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @NonNull
    @Override
    default SingleSubscriptionBuilder<T> expireAfter(long maxCalls) {
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
    SingleSubscriptionBuilder<T> filter(@NonNull Predicate<T> predicate);

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
    SingleSubscriptionBuilder<T> filterNot(@NonNull Predicate<T> predicate);

    /**
     * @param state the state
     * @return the builder instance
     */
    @NonNull
    SingleSubscriptionBuilder<T> ignoreCancelled(boolean state);

    /**
     * Add a expiry predicate.
     *
     * @param predicate the expiry test
     * @param testPoints when to test the expiry predicate
     * @return ths builder instance
     */
    @NonNull
    SingleSubscriptionBuilder<T> expireIf(@NonNull BiPredicate<SingleSubscription<T>, T> predicate, @NonNull ExpiryTestStage... testPoints);

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
    SingleSubscriptionBuilder<T> exceptionConsumer(@NonNull BiConsumer<? super T, Exception> consumer);

    /**
     * Sets that the handler should accept subclasses of the event type.
     *
     * @return the builder instance
     */
    @NonNull
    SingleSubscriptionBuilder<T> handleSubclasses();

    /**
     * Return the handler list builder to append handlers for the event.
     *
     * @return the handler list
     */
    @NonNull
    SingleHandlerList<T> handlers();

    /**
     * Builds and registers the Handler.
     *
     * @param handler the consumer responsible for handling the event.
     * @return a registered {@link SingleSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @NonNull
    default SingleSubscription<T> handler(@NonNull Consumer<? super T> handler) {
        return handlers().consumer(handler).register();
    }

    /**
     * Builds and registers the Handler.
     *
     * @param handler the bi-consumer responsible for handling the event.
     * @return a registered {@link SingleSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @NonNull
    default SingleSubscription<T> biHandler(@NonNull BiConsumer<SingleSubscription<T>, ? super T> handler) {
        return handlers().biConsumer(handler).register();
    }

}
