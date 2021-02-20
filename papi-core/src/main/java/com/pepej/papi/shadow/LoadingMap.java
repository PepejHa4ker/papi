package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

class LoadingMap<K, V> implements Map<K, V> {
    static <K, V> LoadingMap<K, V> of(final Map<K, V> map, final Function<K, V> function) {
        return new LoadingMap<>(map, function);
    }

    static <K, V> LoadingMap<K, V> of(final Function<K, V> function) {
        return of(new ConcurrentHashMap<>(), function);
    }

    private final Map<K, V> map;
    private final Function<K, V> function;

    LoadingMap(final Map<K, V> map, final Function<K, V> function) {
        this.map = map;
        this.function = function;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.map.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        final V value = this.map.get(key);
        if(value != null) {
            return value;
        }
        //noinspection unchecked
        return this.map.computeIfAbsent((K) key, this.function);
    }

    public V getIfPresent(Object key) {
        //noinspection SuspiciousMethodCalls
        return this.map.get(key);
    }

    @Override
    public V put(final K key, final V value) {
        return this.map.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        return this.map.remove(key);
    }

    @Override
    public void putAll(final @NonNull Map<? extends K, ? extends V> that) {
        this.map.putAll(that);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    @NonNull
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    @NonNull
    public Collection<V> values() {
        return this.map.values();
    }

    @Override
    @NonNull
    public Set<Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }
}
