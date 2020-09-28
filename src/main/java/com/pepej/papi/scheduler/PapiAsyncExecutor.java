package com.pepej.papi.scheduler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

final class PapiAsyncExecutor extends AbstractExecutorService implements ScheduledExecutorService {
    private final ExecutorService taskService;
    private final ScheduledExecutorService timerExecutionService;

    private final Set<ScheduledFuture<?>> tasks = Collections.newSetFromMap(new WeakHashMap<>());

    PapiAsyncExecutor() {
        this.taskService = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("papi-scheduler-%d")
                .build()
        );
        this.timerExecutionService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("papi-scheduler-timer")
                .build()
        );
    }

    private ScheduledFuture<?> consumeTask(ScheduledFuture<?> future) {
        synchronized (this.tasks) {
            this.tasks.add(future);
        }
        return future;
    }

    public void cancelRepeatingTasks() {
        synchronized (this.tasks) {
            for (ScheduledFuture<?> task : this.tasks) {
                task.cancel(false);
            }
        }
    }

    @Override
    public void execute(@Nonnull Runnable runnable) {
        this.taskService.execute(PapiExecutors.wrapRunnable(runnable));
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> schedule(@Nonnull Runnable command, long delay, @Nonnull TimeUnit unit) {
        Runnable delegate = PapiExecutors.wrapRunnable(command);
        return consumeTask(this.timerExecutionService.schedule(() -> this.taskService.execute(delegate), delay, unit));
    }

    @Nonnull
    @Override
    public <V> ScheduledFuture<V> schedule(@Nonnull Callable<V> callable, long delay, @Nonnull TimeUnit unit) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(@Nonnull Runnable command, long initialDelay, long period, @Nonnull TimeUnit unit) {
        return consumeTask(this.timerExecutionService.scheduleAtFixedRate(new FixedRateWorker(PapiExecutors.wrapRunnable(command)), initialDelay, period, unit));
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(@Nonnull Runnable command, long initialDelay, long delay, @Nonnull TimeUnit unit) {
        return scheduleAtFixedRate(command, initialDelay, delay, unit);
    }

    @Override
    public void shutdown() {
        // noop
    }

    @Override
    public List<Runnable> shutdownNow() {
        // noop
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, @Nonnull TimeUnit unit) {
        throw new IllegalStateException("Not shutdown");
    }

    private final class FixedRateWorker implements Runnable {
        private final Runnable delegate;
        private final ReentrantLock lock = new ReentrantLock();
        private final AtomicInteger running = new AtomicInteger(0);

        private FixedRateWorker(Runnable delegate) {
            this.delegate = delegate;
        }

        // the purpose of 'lock' and 'running' is to prevent concurrent
        // execution on the underlying delegate runnable.
        // only one instance of the worker will "wait" for the previous task to finish

        @Override
        public void run() {
            // assuming a task that takes a really long time:
            // first call: running=1 - we want to run
            // second call: running=2 - we want to wait
            // third call: running=3 - assuming second is still waiting, we want to cancel
            if (this.running.incrementAndGet() > 2) {
                this.running.decrementAndGet();
                return;
            }

            PapiAsyncExecutor.this.taskService.execute(() -> {
                this.lock.lock();
                try {
                    this.delegate.run();
                } finally {
                    this.lock.unlock();
                    this.running.decrementAndGet();
                }
            });
        }
    }
}
