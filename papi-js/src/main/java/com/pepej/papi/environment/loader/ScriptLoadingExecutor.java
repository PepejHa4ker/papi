package com.pepej.papi.environment.loader;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * A simplified scheduler for {@link ScriptLoader}s.
 */
public interface ScriptLoadingExecutor extends Executor {

    /**
     * Creates a {@link ScriptLoadingExecutor} using a java executor service
     *
     * @param service the service
     * @return a new loading scheduler
     */
    static ScriptLoadingExecutor usingJavaScheduler(ScheduledExecutorService service) {
        Objects.requireNonNull(service, "service");
        return new ScriptLoadingExecutor() {
            @Override
            public AutoCloseable scheduleAtFixedRate(Runnable task, long time, TimeUnit unit) {
                ScheduledFuture<?> future = service.scheduleAtFixedRate(task, 0L, time, unit);
                return () -> future.cancel(false);
            }

            @Override
            public void execute(@NotNull Runnable command) {
                service.execute(command);
            }
        };
    }

    /**
     * Schedules a task to run at a fixed rate, with the first execution
     * occurring with no delay.
     *
     * @param task the task
     * @param time the time
     * @param unit the unit of the time
     * @return an {@link AutoCloseable}, which will cancel the task when
     *         {@link AutoCloseable#close() closed}.
     * @see ScheduledExecutorService#scheduleAtFixedRate(Runnable, long, long, TimeUnit)
     */
    AutoCloseable scheduleAtFixedRate(Runnable task, long time, TimeUnit unit);

}
