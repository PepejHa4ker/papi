package com.pepej.papi.messaging.util;

import com.pepej.papi.Schedulers;
import com.pepej.papi.messaging.Channel;
import com.pepej.papi.promise.ThreadContext;
import com.pepej.papi.scheduler.Task;
import com.pepej.papi.terminable.Terminable;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Periodically publishes a message to a channel.
 *
 * @param <T> the message type
 */
public final class ChannelPublisher<T> implements Terminable {

    /**
     * Creates a new channel publisher.
     *
     * @param channel the channel
     * @param duration the duration to wait between publishing
     * @param unit the unit of duration
     * @param threadContext the context to call the supplier in
     * @param supplier the message supplier
     * @param <T> the type of the message
     * @return a channel publisher
     */
    @Nonnull
    public static <T> ChannelPublisher<T> create(@Nonnull Channel<T> channel, long duration, @Nonnull TimeUnit unit, @Nonnull ThreadContext threadContext, @Nonnull Supplier<? extends T> supplier) {
        Objects.requireNonNull(channel, "channel");
        Objects.requireNonNull(unit, "unit");
        Objects.requireNonNull(threadContext, "threadContext");
        Objects.requireNonNull(supplier, "supplier");

        return new ChannelPublisher<>(channel, supplier, duration, unit, threadContext);
    }

    /**
     * Creates a new channel publisher.
     *
     * @param channel the channel
     * @param duration the duration to wait between publishing
     * @param unit the unit of duration
     * @param supplier the message supplier
     * @param <T> the type of the message
     * @return a channel publisher
     */
    @Nonnull
    public static <T> ChannelPublisher<T> create(@Nonnull Channel<T> channel, long duration, @Nonnull TimeUnit unit, @Nonnull Supplier<? extends T> supplier) {
        return create(channel, duration, unit, ThreadContext.ASYNC, supplier);
    }

    private final Channel<T> channel;
    private final Supplier<? extends T> supplier;
    private final Task task;

    private ChannelPublisher(Channel<T> channel, Supplier<? extends T> supplier, long duration, TimeUnit unit, ThreadContext threadContext) {
        this.channel = channel;
        this.supplier = supplier;
        this.task = Schedulers.builder()
                              .on(threadContext)
                              .afterAndEvery(duration, unit)
                              .run(this::submit);
    }

    /**
     * Gets the channel
     *
     * @return the channel
     */
    public Channel<T> getChannel() {
        return this.channel;
    }

    private void submit() {
        this.channel.sendMessage(this.supplier.get());
    }

    @Override
    public void close() {
        this.task.close();
    }
}
