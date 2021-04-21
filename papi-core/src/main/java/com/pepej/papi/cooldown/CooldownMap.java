package com.pepej.papi.cooldown;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

/**
 * A self-populating map of cooldown instances
 *
 * @param <T> the type
 */
public interface CooldownMap<T> {

    /**
     * Creates a new collection with the cooldown properties defined by the base instance
      *
     * @param base the cooldown to base off
     * @param <T> the type
     * @return a new collection
     */
    @NonNull
    static <T> CooldownMap<T> create(@NonNull Cooldown base) {
        Objects.requireNonNull(base, "base");
        return new CooldownMapImpl<>(base);
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
    Cooldown get(@NonNull T key);

    void put(@NonNull T key, @NonNull Cooldown cooldown);

    /**
     * Gets the cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @NonNull
    Map<T, Cooldown> getAll();

    /* methods from Cooldown */

    default boolean test(@NonNull T key) {
        return get(key).testAndReset();
    }

    default boolean testSilently(@NonNull T key) {
        return get(key).test();
    }

    default long elapsed(@NonNull T key) {
        return get(key).elapsed();
    }

    default void reset(@NonNull T key) {
        get(key).reset();
    }

    default long remainingMillis(@NonNull T key) {
        return get(key).remainingMillis();
    }

    default long remainingTime(@NonNull T key, @NonNull TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    @NonNull
    default OptionalLong getLastTested(@NonNull T key) {
        return get(key).getLastTested();
    }

    default void setLastTested(@NonNull T key, long time) {
        get(key).setLastTested(time);
    }

}
