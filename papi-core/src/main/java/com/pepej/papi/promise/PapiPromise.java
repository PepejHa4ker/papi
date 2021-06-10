package com.pepej.papi.promise;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.pepej.papi.interfaces.Delegate;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.messaging.bungee.BungeeCord;
import com.pepej.papi.scheduler.PapiExecutors;
import com.pepej.papi.scheduler.Ticks;
import com.pepej.papi.utils.Log;
import org.bukkit.Bukkit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Implementation of {@link Promise} using the server scheduler.
 *
 * @param <V> the result type
 */
final class PapiPromise<V> implements Promise<V> {
    private static final Consumer<Exception> EXCEPTION_CONSUMER = e -> Log.severe("Exception during promise completion: cause %s trace: %s", e.getCause(), e.getStackTrace());

    @NonNull
    static <U> PapiPromise<U> empty() {
        return new PapiPromise<>();
    }

    @NonNull
    static <U> PapiPromise<U> completed(@Nullable U value) {
        return new PapiPromise<>(value);
    }

    @NonNull
    static <U> PapiPromise<U> exceptionally(@NonNull Throwable t) {
        return new PapiPromise<>(t);
    }

    @NonNull
    static <U> Promise<U> wrapFuture(@NonNull Future<U> future) {
        if (future instanceof CompletableFuture<?>) {
            return new PapiPromise<>(((CompletableFuture<U>) future).thenApply(Function.identity()));

        } else if (future instanceof CompletionStage<?>) {
            //noinspection unchecked
            CompletionStage<U> fut = (CompletionStage<U>) future;
            return new PapiPromise<>(fut.toCompletableFuture().thenApply(Function.identity()));

        } else if (future instanceof ListenableFuture<?>) {
            ListenableFuture<U> fut = (ListenableFuture<U>) future;
            PapiPromise<U> promise = empty();
            promise.supplied.set(true);

            Futures.addCallback(fut, new FutureCallback<U>() {
                @Override
                public void onSuccess(@Nullable U result) {
                    promise.complete(result);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    promise.completeExceptionally(t);
                }
            });

            return promise;

        } else {
            if (future.isDone()) {
                try {
                    return completed(future.get());
                } catch (ExecutionException e) {
                    return exceptionally(e);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return Promise.supplyingExceptionallyAsync(future::get);
            }
        }
    }

    /**
     * If the promise is currently being supplied
     */
    private final AtomicBoolean supplied = new AtomicBoolean(false);

    /**
     * If the execution of the promise is cancelled
     */
    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    /**
     * The completable future backing this promise
     */
    @NonNull
    private final CompletableFuture<V> fut;

    private PapiPromise() {
        this.fut = new CompletableFuture<>();
    }

    private PapiPromise(@Nullable V v) {
        this.fut = CompletableFuture.completedFuture(v);
        this.supplied.set(true);
    }

    private PapiPromise(@NonNull Throwable t) {
        (this.fut = new CompletableFuture<>()).completeExceptionally(t);
        this.supplied.set(true);
    }

    private PapiPromise(@NonNull CompletableFuture<V> fut) {
        Objects.requireNonNull(fut, "message");
        this.fut = fut;
        this.supplied.set(true);
        this.cancelled.set(fut.isCancelled());
    }

    /* utility methods */

    private void executeSync(@NonNull Runnable runnable) {
        if (ThreadContext.forCurrentThread() == ThreadContext.SYNC) {
            PapiExecutors.wrapRunnable(runnable).run();
        } else {
            PapiExecutors.sync().execute(runnable);
        }
    }

    private void executeAsync(@NonNull Runnable runnable) {
        PapiExecutors.asyncPapi().execute(runnable);
    }

    private void executeDelayedSync(@NonNull Runnable runnable, long delayTicks) {
        if (delayTicks <= 0) {
            executeSync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), PapiExecutors.wrapRunnable(runnable), delayTicks);
        }
    }

    private void executeDelayedAsync(@NonNull Runnable runnable, long delayTicks) {
        if (delayTicks <= 0) {
            executeAsync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(LoaderUtils.getPlugin(), PapiExecutors.wrapRunnable(runnable), delayTicks);
        }
    }

    private void executeDelayedSync(@NonNull Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0) {
            executeSync(runnable);
        } else {
            Bukkit.getScheduler().runTaskLater(LoaderUtils.getPlugin(), PapiExecutors.wrapRunnable(runnable), Ticks.from(delay, unit));
        }
    }

    private void executeDelayedAsync(@NonNull Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0) {
            executeAsync(runnable);
        } else {
            PapiExecutors.asyncPapi().schedule(PapiExecutors.wrapRunnable(runnable), delay, unit);
        }
    }

    private void complete(V value) {
        if (!this.cancelled.get()) {
            this.fut.complete(value);
        }
    }

    private void completeExceptionally(@NonNull Throwable t) {
        if (!this.cancelled.get()) {
            this.fut.completeExceptionally(t);
        }
    }

    private void markAsSupplied() {
        if (!this.supplied.compareAndSet(false, true)) {
            throw new IllegalStateException("Promise is already being supplied.");
        }
    }

    /* future methods */

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        this.cancelled.set(true);
        return this.fut.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return this.fut.isCancelled();
    }

    @Override
    public boolean isDone() {
        return this.fut.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return this.fut.get();
    }

    @Override
    public V get(long timeout, @NonNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.fut.get(timeout, unit);
    }

    @Override
    public V join() {
        return this.fut.join();
    }

    @Override
    public V getNow(V valueIfAbsent) {
        return this.fut.getNow(valueIfAbsent);
    }

    @Override
    public CompletableFuture<V> toCompletableFuture() {
        return this.fut.thenApply(Function.identity());
    }

    @Override
    public void close() {
        cancel();
    }

    @Override
    public boolean isClosed() {
        return isCancelled();
    }

    /* implementation */

    @NonNull
    @Override
    public Promise<V> supply(@Nullable V value) {
        markAsSupplied();
        complete(value);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyException(@NonNull Throwable exception) {
        markAsSupplied();
        completeExceptionally(exception);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplySync(@NonNull Supplier<V> supplier) {
        markAsSupplied();
        executeSync(new SupplyRunnable(supplier));
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyAsync(@NonNull Supplier<V> supplier) {
        markAsSupplied();
        executeAsync(new SupplyRunnable(supplier));
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyDelayedSync(@NonNull Supplier<V> supplier, long delayTicks) {
        markAsSupplied();
        executeDelayedSync(new SupplyRunnable(supplier), delayTicks);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyDelayedSync(@NonNull Supplier<V> supplier, long delay, @NonNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedSync(new SupplyRunnable(supplier), delay, unit);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyDelayedAsync(@NonNull Supplier<V> supplier, long delayTicks) {
        markAsSupplied();
        executeDelayedAsync(new SupplyRunnable(supplier), delayTicks);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyDelayedAsync(@NonNull Supplier<V> supplier, long delay, @NonNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedAsync(new SupplyRunnable(supplier), delay, unit);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyExceptionallySync(@NonNull Callable<V> callable) {
        markAsSupplied();
        executeSync(new ThrowingSupplyRunnable(callable));
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyExceptionallyAsync(@NonNull Callable<V> callable) {
        markAsSupplied();
        executeAsync(new ThrowingSupplyRunnable(callable));
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyExceptionallyDelayedSync(@NonNull Callable<V> callable, long delayTicks) {
        markAsSupplied();
        executeDelayedSync(new ThrowingSupplyRunnable(callable), delayTicks);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyExceptionallyDelayedSync(@NonNull Callable<V> callable, long delay, @NonNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedSync(new ThrowingSupplyRunnable(callable), delay, unit);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyExceptionallyDelayedAsync(@NonNull Callable<V> callable, long delayTicks) {
        markAsSupplied();
        executeDelayedAsync(new ThrowingSupplyRunnable(callable), delayTicks);
        return this;
    }

    @NonNull
    @Override
    public Promise<V> supplyExceptionallyDelayedAsync(@NonNull Callable<V> callable, long delay, @NonNull TimeUnit unit) {
        markAsSupplied();
        executeDelayedAsync(new ThrowingSupplyRunnable(callable), delay, unit);
        return this;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenApplySync(@NonNull Function<? super V, ? extends U> fn) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeSync(new ApplyRunnable<>(promise, fn, value));
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenApplyAsync(@NonNull Function<? super V, ? extends U> fn) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeAsync(new ApplyRunnable<>(promise, fn, value));
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenApplyDelayedSync(@NonNull Function<? super V, ? extends U> fn, long delayTicks) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ApplyRunnable<>(promise, fn, value), delayTicks);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenApplyDelayedSync(@NonNull Function<? super V, ? extends U> fn, long delay, @NonNull TimeUnit unit) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ApplyRunnable<>(promise, fn, value), delay, unit);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenApplyDelayedAsync(@NonNull Function<? super V, ? extends U> fn, long delayTicks) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ApplyRunnable<>(promise, fn, value), delayTicks);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenApplyDelayedAsync(@NonNull Function<? super V, ? extends U> fn, long delay, @NonNull TimeUnit unit) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ApplyRunnable<>(promise, fn, value), delay, unit);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenComposeSync(@NonNull Function<? super V, ? extends Promise<U>> fn) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeSync(new ComposeRunnable<>(promise, fn, value, true));
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenComposeAsync(@NonNull Function<? super V, ? extends Promise<U>> fn) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeAsync(new ComposeRunnable<>(promise, fn, value, false));
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenComposeDelayedSync(@NonNull Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ComposeRunnable<>(promise, fn, value, true), delayTicks);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenComposeDelayedSync(@NonNull Function<? super V, ? extends Promise<U>> fn, long delay, @NonNull TimeUnit unit) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedSync(new ComposeRunnable<>(promise, fn, value, true), delay, unit);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenComposeDelayedAsync(@NonNull Function<? super V, ? extends Promise<U>> fn, long delayTicks) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ComposeRunnable<>(promise, fn, value, false), delayTicks);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public <U> Promise<U> thenComposeDelayedAsync(@NonNull Function<? super V, ? extends Promise<U>> fn, long delay, @NonNull TimeUnit unit) {
        PapiPromise<U> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t != null) {
                promise.completeExceptionally(t);
            } else {
                executeDelayedAsync(new ComposeRunnable<>(promise, fn, value, false), delay, unit);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public Promise<V> exceptionallySync(@NonNull Function<Throwable, ? extends V> fn) {
        PapiPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeSync(new ExceptionallyRunnable<>(promise, fn, t));
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public Promise<V> exceptionallyAsync(@NonNull Function<Throwable, ? extends V> fn) {
        PapiPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeAsync(new ExceptionallyRunnable<>(promise, fn, t));
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public Promise<V> exceptionallyDelayedSync(@NonNull Function<Throwable, ? extends V> fn, long delayTicks) {
        PapiPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public Promise<V> exceptionallyDelayedSync(@NonNull Function<Throwable, ? extends V> fn, long delay, @NonNull TimeUnit unit) {
        PapiPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedSync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public Promise<V> exceptionallyDelayedAsync(@NonNull Function<Throwable, ? extends V> fn, long delayTicks) {
        PapiPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delayTicks);
            }
        });
        return promise;
    }

    @NonNull
    @Override
    public Promise<V> exceptionallyDelayedAsync(@NonNull Function<Throwable, ? extends V> fn, long delay, @NonNull TimeUnit unit) {
        PapiPromise<V> promise = empty();
        this.fut.whenComplete((value, t) -> {
            if (t == null) {
                promise.complete(value);
            } else {
                executeDelayedAsync(new ExceptionallyRunnable<>(promise, fn, t), delay, unit);
            }
        });
        return promise;
    }

    /* delegating behaviour runnables */

    private final class ThrowingSupplyRunnable implements Runnable, Delegate<Callable<V>> {
        private final Callable<V> supplier;
        private ThrowingSupplyRunnable(Callable<V> supplier) {
            this.supplier = supplier;
        }
        @Override public Callable<V> getDelegate() { return this.supplier; }

        @Override
        public void run() {
            if (PapiPromise.this.cancelled.get()) {
                return;
            }
            try {
                fut.complete(this.supplier.call());
            } catch (Exception t) {
                EXCEPTION_CONSUMER.accept(t);
                fut.completeExceptionally(t);
            }
        }
    }

    private final class SupplyRunnable implements Runnable, Delegate<Supplier<V>> {
        private final Supplier<V> supplier;
        private SupplyRunnable(Supplier<V> supplier) {
            this.supplier = supplier;
        }
        @Override public Supplier<V> getDelegate() { return this.supplier; }

        @Override
        public void run() {
            if (PapiPromise.this.cancelled.get()) {
                return;
            }
            try {
                PapiPromise.this.fut.complete(this.supplier.get());
            } catch (Exception t) {
                EXCEPTION_CONSUMER.accept(t);
                PapiPromise.this.fut.completeExceptionally(t);
            }
        }
    }

    private final class ApplyRunnable<U> implements Runnable, Delegate<Function<? super V, ? extends U>> {
        private final PapiPromise<U> promise;
        private final Function<? super V, ? extends U> function;
        private final V value;
        private ApplyRunnable(PapiPromise<U> promise, Function<? super V, ? extends U> function, V value) {
            this.promise = promise;
            this.function = function;
            this.value = value;
        }
        @Override public Function<? super V, ? extends U> getDelegate() { return this.function; }

        @Override
        public void run() {
            if (PapiPromise.this.cancelled.get()) {
                return;
            }
            try {
                this.promise.complete(this.function.apply(this.value));
            } catch (Exception t) {
                EXCEPTION_CONSUMER.accept(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

    private final class ComposeRunnable<U> implements Runnable, Delegate<Function<? super V, ? extends Promise<U>>> {
        private final PapiPromise<U> promise;
        private final Function<? super V, ? extends Promise<U>> function;
        private final V value;
        private final boolean sync;
        private ComposeRunnable(PapiPromise<U> promise, Function<? super V, ? extends Promise<U>> function, V value, boolean sync) {
            this.promise = promise;
            this.function = function;
            this.value = value;
            this.sync = sync;
        }
        @Override public Function<? super V, ? extends Promise<U>> getDelegate() { return this.function; }

        @Override
        public void run() {
            if (PapiPromise.this.cancelled.get()) {
                return;
            }
            try {
                Promise<U> p = this.function.apply(this.value);
                if (p == null) {
                    this.promise.complete(null);
                } else {
                    if (this.sync) {
                        p.thenAcceptSync(this.promise::complete);
                    } else {
                        p.thenAcceptAsync(this.promise::complete);
                    }
                }
            } catch (Exception t) {
                EXCEPTION_CONSUMER.accept(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

    private final class ExceptionallyRunnable<U> implements Runnable, Delegate<Function<Throwable, ? extends U>> {
        private final PapiPromise<U> promise;
        private final Function<Throwable, ? extends U> function;
        private final Throwable t;
        private ExceptionallyRunnable(PapiPromise<U> promise, Function<Throwable, ? extends U> function, Throwable t) {
            this.promise = promise;
            this.function = function;
            this.t = t;
        }
        @Override public Function<Throwable, ? extends U> getDelegate() {
            return this.function;
        }

        @Override
        public void run() {
            if (PapiPromise.this.cancelled.get()) {
                return;
            }
            try {
                this.promise.complete(this.function.apply(this.t));
            } catch (Exception t) {
                EXCEPTION_CONSUMER.accept(t);
                this.promise.completeExceptionally(t);
            }
        }
    }

}
