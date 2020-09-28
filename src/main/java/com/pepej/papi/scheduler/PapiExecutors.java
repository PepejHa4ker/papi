package com.pepej.papi.scheduler;

import com.pepej.papi.interfaces.Delegate;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.utils.Log;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

/**
 * Provides common {@link Executor} instances.
 */
public final class PapiExecutors {
    private static final Consumer<Throwable> EXCEPTION_CONSUMER = throwable -> {
        Log.severe("[SCHEDULER] Exception thrown whilst executing task");
        throwable.printStackTrace();
    };

    private static final Executor SYNC_BUKKIT = new BukkitSyncExecutor();
    private static final Executor ASYNC_BUKKIT = new BukkitAsyncExecutor();
    private static final PapiAsyncExecutor ASYNC_PAPI = new PapiAsyncExecutor();

    public static Executor sync() {
        return SYNC_BUKKIT;
    }

    public static ScheduledExecutorService asyncPapi() {
        return ASYNC_PAPI;
    }

    public static Executor asyncBukkit() {
        return ASYNC_BUKKIT;
    }

    public static void shutdown() {
        ASYNC_PAPI.cancelRepeatingTasks();
    }

    private static final class BukkitSyncExecutor implements Executor {
        @Override
        public void execute(@Nonnull Runnable runnable) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(LoaderUtils.getPlugin(), wrapRunnable(runnable));
        }
    }

    private static final class BukkitAsyncExecutor implements Executor {
        @Override
        public void execute(@Nonnull Runnable runnable) {
            Bukkit.getScheduler().runTaskAsynchronously(LoaderUtils.getPlugin(), wrapRunnable(runnable));
        }
    }

    public static Runnable wrapRunnable(Runnable runnable) {
        return new SchedulerWrappedRunnable(runnable);
    }

    private static final class SchedulerWrappedRunnable implements Runnable, Delegate<Runnable> {
        private final Runnable delegate;

        private SchedulerWrappedRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public void run() {
            try {
                this.delegate.run();
            } catch (Throwable t) {
                EXCEPTION_CONSUMER.accept(t);
            }
        }

        @Override
        public Runnable getDelegate() {
            return this.delegate;
        }
    }


    private PapiExecutors() {}

}
