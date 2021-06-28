package com.pepej.papi.metadata;

import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents a value which will expire if condition will return true
 *
 * @param <T> the wrapped value type
 */
public class ConditionalValue<T> implements TransientValue<T> {

    private final Predicate<? super T> expireCondition;
    private final T value;

    public static <T, E> ConditionalValue<T> of(T value, Predicate<? super T> expireCondition) {
        Objects.requireNonNull(value, "value");
        Objects.requireNonNull(expireCondition, "expireCondition");
        return new ConditionalValue<>(value, expireCondition);
    }

    private ConditionalValue(T value, Predicate<? super T> expireCondition) {
        this.value = value;
        this.expireCondition = expireCondition;
    }

    @Nullable
    @Override
    public T getOrNull() {
        if (shouldExpire()) {
            return null;
        }

        return this.value;
    }

    @Override
    public boolean shouldExpire() {
        return expireCondition.test(value);
    }
}
