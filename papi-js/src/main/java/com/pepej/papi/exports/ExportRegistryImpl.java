package com.pepej.papi.exports;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

final class ExportRegistryImpl implements ExportRegistry, Function<String, Export<?>> {
    private final Map<String, Export<?>> exports = new ConcurrentHashMap<>();

    @Override
    public Export<?> apply(String s) {
        return new ExportImpl<>(s);
    }

    @Override
    public <T> Export<T> get(String name) {
        //noinspection unchecked
        return (Export<T>) this.exports.computeIfAbsent(name.toLowerCase(), this);
    }

    @Override
    public void remove(String name) {
        Export<?> export = this.exports.get(name.toLowerCase());
        if (export != null) {
            export.clear();
        }
    }

    @Override
    public Collection<Export<?>> getAll() {
        return Collections.unmodifiableCollection(this.exports.values());
    }
}
