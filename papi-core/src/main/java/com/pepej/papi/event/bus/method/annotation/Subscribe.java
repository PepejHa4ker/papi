package com.pepej.papi.event.bus.method.annotation;

import java.lang.annotation.*;

/**
 * Marks a method as an event subscriber.
 *
 * @see IgnoreCancelled
 * @see PostOrder
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {
}