package com.pepej.papi.event.functional;

import com.pepej.papi.event.Subscription;
import com.pepej.papi.utils.Log;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * Abstract {@link Subscription} builder.
 *
 * @param <T> the handled / event type
 */
public interface SubscriptionBuilder<T> {

    BiConsumer<Object, Throwable> DEFAULT_EXCEPTION_CONSUMER = (event, throwable) -> {
        Log.severe("&a[EVENTS]&c Exception thrown whilst handling &d%s", event.getClass().getSimpleName());
        throwable.printStackTrace();
    };

    /**
     * Add a expiry predicate.
     *
     * @param predicate the expiry test
     * @return ths builder instance
     */
    @NonNull
    SubscriptionBuilder<T> expireIf(@NonNull Predicate<T> predicate);

    /**
     * Sets the expiry time on the handler
     *
     * @param duration the duration until expiry
     * @param unit     the unit for the duration
     * @return the builder instance
     * @throws IllegalArgumentException if duration is not greater than or equal to 1
     */
    @NonNull
    SubscriptionBuilder<T> expireAfter(long duration, @NonNull TimeUnit unit);

    /**
     * Sets the number of calls until the handler will automatically be unregistered
     *
     * <p>The call counter is only incremented if the event call passes all filters and if the handler completes
     * without throwing an exception.
     *
     * @param maxCalls the number of times the handler will be called until being unregistered.
     * @return the builder instance
     * @throws IllegalArgumentException if maxCalls is not greater than or equal to 1
     */
    @NonNull
    SubscriptionBuilder<T> expireAfter(long maxCalls);

    /**
     * Adds a filter to the handler.
     *
     * <p>An event will only be handled if it passes all filters. Filters are evaluated in the order they are
     * registered.
     *
     * @param predicate the filter
     * @return the builder instance
     */
    @NonNull
    SubscriptionBuilder<T> filter(@NonNull Predicate<T> predicate);

    /**
     * Adds a filter to the handler.
     *
     * <p>An event will only be handled if it not passes all filters. Filters are evaluated in the order they are
     * registered.
     *
     * @param predicate the filter
     * @return the builder instance
     */
    @NonNull
    SubscriptionBuilder<T> filterNot(@NonNull Predicate<T> predicate);

}
