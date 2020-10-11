package com.pepej.papi.cooldown;

import javax.annotation.Nonnull;
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
     * @return a new collection
     */
    @Nonnull
    static <T> CooldownMap<T> create(@Nonnull Cooldown base) {
        Objects.requireNonNull(base, "base");
        return new CooldownMapImpl<>(base);
    }

    /**
     * Gets the base cooldown
     *
     * @return the base cooldown
     */
    @Nonnull
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
    @Nonnull
    Cooldown get(@Nonnull T key);

    void put(@Nonnull T key, @Nonnull Cooldown cooldown);

    /**
     * Gets the cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @Nonnull
    Map<T, Cooldown> getAll();

    /* methods from Cooldown */

    default boolean test(@Nonnull T key) {
        return get(key).test();
    }

    default boolean testSilently(@Nonnull T key) {
        return get(key).testSilently();
    }

    default long elapsed(@Nonnull T key) {
        return get(key).elapsed();
    }

    default void reset(@Nonnull T key) {
        get(key).reset();
    }

    default long remainingMillis(@Nonnull T key) {
        return get(key).remainingMillis();
    }

    default long remainingTime(@Nonnull T key, @Nonnull TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    @Nonnull
    default OptionalLong getLastTested(@Nonnull T key) {
        return get(key).getLastTested();
    }

    default void setLastTested(@Nonnull T key, long time) {
        get(key).setLastTested(time);
    }

}
