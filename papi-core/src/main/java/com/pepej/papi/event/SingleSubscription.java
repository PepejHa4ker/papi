package com.pepej.papi.event;

import org.bukkit.event.Event;

import javax.annotation.Nonnull;

/**
 * Represents a subscription to a single given event.
 *
 * @param <T> the event type
 */
public interface SingleSubscription<T extends Event> extends Subscription {

    /**
     * Gets the class the handler is handling
     *
     * @return the class the handler is handling.
     */
    @Nonnull
    Class<T> getEventClass();

}
