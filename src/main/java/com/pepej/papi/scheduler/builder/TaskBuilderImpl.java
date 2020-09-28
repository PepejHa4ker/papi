package com.pepej.papi.scheduler.builder;


import com.pepej.papi.Schedulers;
import com.pepej.papi.promise.Promise;
import com.pepej.papi.promise.ThreadContext;
import com.pepej.papi.scheduler.Task;

import javax.annotation.Nonnull;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

class TaskBuilderImpl implements TaskBuilder {
    static final TaskBuilder INSTANCE = new TaskBuilderImpl();

    private final TaskBuilder.ThreadContextual sync;
    private final ThreadContextual async;

    private TaskBuilderImpl() {
        this.sync = new ThreadContextualBuilder(ThreadContext.SYNC);
        this.async = new ThreadContextualBuilder(ThreadContext.ASYNC);
    }

    @Nonnull
    @Override
    public TaskBuilder.ThreadContextual sync() {
        return this.sync;
    }

    @Nonnull
    @Override
    public TaskBuilder.ThreadContextual async() {
        return this.async;
    }

    private static final class ThreadContextualBuilder implements TaskBuilder.ThreadContextual {
        private final ThreadContext context;
        private final ContextualPromiseBuilder instant;

        ThreadContextualBuilder(ThreadContext context) {
            this.context = context;
            this.instant = new ContextualPromiseBuilderImpl(context);
        }

        @Nonnull
        @Override
        public ContextualPromiseBuilder now() {
            return this.instant;
        }

        @Nonnull
        @Override
        public DelayedTick after(long ticks) {
            return new DelayedTickBuilder(this.context, ticks);
        }

        @Nonnull
        @Override
        public DelayedTime after(long duration, @Nonnull TimeUnit unit) {
            return new DelayedTimeBuilder(this.context, duration, unit);
        }

        @Nonnull
        @Override
        public ContextualTaskBuilder afterAndEvery(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, ticks, ticks);
        }

        @Nonnull
        @Override
        public ContextualTaskBuilder afterAndEvery(long duration, @Nonnull TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, duration, unit, duration, unit);
        }

        @Nonnull
        @Override
        public ContextualTaskBuilder every(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, 0, ticks);
        }

        @Nonnull
        @Override
        public ContextualTaskBuilder every(long duration, @Nonnull TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, 0, TimeUnit.NANOSECONDS, duration, unit);
        }
    }

    private static final class DelayedTickBuilder implements TaskBuilder.DelayedTick {
        private final ThreadContext context;
        private final long delay;

        DelayedTickBuilder(ThreadContext context, long delay) {
            this.context = context;
            this.delay = delay;
        }

        @Nonnull
        @Override
        public <T> Promise<T> supply(@Nonnull Supplier<T> supplier) {
            return Schedulers.get(this.context).supplyLater(supplier, this.delay);
        }

        @Nonnull
        @Override
        public <T> Promise<T> call(@Nonnull Callable<T> callable) {
            return Schedulers.get(this.context).callLater(callable, this.delay);
        }

        @Nonnull
        @Override
        public Promise<Void> run(@Nonnull Runnable runnable) {
            return Schedulers.get(this.context).runLater(runnable, this.delay);
        }

        @Nonnull
        @Override
        public ContextualTaskBuilder every(long ticks) {
            return new ContextualTaskBuilderTickImpl(this.context, this.delay, ticks);
        }
    }

    private static final class DelayedTimeBuilder implements TaskBuilder.DelayedTime {
        private final ThreadContext context;
        private final long delay;
        private final TimeUnit delayUnit;

        DelayedTimeBuilder(ThreadContext context, long delay, TimeUnit delayUnit) {
            this.context = context;
            this.delay = delay;
            this.delayUnit = delayUnit;
        }

        @Nonnull
        @Override
        public <T> Promise<T> supply(@Nonnull Supplier<T> supplier) {
            return Schedulers.get(this.context).supplyLater(supplier, this.delay, this.delayUnit);
        }

        @Nonnull
        @Override
        public <T> Promise<T> call(@Nonnull Callable<T> callable) {
            return Schedulers.get(this.context).callLater(callable, this.delay, this.delayUnit);
        }

        @Nonnull
        @Override
        public Promise<Void> run(@Nonnull Runnable runnable) {
            return Schedulers.get(this.context).runLater(runnable, this.delay, this.delayUnit);
        }

        @Nonnull
        @Override
        public ContextualTaskBuilder every(long duration, TimeUnit unit) {
            return new ContextualTaskBuilderTimeImpl(this.context, this.delay, this.delayUnit, duration, unit);
        }
    }

    private static class ContextualPromiseBuilderImpl implements ContextualPromiseBuilder {
        private final ThreadContext context;

        ContextualPromiseBuilderImpl(ThreadContext context) {
            this.context = context;
        }

        @Nonnull
        @Override
        public <T> Promise<T> supply(@Nonnull Supplier<T> supplier) {
            return Schedulers.get(this.context).supply(supplier);
        }

        @Nonnull
        @Override
        public <T> Promise<T> call(@Nonnull Callable<T> callable) {
            return Schedulers.get(this.context).call(callable);
        }

        @Nonnull
        @Override
        public Promise<Void> run(@Nonnull Runnable runnable) {
            return Schedulers.get(this.context).run(runnable);
        }
    }

    private static class ContextualTaskBuilderTickImpl implements ContextualTaskBuilder {
        private final ThreadContext context;
        private final long delay;
        private final long interval;

        ContextualTaskBuilderTickImpl(ThreadContext context, long delay, long interval) {
            this.context = context;
            this.delay = delay;
            this.interval = interval;
        }

        @Nonnull
        @Override
        public Task consume(@Nonnull Consumer<Task> consumer) {
            return Schedulers.get(this.context).runRepeating(consumer, this.delay, this.interval);
        }

        @Nonnull
        @Override
        public Task run(@Nonnull Runnable runnable) {
            return Schedulers.get(this.context).runRepeating(runnable, this.delay, this.interval);
        }
    }

    private static class ContextualTaskBuilderTimeImpl implements ContextualTaskBuilder {
        private final ThreadContext context;
        private final long delay;
        private final TimeUnit delayUnit;
        private final long interval;
        private final TimeUnit intervalUnit;

        ContextualTaskBuilderTimeImpl(ThreadContext context, long delay, TimeUnit delayUnit, long interval, TimeUnit intervalUnit) {
            this.context = context;
            this.delay = delay;
            this.delayUnit = delayUnit;
            this.interval = interval;
            this.intervalUnit = intervalUnit;
        }

        @Nonnull
        @Override
        public Task consume(@Nonnull Consumer<Task> consumer) {
            return Schedulers.get(this.context).runRepeating(consumer, this.delay, this.delayUnit, this.interval, this.intervalUnit);
        }

        @Nonnull
        @Override
        public Task run(@Nonnull Runnable runnable) {
            return Schedulers.get(this.context).runRepeating(runnable, this.delay, this.delayUnit, this.interval, this.intervalUnit);
        }
    }
}
