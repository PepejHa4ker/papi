package com.pepej.papi.cooldown;

import javax.annotation.Nonnull;
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
     * @return a new collection
     */
    @Nonnull
    static <I, O> ComposedCooldownMap<I, O> create(@Nonnull Cooldown base, @Nonnull Function<I, O> composeFunction) {
        Objects.requireNonNull(base, "base");
        Objects.requireNonNull(composeFunction, "composeFunction");
        return new ComposedCooldownMapImpl<>(base, composeFunction);
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
    Cooldown get(@Nonnull I key);

    void put(@Nonnull O key, @Nonnull Cooldown cooldown);

    /**
     * Gets the cooldowns contained within this collection.
     *
     * @return the backing map
     */
    @Nonnull
    Map<O, Cooldown> getAll();

    /* methods from Cooldown */

    default boolean test(@Nonnull I key) {
        return get(key).test();
    }

    default boolean testSilently(@Nonnull I key) {
        return get(key).testSilently();
    }

    default long elapsed(@Nonnull I key) {
        return get(key).elapsed();
    }

    default void reset(@Nonnull I key) {
        get(key).reset();
    }

    default long remainingMillis(@Nonnull I key) {
        return get(key).remainingMillis();
    }

    default long remainingTime(@Nonnull I key, @Nonnull TimeUnit unit) {
        return get(key).remainingTime(unit);
    }

    @Nonnull
    default OptionalLong getLastTested(@Nonnull I key) {
        return get(key).getLastTested();
    }

    default void setLastTested(@Nonnull I key, long time) {
        get(key).setLastTested(time);
    }

}
