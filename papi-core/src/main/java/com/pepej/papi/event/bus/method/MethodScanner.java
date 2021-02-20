package com.pepej.papi.event.bus.method;

import com.pepej.papi.event.bus.api.PostOrders;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

/**
 * Determines which methods on a listener should be registered
 * as subscribers, and what properties they should have.
 *
 * @param <L> the listener type
 */
public interface MethodScanner<L> {
    /**
     * Gets if the factory should generate a subscriber for this method.
     *
     * @param listener the listener being scanned
     * @param method the method declaration being considered
     * @return if a subscriber should be registered
     */
    boolean shouldRegister(final @NonNull L listener, final @NonNull Method method);

    /**
     * Gets the post order the resultant subscriber should be called at.
     *
     * @param listener the listener
     * @param method the method
     * @return the post order of this subscriber
     * @see PostOrders
     */
    int postOrder(final @NonNull L listener, final @NonNull Method method);

    /**
     * Gets if cancelled events should be posted to the resultant subscriber.
     *
     * @param listener the listener
     * @param method the method
     * @return if cancelled events should be posted
     */
    boolean consumeCancelledEvents(final @NonNull L listener, final @NonNull Method method);
}