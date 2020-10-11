package com.pepej.papi.eventbus;

import com.pepej.papi.terminable.Terminable;

import javax.annotation.Nonnull;

/**
 * Utilities for working with {@link EventSubscriber}s.
 */
public final class Subscribers {

    private Subscribers() {}

    /**
     * Registers the given {@code subscriber} with the {@code bus}, and returns
     * a {@link Terminable} to encapsulate the subscription.
     *
     * @param bus the event bus
     * @param clazz the registration class
     * @param subscriber the subscriber
     * @param <E> the event type
     * @param <T> the subscriber type
     * @return a terminable to encapsulate the subscription
     */
    public static <E, T extends E> Terminable register(EventBus<E> bus, @Nonnull final Class<T> clazz, @Nonnull final EventSubscriber<? super T> subscriber) {
        bus.register(clazz, subscriber);
        return () -> bus.unregister(subscriber);
    }

}
