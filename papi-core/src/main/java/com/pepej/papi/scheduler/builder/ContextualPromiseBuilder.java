package com.pepej.papi.scheduler.builder;


import com.pepej.papi.promise.Promise;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Builds instances of {@link Promise}, often combining parameters with
 * variables already known by this instance.
 */
public interface ContextualPromiseBuilder {

    @NonNull
    <T> Promise<T> supply(@NonNull Supplier<T> supplier);

    @NonNull
    <T> Promise<T> call(@NonNull Callable<T> callable);

    @NonNull
    Promise<Void> run(@NonNull Runnable runnable);

}
