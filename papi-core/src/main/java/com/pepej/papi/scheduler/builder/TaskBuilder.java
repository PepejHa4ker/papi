package com.pepej.papi.scheduler.builder;

import com.pepej.papi.promise.ThreadContext;
import com.pepej.papi.scheduler.Scheduler;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.TimeUnit;

/**
 * Functional builder providing chained access to the functionality in {@link Scheduler};
 */
public interface TaskBuilder {

    /**
     * Gets a task builder instance
     *
     * @return a task builder instance
     */
    @NonNull
    static TaskBuilder newBuilder() {
        return TaskBuilderImpl.INSTANCE;
    }

    /**
     * Defines the thread context of the new task,
     * and returns the next builder in the chain.
     *
     * @param context the context to run the task in
     * @return a contextual builder
     */
    @NonNull
    default ThreadContextual on(@NonNull ThreadContext context) {
        switch (context) {
            case SYNC:
                return sync();
            case ASYNC:
                return async();
            default:
                throw new AssertionError();
        }
    }

    /**
     * Marks that the new task should run sync,
     * and returns the next builder in the chain.
     *
     * @return a "sync" contextual builder
     */
    @NonNull
    ThreadContextual sync();

    /**
     * Marks that the new task should run async,
     * and returns the next builder in the chain.
     *
     * @return an "async" contextual builder
     */
    @NonNull
    ThreadContextual async();

    /**
     * The next builder in the task chain, which already has a defined task context.
     */
    interface ThreadContextual {

        /**
         * Marks that the new task should execute immediately,
         * and returns the next builder in the chain.
         *
         * @return an "instant" promise builder
         */
        @NonNull
        ContextualPromiseBuilder now();

        /**
         * Marks that the new task should run after the specified delay,
         * and returns the next builder in the chain.
         *
         * @param ticks the number of ticks to delay execution by
         * @return a delayed builder
         */
        @NonNull
        DelayedTick after(long ticks);

        /**
         * Marks that the new task should run after the specified delay,
         * and returns the next builder in the chain.
         *
         * @param duration the duration to delay execution by
         * @param unit the units of the duration
         * @return a delayed builder
         */
        @NonNull
        DelayedTime after(long duration, @NonNull TimeUnit unit);

        /**
         * Marks that the new task should run after the specified delay,
         * then repeat on the specified interval, and returns the next builder in the chain.
         *
         * @param ticks the number of ticks to delay execution by
         * @return a delayed builder
         */
        @NonNull
        ContextualTaskBuilder afterAndEvery(long ticks);

        /**
         * Marks that the new task should run after the specified delay,
         * then repeat on the specified interval, and returns the next builder in the chain.
         *
         * @param duration the duration to delay execution by
         * @param unit the units of the duration
         * @return a delayed builder
         */
        @NonNull
        ContextualTaskBuilder afterAndEvery(long duration, @NonNull TimeUnit unit);

        /**
         * Marks that the new task should start running instantly, but repeat on the specified interval,
         * and returns the next builder in the chain.
         *
         * @param ticks the number of ticks to wait between executions
         * @return a delayed builder
         */
        @NonNull
        ContextualTaskBuilder every(long ticks);

        /**
         * Marks that the new task should start running instantly, but repeat on the specified interval,
         * and returns the next builder in the chain.
         *
         * @param duration the duration to wait between executions
         * @param unit the units of the duration
         * @return a delayed builder
         */
        @NonNull
        ContextualTaskBuilder every(long duration, @NonNull TimeUnit unit);

    }

    /**
     * The next builder in the task chain, which already has a defined delay context.
     *
     * <p>This interface extends {@link ContextualPromiseBuilder} to allow for
     * delayed, non-repeating tasks.</p>
     */
    interface Delayed extends ContextualPromiseBuilder {

    }

    interface DelayedTick extends Delayed {

        /**
         * Marks that the new task should repeat on the specified interval,
         * and returns the next builder in the chain.
         *
         * @param ticks the number of ticks to wait between executions
         * @return a delayed builder
         */
        @NonNull
        ContextualTaskBuilder every(long ticks);

    }

    interface DelayedTime extends Delayed {

        /**
         * Marks that the new task should repeat on the specified interval,
         * and returns the next builder in the chain.
         *
         * @param duration the duration to wait between executions
         * @param unit the units of the duration
         * @return a delayed builder
         */
        @NonNull
        ContextualTaskBuilder every(long duration, TimeUnit unit);

    }

}
