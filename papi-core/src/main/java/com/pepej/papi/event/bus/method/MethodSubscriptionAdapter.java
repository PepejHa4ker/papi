package com.pepej.papi.event.bus.method;


import com.pepej.papi.event.bus.api.EventBus;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

/**
 * A subscription adapter for {@link EventBus} which supports defining
 * event subscribers as methods in a class.
 *
 * @param <L> the listener type
 */
public interface MethodSubscriptionAdapter<L> {
    /**
     * Registers all methods determined to be {@link MethodScanner#shouldRegister(Object, Method) valid}
     * on the {@code listener} to receive events.
     *
     * @param listener the listener
     */
    void register(final @NonNull L listener);

    /**
     * Unregisters all methods on a registered {@code listener}.
     *
     * @param listener the listener
     */
    void unregister(final @NonNull L listener);
}
