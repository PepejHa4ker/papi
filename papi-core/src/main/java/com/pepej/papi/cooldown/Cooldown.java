package com.pepej.papi.cooldown;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pepej.papi.gson.GsonSerializable;
import com.pepej.papi.scheduler.Ticks;
import com.pepej.papi.time.Time;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

/**
 * A simple cooldown abstraction
 */
public interface Cooldown extends GsonSerializable {
    static Cooldown deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("lastTested"));
        Preconditions.checkArgument(object.has("timeout"));

        long lastTested = object.get("lastTested").getAsLong();
        long timeout = object.get("timeout").getAsLong();

        Cooldown c = of(timeout, TimeUnit.MILLISECONDS);
        c.setLastTested(lastTested);
        return c;
    }

    /**
     * Creates a cooldown lasting a number of game ticks
     *
     * @param ticks the number of ticks
     * @return a new cooldown
     */
    @NonNull
    static Cooldown ofTicks(long ticks) {
        return new CooldownImpl(Ticks.to(ticks, TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS);
    }

    /**
     * Creates a cooldown lasting a specified amount of time
     *
     * @param amount the amount of time
     * @param unit the unit of time
     * @return a new cooldown
     */
    @NonNull
    static Cooldown of(long amount, @NonNull TimeUnit unit) {
        return new CooldownImpl(amount, unit);
    }

    /**
     * Returns true if the cooldown is not active, and then resets the timer
     *
     * <p>If the cooldown is currently active, the timer is <strong>not</strong> reset.</p>
     *
     * @return true if the cooldown is not active
     */
    default boolean testAndReset() {
        if (!test()) {
            return false;
        }

        reset();
        return true;
    }

    /**
     * Returns true if the cooldown is not active
     *
     * @return true if the cooldown is not active
     */
    default boolean test() {
        return elapsed() > getTimeout();
    }

    /**
     * Returns the elapsed time in milliseconds since the cooldown was last reset, or since creation time
     *
     * @return the elapsed time
     */
    default long elapsed() {
        return Time.nowMillis() - getLastTested().orElse(0);
    }

    /**
     * Resets the cooldown
     */
    default void reset() {
        setLastTested(Time.nowMillis());
    }

    /**
     * Gets the time in milliseconds until the cooldown will become inactive.
     *
     * <p>If the cooldown is not active, this method returns <code>0</code>.</p>
     *
     * @return the time in millis until the cooldown will expire
     */
    default long remainingMillis() {
        long diff = elapsed();
        return diff > getTimeout() ? 0L : getTimeout() - diff;
    }

    /**
     * Gets the time until the cooldown will become inactive.
     *
     * <p>If the cooldown is not active, this method returns <code>0</code>.</p>
     *
     * @param unit the unit to return in
     * @return the time until the cooldown will expire
     */
    default long remainingTime(TimeUnit unit) {
        return Math.max(0L, unit.convert(remainingMillis(), TimeUnit.MILLISECONDS));
    }

    /**
     * Return the time in milliseconds when this cooldown was last {@link #testAndReset()}ed.
     *
     * @return the last call time
     */
    @NonNull
    OptionalLong getLastTested();

    /**
     * Sets the time in milliseconds when this cooldown was last tested.
     *
     * <p>Note: this should only be used when re-constructing a cooldown
     * instance. Use {@link #testAndReset()} otherwise.</p>
     *
     * @param time the time
     */
    void setLastTested(long time);

    /**
     * Gets the timeout in milliseconds for this cooldown
     *
     * @return the timeout in milliseconds
     */
    long getTimeout();

    /**
     * Copies the properties of this cooldown to a new instance
     *
     * @return a cloned cooldown instance
     */
    @NonNull
    Cooldown copy();

}
