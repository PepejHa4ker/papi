package com.pepej.papi.scheduler.builder;

import com.pepej.papi.scheduler.Scheduler;
import com.pepej.papi.scheduler.Task;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * Queues execution of tasks using {@link Scheduler}, often combining parameters with
 * variables already known by this instance.
 */
public interface ContextualTaskBuilder {

    @Nonnull
    Task consume(@Nonnull Consumer<Task> consumer);

    @Nonnull
    Task run(@Nonnull Runnable runnable);

}
