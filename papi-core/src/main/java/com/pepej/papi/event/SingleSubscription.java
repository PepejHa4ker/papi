package com.pepej.papi.event;

import org.bukkit.event.Event;
import org.checkerframework.checker.nullness.qual.NonNull;


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
    @NonNull
    Class<T> getEventClass();

}
