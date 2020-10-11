package com.pepej.papi.eventbus;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * A functional interface representing an object that can handle a given type of event.
 *
 * @param <E> the event type
 */
@FunctionalInterface
public interface EventSubscriber<E> {
    /**
     * Invokes this event subscriber.
     *
     * <p>Called by the event bus when a new event is "posted" to this subscriber.</p>
     *
     * @param event the event that was posted
     * @throws Throwable any exception thrown during handling
     */
    void invoke(final @Nonnull E event) throws Throwable;

    /**
     * Gets the post order this subscriber should be called at.
     *
     * @return the post order of this subscriber
     * @see PostOrders
     */
    default int postOrder() {
        return PostOrders.NORMAL;
    }

    /**
     * Gets if cancelled events should be posted to this subscriber.
     *
     * @return if cancelled events should be posted
     */
    default boolean consumeCancelledEvents() {
        return true;
    }

    /**
     * Gets the generic type of this subscriber, if it is known.
     *
     * @return the generic type of the subscriber
     */
    default @Nullable Type genericType() {
        return null;
    }
}

