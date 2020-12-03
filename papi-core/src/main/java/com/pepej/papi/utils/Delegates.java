package com.pepej.papi.utils;

import com.pepej.papi.interfaces.Delegate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * A collection of utility methods for delegating Java 8 functions
 */
public final class Delegates {

    public static <T> Consumer<T> runnableToConsumer(@NonNull final Runnable runnable) {
        return new RunnableToConsumer<>(runnable);
    }

    public static Supplier<Void> runnableToSupplier(@NonNull final Runnable runnable) {
        return new RunnableToSupplier<>(runnable);
    }

    public static <T> Supplier<T> callableToSupplier(@NonNull final Callable<T> callable) {
        return new CallableToSupplier<>(callable);
    }

    public static <T, U> BiConsumer<T, U> consumerToBiConsumerFirst(@NonNull final Consumer<T> consumer) {
        return new ConsumerToBiConsumerFirst<>(consumer);
    }

    public static <T, U> BiConsumer<T, U> consumerToBiConsumerSecond(@NonNull final Consumer<U> consumer) {
        return new ConsumerToBiConsumerSecond<>(consumer);
    }

    public static <T, U> BiPredicate<T, U> predicateToBiPredicateFirst(@NonNull final Predicate<T> predicate) {
        return new PredicateToBiPredicateFirst<>(predicate);
    }

    public static <T, U> BiPredicate<T, U> predicateToBiPredicateSecond(@NonNull final Predicate<U> predicate) {
        return new PredicateToBiPredicateSecond<>(predicate);
    }

    public static <T, U> Function<T, U> consumerToFunction(@NonNull final Consumer<T> consumer) {
        return new ConsumerToFunction<>(consumer);
    }

    public static <T, U> Function<T, U> runnableToFunction(@NonNull final Runnable runnable) {
        return new RunnableToFunction<>(runnable);
    }

    private static abstract class AbstractDelegate<T> implements Delegate<T> {
        final T delegate;

        AbstractDelegate(@NonNull final T delegate) {
            this.delegate = delegate;
        }

        @Override
        public T getDelegate() {
            return delegate;
        }
    }

    private static final class RunnableToConsumer<T> extends AbstractDelegate<Runnable> implements Consumer<T> {
        RunnableToConsumer(@NonNull final Runnable delegate) {
            super(delegate);
        }

        @Override
        public void accept(@NonNull final T t) {
            this.delegate.run();
        }
    }

    private static final class CallableToSupplier<T> extends AbstractDelegate<Callable<T>> implements Supplier<T> {
        CallableToSupplier(@NonNull final Callable<T> delegate) {
            super(delegate);
        }

        @Override
        public T get() {
            try {
                return this.delegate.call();
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final class RunnableToSupplier<T> extends AbstractDelegate<Runnable> implements Supplier<T> {
        RunnableToSupplier(@NonNull final Runnable delegate) {
            super(delegate);
        }

        @Override
        @Nullable
        public T get() {
            this.delegate.run();
            return null;
        }
    }

    private static final class ConsumerToBiConsumerFirst<T, U> extends AbstractDelegate<Consumer<T>> implements BiConsumer<T, U> {
        ConsumerToBiConsumerFirst(@NonNull final Consumer<T> delegate) {
            super(delegate);
        }

        @Override
        public void accept(@NonNull final T t, @NonNull final U u) {
            this.delegate.accept(t);
        }
    }

    private static final class ConsumerToBiConsumerSecond<T, U> extends AbstractDelegate<Consumer<U>> implements BiConsumer<T, U> {
        ConsumerToBiConsumerSecond(@NonNull final Consumer<U> delegate) {
            super(delegate);
        }

        @Override
        public void accept(@NonNull final T t, @NonNull final U u) {
            this.delegate.accept(u);
        }
    }

    private static final class PredicateToBiPredicateFirst<T, U> extends AbstractDelegate<Predicate<T>> implements BiPredicate<T, U> {
        PredicateToBiPredicateFirst(@NonNull final Predicate<T> delegate) {
            super(delegate);
        }

        @Override
        public boolean test(@NonNull final T t, @NonNull final U u) {
            return this.delegate.test(t);
        }
    }

    private static final class PredicateToBiPredicateSecond<T, U> extends AbstractDelegate<Predicate<U>> implements BiPredicate<T, U> {
        PredicateToBiPredicateSecond(@NonNull final Predicate<U> delegate) {
            super(delegate);
        }

        @Override
        public boolean test(@NonNull final T t, @NonNull final U u) {
            return this.delegate.test(u);
        }
    }

    private static final class ConsumerToFunction<T, R> extends AbstractDelegate<Consumer<T>> implements Function<T, R> {
        ConsumerToFunction(@NonNull final Consumer<T> delegate) {
            super(delegate);
        }

        @Nullable
        @Override
        public R apply(@NonNull final T t) {
            this.delegate.accept(t);
            return null;
        }
    }

    private static final class RunnableToFunction<T, R> extends AbstractDelegate<Runnable> implements Function<T, R> {
        RunnableToFunction(@NonNull final Runnable delegate) {
            super(delegate);
        }

        @Nullable
        @Override
        public R apply(@NonNull final T t) {
            this.delegate.run();
            return null;
        }
    }

    private Delegates() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}
