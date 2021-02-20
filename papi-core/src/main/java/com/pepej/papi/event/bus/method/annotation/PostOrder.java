package com.pepej.papi.event.bus.method.annotation;

import com.pepej.papi.event.bus.api.PostOrders;

import java.lang.annotation.*;

/**
 * Marks the post order of an event subscriber.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostOrder {
    /**
     * Gets the post order of this subscriber.
     *
     * @return the post order
     * @see PostOrders
     */
    int value();
}
