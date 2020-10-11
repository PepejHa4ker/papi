package com.pepej.papi.exports;


import jdk.nashorn.api.scripting.AbstractJSObject;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

/**
 * Atomic implementation of {@link Export}.
 *
 * @param <T> the type
 */
final class ExportImpl<T> implements Export<T> {
    private final String name;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private T value = null;
    private Pointer<T> pointer = null;

    ExportImpl(String name) {
        this.name = name;
    }

    private Lock readLock() { return this.lock.readLock(); }
    private Lock writeLock() { return this.lock.writeLock(); }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public synchronized Pointer<T> pointer() {
        if (this.pointer == null) {
            this.pointer = new PointerImpl<>(this);
        }
        return this.pointer;
    }

    @Override
    public T get() {
        readLock().lock();
        try {
            return this.value;
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public T get(T other) {
        T value = get();
        return value != null ? value : other;
    }

    @Override
    public Export<T> put(T value) {
        writeLock().lock();
        try {
            this.value = value;
        } finally {
            writeLock().unlock();
        }
        return this;
    }

    @Override
    public Export<T> putIfAbsent(T value) {
        writeLock().lock();
        try {
            if (this.value == null) {
                this.value = value;
            }
        } finally {
            writeLock().unlock();
        }
        return this;
    }

    @Override
    public Export<T> computeIfAbsent(Supplier<? extends T> other) {
        writeLock().lock();
        try {
            if (this.value == null) {
                this.value = other.get();
            }
        } finally {
            writeLock().unlock();
        }
        return this;
    }

    @Override
    public boolean containsValue() {
        readLock().lock();
        try {
            return this.value != null;
        } finally {
            readLock().unlock();
        }
    }

    @Override
    public void clear() {
        writeLock().lock();
        try {
            this.value = null;
        } finally {
            writeLock().unlock();
        }
    }

    private static final class PointerImpl<T> extends AbstractJSObject implements Pointer<T> {
        private final Export<T> export;

        private PointerImpl(Export<T> export) {
            this.export = export;
        }

        @Override
        public boolean isFunction() {
            return true;
        }

        @Override
        public Object call(Object thiz, Object... args) {
            return get();
        }

        @Override
        public T get() {
            return this.export.get();
        }
    }

}
