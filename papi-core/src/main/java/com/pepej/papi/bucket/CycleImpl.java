package com.pepej.papi.bucket;

import com.google.common.collect.ImmutableList;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

final class CycleImpl<E> implements Cycle<E> {

    /**
     * The list that backs this instance
     */
    private final List<E> objects;

    /**
     * The number of elements in the cycle
     */
    private final int size;

    /**
     * The current position of the cursor
     */
    private final AtomicInteger cursor = new AtomicInteger(0);

    CycleImpl(@NonNull List<E> objects) {
        if (objects.isEmpty()) {
            throw new IllegalArgumentException("List of objects cannot be empty.");
        }
        this.objects = ImmutableList.copyOf(objects);
        this.size = this.objects.size();
    }

    private CycleImpl(CycleImpl<E> other) {
        this.objects = other.objects;
        this.size = other.size;
    }

    @Override
    public int cursor() {
        return this.cursor.get();
    }

    @Override
    public void setCursor(int index) {
        if (index < 0 || index >= this.size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size);
        }
        this.cursor.set(index);
    }

    @NonNull
    @Override
    public E current() {
        return this.objects.get(cursor());
    }

    @NonNull
    @Override
    public E next() {
        return this.objects.get(this.cursor.updateAndGet(i -> {
            int n = i + 1;
            if (n >= this.size) {
                return 0;
            }
            return n;
        }));
    }

    @NonNull
    @Override
    public E previous() {
        return this.objects.get(this.cursor.updateAndGet(i -> {
            if (i == 0) {
                return this.size - 1;
            }
            return i - 1;
        }));
    }

    @Override
    public int nextPosition() {
        int n = this.cursor.get() + 1;
        if (n >= this.size) {
            return 0;
        }
        return n;
    }

    @Override
    public int previousPosition() {
        int i = this.cursor.get();
        if (i == 0) {
            return this.size - 1;
        }
        return i - 1;
    }

    @NonNull
    @Override
    public E peekNext() {
        return this.objects.get(nextPosition());
    }

    @NonNull
    @Override
    public E peekPrevious() {
        return this.objects.get(previousPosition());
    }

    @NonNull
    @Override
    public List<E> getBacking() {
        return this.objects;
    }

    @Override
    public Cycle<E> copy() {
        return new CycleImpl<>(this);
    }
}
