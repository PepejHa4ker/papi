package com.pepej.papi.cooldown;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * A self-populating, composed map of cooldown instances
 *
 * @param <I> input type
 * @param <O> internal type
 */
public interface ComposedCooldownMap<I, O> {

    /**
     * Creates a new collection with the cooldown properties defined by the base instance
     *
     * @param base the cooldown to base off
     * @param <I> input type
     * @param <O> internal type
     * @param composeFunction the function to compose
     * @return a new collection
     */
    @NonNull
    static <I, O> ComposedCooldownMap<I, O> create(@NonNull Cooldown base, @NonNull Function<I, O> composeFunction) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(composeFunction, "composeFunction");
        return new ComposedCooldownMapImpl<>(base, composeFunction);
    }

    /**
     * Gets the base cooldown
     *
     * @return the base cooldown
     */
    @NonNull
    Cooldown getBase();

    /**
     * Gets the internal cooldown instance associated with the given key.
     *
     * <p>The inline Cooldown methods in this class should be used to access the functionality of the cooldown as opposed
     * to calling the methods directly via the instance returned by this method.</p>
     *
     * @param key the key
     * @return a cooldown instance
     */
    @NonNull
    Cooldown get(@NonNull I key);

    void put(@NonNull O key, @NonNull Cooldown cooldown);

    /**
     * Gets the cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @NonNull
    Map<O, Cooldown> getAll();

    /* methods from Cooldown */

    default boolean test(@NonNull I key) {
        return get(key).testAndReset();
    }

    default boolean testSilently(@NonNull I key) {
        return get(key).test();
    }

    default long elapsed(@NonNull I key) {
        return get(key).elapsed();
    }

    default void reset(@NonNull I key) {
        get(key).reset();
    }

    default long remainingMillis(@NonNull I key) {
        return get(key).remainingMillis();
    }

    default long remainingTime(@NonNull I key, @NonNull TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    @NonNull
    default OptionalLong getLastTested(@NonNull I key) {
        return get(key).getLastTested();
    }

    default void setLastTested(@NonNull I key, long time) {
        get(key).setLastTested(time);
    }

}
