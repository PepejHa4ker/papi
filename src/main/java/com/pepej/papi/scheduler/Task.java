package com.pepej.papi.scheduler;

import com.pepej.papi.terminable.Terminable;

/**
 * Represents a scheduled repeating task
 */
public interface Task extends Terminable {

    /**
     * Gets the number of times this task has ran. The counter is only incremented at the end of execution.
     *
     * @return the number of times this task has ran
     */
    int getTimesRan();

    /**
     * Stops the task
     *
     * @return true if the task wasn't already cancelled
     */
    boolean stop();

    /**
     * Gets the Bukkit ID for this task
     *
     * @return the bukkit id for this task
     */
    int getBukkitId();

    /**
     * {@link #stop() Stops} the task
     */
    @Override
    default void close() {
        stop();
    }
}