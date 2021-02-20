package com.pepej.papi.command.argument;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings("rawtypes")
public class SimpleParserRegistry implements ArgumentParserRegistry {
    private final Map<TypeToken<?>, List<ArgumentParser<?>>> parsers = new ConcurrentHashMap<>();

    @NonNull
    @Override
    public <T> Optional<ArgumentParser<T>> find(@NonNull TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return Optional.empty();
        }

        //noinspection unchecked
        return Optional.of((ArgumentParser<T>) parsers.get(0));
    }

    @NonNull
    @Override
    public <T> Collection<ArgumentParser<T>> findAll(@NonNull TypeToken<T> type) {
        Objects.requireNonNull(type, "type");
        List<ArgumentParser<?>> parsers = this.parsers.get(type);
        if (parsers == null || parsers.isEmpty()) {
            return ImmutableList.of();
        }

        //noinspection unchecked
        return (Collection) Collections.unmodifiableList(parsers);
    }

    @Override
    public <T> void register(@NonNull TypeToken<T> type, @NonNull ArgumentParser<T> parser) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(parser, "parser");
        List<ArgumentParser<?>> list = this.parsers.computeIfAbsent(type, t -> new CopyOnWriteArrayList<>());
        if (!list.contains(parser)) {
            list.add(parser);
        }
    }
}