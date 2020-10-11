package com.pepej.papi.event;

import com.pepej.papi.terminable.Terminable;

import java.util.Collection;

/**
 * Represents a subscription to a given (set of) event(s).
 */
public interface Subscription extends Terminable {

    /**
     * Gets whether the handler is active
     *
     * @return if the handler is active
     */
    boolean isActive();

    /**
     * Gets the number of times the handler has been called
     *
     * @return the number of times the handler has been called
     */
    long getCallCounter();

    /**
     * Unregisters the handler
     *
     * @return true if the handler wasn't already unregistered
     */
    boolean unregister();

    @Override
    default void close() {
        unregister();
    }

    /**
     * Gets the functional handlers and filters used by this subscription.
     *
     * @return the functions used by this subscription.
     * @deprecated not API, subject to change or removal
     */
    @Deprecated
    Collection<Object> getFunctions();

}
