package com.pepej.papi.event.bus.api;


/**
 * The "post order" is a representation of the order, relative to other posts,
 * that a given {@link EventSubscriber} should be posted events.
 *
 * <p>Post orders are represented by {@link Integer}s, and subscribers are
 * ordered using the natural ordering of Java integers.</p>
 *
 * <p>Some "standard" post orders are expressed as constants on this class.</p>
 */
public interface PostOrders {
    /**
     * Marks that the subscriber should be called first, before all other subscribers.
     */
    int FIRST = -100;
    /**
     * Marks that the subscriber should be called before {@link #NORMAL normal} subscribers.
     */
    int EARLY = -50;
    /**
     * Marks that the subscriber should be called with no special priority.
     */
    int NORMAL = 0;
    /**
     * Marks that the subscriber should be called after {@link #NORMAL normal} subscribers.
     */
    int LATE = 50;
    /**
     * Marks that the subscriber should be called last, after all other subscribers.
     */
    int LAST = 100;
}
