package com.pepej.papi.datatree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ConfigurateDataTree implements DataTree {
    private final ConfigurationNode node;

    public ConfigurateDataTree(ConfigurationNode node) {
        this.node = Objects.requireNonNull(node, "node");
    }

    public ConfigurationNode getNode() {
        return this.node;
    }

    @NonNull
    @Override
    public ConfigurateDataTree resolve(@NonNull Object... path) {
        return new ConfigurateDataTree(this.node.node(path));
    }

    @NonNull
    @Override
    public Stream<Map.Entry<String, ConfigurateDataTree>> asObject() {
        Preconditions.checkState(this.node.isMap(), "node does not have map children");
        return this.node.childrenMap().entrySet().stream()
                        .map(entry -> Maps.immutableEntry(entry.getKey().toString(), new ConfigurateDataTree(entry.getValue())));
    }

    @NonNull
    @Override
    public Stream<ConfigurateDataTree> asArray() {
        Preconditions.checkState(this.node.isList(), "node does not have list children");
        return this.node.childrenList().stream().map(ConfigurateDataTree::new);
    }

    @NonNull
    @Override
    public Stream<Map.Entry<Integer, ConfigurateDataTree>> asIndexedArray() {
        Preconditions.checkState(this.node.isList(), "node does not have list children");
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<Map.Entry<Integer, ConfigurateDataTree>>() {
            private final Iterator<? extends ConfigurationNode> iterator = ConfigurateDataTree.this.node.childrenList().iterator();
            private int index = 0;

            @Override
            public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override
            public Map.Entry<Integer, ConfigurateDataTree> next() {
                return Maps.immutableEntry(this.index++, new ConfigurateDataTree(this.iterator.next()));
            }
        }, Spliterator.ORDERED | Spliterator.IMMUTABLE), false);
    }

    @SneakyThrows
    @NonNull
    @Override
    public String asString() {
        return Objects.requireNonNull(node.getString());
    }

    @SneakyThrows
    @NonNull
    @Override
    public Number asNumber() {
        return Objects.requireNonNull(this.node.get(Number.class));
    }

    @Override
    public int asInt() {
        return this.node.getInt();
    }

    @Override
    public double asDouble() {
        return this.node.getDouble();
    }

    @Override
    public boolean asBoolean() {
        return this.node.getBoolean();
    }
}
