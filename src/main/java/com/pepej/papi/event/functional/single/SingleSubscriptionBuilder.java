package com.pepej.papi.event.functional.single;

import com.google.common.base.Preconditions;
import com.pepej.papi.event.SingleSubscription;
import com.pepej.papi.event.functional.ExpiryTestStage;
import com.pepej.papi.event.functional.SubscriptionBuilder;
import com.pepej.papi.utils.Delegates;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import javax.annotation.Nonnull;
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
    @Nonnull
    static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@Nonnull Class<T> eventClass) {
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
    @Nonnull
    static <T extends Event> SingleSubscriptionBuilder<T> newBuilder(@Nonnull Class<T> eventClass, @Nonnull EventPriority priority) {
        Objects.requireNonNull(eventClass, "eventClass");
        Objects.requireNonNull(priority, "priority");
        return new SingleSubscriptionBuilderImpl<>(eventClass, priority);
    }

    // override return type - we return SingleSubscriptionBuilder, not SubscriptionBuilder

    @Nonnull
    @Override
    default SingleSubscriptionBuilder<T> expireIf(@Nonnull Predicate<T> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Nonnull
    @Override
    default SingleSubscriptionBuilder<T> expireAfter(long duration, @Nonnull TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @Nonnull
    @Override
    default SingleSubscriptionBuilder<T> expireAfter(long maxCalls) {
        Preconditions.checkArgument(maxCalls >= 1, "maxCalls < 1");
        return expireIf((handler, event) -> handler.getCallCounter() >= maxCalls, ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @Nonnull
    @Override
    SingleSubscriptionBuilder<T> filter(@Nonnull Predicate<T> predicate);

    /**
     * Add a expiry predicate.
     *
     * @param predicate the expiry test
     * @param testPoints when to test the expiry predicate
     * @return ths builder instance
     */
    @Nonnull
    SingleSubscriptionBuilder<T> expireIf(@Nonnull BiPredicate<SingleSubscription<T>, T> predicate, @Nonnull ExpiryTestStage... testPoints);

    /**
     * Sets the exception consumer for the handler.
     *
     * <p> If an exception is thrown in the handler, it is passed to this consumer to be swallowed.
     *
     * @param consumer the consumer
     * @return the builder instance
     * @throws NullPointerException if the consumer is null
     */
    @Nonnull
    SingleSubscriptionBuilder<T> exceptionConsumer(@Nonnull BiConsumer<? super T, Throwable> consumer);

    /**
     * Sets that the handler should accept subclasses of the event type.
     *
     * @return the builder instance
     */
    @Nonnull
    SingleSubscriptionBuilder<T> handleSubclasses();

    /**
     * Return the handler list builder to append handlers for the event.
     *
     * @return the handler list
     */
    @Nonnull
    SingleHandlerList<T> handlers();

    /**
     * Builds and registers the Handler.
     *
     * @param handler the consumer responsible for handling the event.
     * @return a registered {@link SingleSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @Nonnull
    default SingleSubscription<T> handler(@Nonnull Consumer<? super T> handler) {
        return handlers().consumer(handler).register();
    }

    /**
     * Builds and registers the Handler.
     *
     * @param handler the bi-consumer responsible for handling the event.
     * @return a registered {@link SingleSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @Nonnull
    default SingleSubscription<T> biHandler(@Nonnull BiConsumer<SingleSubscription<T>, ? super T> handler) {
        return handlers().biConsumer(handler).register();
    }

}
