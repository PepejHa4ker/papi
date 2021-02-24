package com.pepej.papi.event.functional.protocol;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.pepej.papi.event.ProtocolSubscription;
import com.pepej.papi.event.functional.ExpiryTestStage;
import com.pepej.papi.event.functional.SubscriptionBuilder;
import com.pepej.papi.utils.Delegates;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Functional builder for {@link ProtocolSubscription}s.
 */
public interface ProtocolSubscriptionBuilder extends SubscriptionBuilder<PacketEvent> {

    /**
     * Makes a HandlerBuilder for the given packets
     *
     * @param packets the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    static ProtocolSubscriptionBuilder newBuilder(@NonNull PacketType... packets) {
        return newBuilder(ListenerPriority.NORMAL, packets);
    }

    /**
     * Makes a HandlerBuilder for the given packets
     *
     * @param priority the priority to listen at
     * @param packets  the packets to handle
     * @return a {@link ProtocolSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    static ProtocolSubscriptionBuilder newBuilder(@NonNull ListenerPriority priority, @NonNull PacketType... packets) {
        Objects.requireNonNull(priority, "priority");
        Objects.requireNonNull(packets, "packets");
        return new ProtocolSubscriptionBuilderImpl(ImmutableSet.copyOf(packets), priority);
    }

    // override return type - we return SingleSubscriptionBuilder, not SubscriptionBuilder

    @NonNull
    @Override
    default ProtocolSubscriptionBuilder expireIf(@NonNull Predicate<PacketEvent> predicate) {
        return expireIf(Delegates.predicateToBiPredicateSecond(predicate), ExpiryTestStage.PRE, ExpiryTestStage.POST_HANDLE);
    }

    @NonNull
    @Override
    default ProtocolSubscriptionBuilder expireAfter(long duration, @NonNull TimeUnit unit) {
        Objects.requireNonNull(unit, "unit");
        Preconditions.checkArgument(duration >= 1, "duration < 1");
        long expiry = Math.addExact(System.currentTimeMillis(), unit.toMillis(duration));
        return expireIf((handler, event) -> System.currentTimeMillis() > expiry, ExpiryTestStage.PRE);
    }

    @NonNull
    @Override
    default ProtocolSubscriptionBuilder expireAfter(long maxCalls) {
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
    ProtocolSubscriptionBuilder filter(@NonNull Predicate<PacketEvent> predicate);

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
    ProtocolSubscriptionBuilder filterNot(@NonNull Predicate<PacketEvent> predicate);


    /**
     * Add a expiry predicate.
     *
     * @param predicate  the expiry test
     * @param testPoints when to test the expiry predicate
     * @return ths builder instance
     */
    @NonNull
    ProtocolSubscriptionBuilder expireIf(@NonNull BiPredicate<ProtocolSubscription, PacketEvent> predicate, @NonNull ExpiryTestStage... testPoints);

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
    ProtocolSubscriptionBuilder exceptionConsumer(@NonNull BiConsumer<? super PacketEvent, Throwable> consumer);

    /**
     * Return the handler list builder to append handlers for the event.
     *
     * @return the handler list
     */
    @NonNull
    ProtocolHandlerList handlers();

    /**
     * Builds and registers the Handler.
     *
     * @param handler the consumer responsible for handling the event.
     * @return a registered {@link ProtocolSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @NonNull
    default ProtocolSubscription handler(@NonNull Consumer<? super PacketEvent> handler) {
        return handlers().consumer(handler).register();
    }

    /**
     * Builds and registers the Handler.
     *
     * @param handler the bi-consumer responsible for handling the event.
     * @return a registered {@link ProtocolSubscription} instance.
     * @throws NullPointerException if the handler is null
     */
    @NonNull
    default ProtocolSubscription biHandler(@NonNull BiConsumer<ProtocolSubscription, ? super PacketEvent> handler) {
        return handlers().biConsumer(handler).register();
    }

}
