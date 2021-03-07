package com.pepej.papi.event.bus.method;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

/**
 * Functional interface that can invoke a defined method on a listener object when an event is posted.
 *
 * @param <E> the event type
 * @param <L> the listener type
 */
@FunctionalInterface
public interface EventExecutor<E, L> {
    /**
     * Invokes the appropriate method on the given listener to handle the event.
     *
     * @param listener the listener
     * @param event the event
     * @throws Exception if an exception occurred
     */
    void invoke(final @NonNull L listener, final @NonNull E event) throws Exception;

    /**
     * Factory for {@link EventExecutor}s.
     *
     * @param <E> the event type
     * @param <L> the listener type
     */
    @FunctionalInterface
    interface Factory<E, L> {
        /**
         * Creates an event executor.
         *
         * @param object the listener object
         * @param method the method to call on the object
         * @return an event executor
         * @throws Exception if an exception occurred while creating an executor
         */
        @NonNull EventExecutor<E, L> create(final @NonNull Object object, final @NonNull Method method) throws Exception;
    }
}
