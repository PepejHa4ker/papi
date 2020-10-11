package com.pepej.papi.scheduler.builder;


import com.pepej.papi.promise.Promise;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Builds instances of {@link Promise}, often combining parameters with
 * variables already known by this instance.
 */
public interface ContextualPromiseBuilder {

    @Nonnull
    <T> Promise<T> supply(@Nonnull Supplier<T> supplier);

    @Nonnull
    <T> Promise<T> call(@Nonnull Callable<T> callable);

    @Nonnull
    Promise<Void> run(@Nonnull Runnable runnable);

}
