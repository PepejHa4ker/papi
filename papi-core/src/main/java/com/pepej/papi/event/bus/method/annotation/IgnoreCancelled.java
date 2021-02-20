package com.pepej.papi.event.bus.method.annotation;

import com.pepej.papi.event.bus.api.Cancellable;

import java.lang.annotation.*;

/**
 * Marks an event subscriber that should not receive events even if they have been {@link Cancellable#cancelled() cancelled}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IgnoreCancelled {
}
