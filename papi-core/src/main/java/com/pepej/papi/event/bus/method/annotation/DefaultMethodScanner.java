package com.pepej.papi.event.bus.method.annotation;


import com.pepej.papi.event.bus.api.PostOrders;
import com.pepej.papi.event.bus.method.MethodScanner;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;

/**
 * Implementation of {@link MethodScanner} using the built-in
 * {@link Subscribe}, {@link IgnoreCancelled} and {@link PostOrder} annotations.
 *
 * @param <L> the listener type
 */
public class DefaultMethodScanner<L> implements MethodScanner<L> {
    private static final DefaultMethodScanner INSTANCE = new DefaultMethodScanner();

    @SuppressWarnings("unchecked")
    public static <L> @NonNull MethodScanner<L> get() {
        return (MethodScanner<L>) INSTANCE;
    }

    // Allow subclasses
    protected DefaultMethodScanner() {
    }

    @Override
    public boolean shouldRegister(final @NonNull L listener, final @NonNull Method method) {
        return method.getAnnotation(Subscribe.class) != null;
    }

    @Override
    public int postOrder(final @NonNull L listener, final @NonNull Method method) {
        return method.isAnnotationPresent(PostOrder.class) ? method.getAnnotation(PostOrder.class).value() : PostOrders.NORMAL;
    }

    @Override
    public boolean consumeCancelledEvents(final @NonNull L listener, final @NonNull Method method) {
        return !method.isAnnotationPresent(IgnoreCancelled.class);
    }
}