package com.pepej.papi.scheduler.builder;

import com.pepej.papi.scheduler.Scheduler;
import com.pepej.papi.scheduler.Task;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.function.Consumer;

/**
 * Queues execution of tasks using {@link Scheduler}, often combining parameters with
 * variables already known by this instance.
 */
public interface ContextualTaskBuilder {

    @NonNull
    Task consume(@NonNull Consumer<Task> consumer);

    @NonNull
    Task run(@NonNull Runnable runnable);

}
