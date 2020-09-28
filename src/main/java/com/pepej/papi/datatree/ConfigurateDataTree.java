package com.pepej.papi.datatree;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.Types;

import javax.annotation.Nonnull;
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

    @Nonnull
    @Override
    public ConfigurateDataTree resolve(@Nonnull Object... path) {
        return new ConfigurateDataTree(this.node.getNode(path));
    }

    @Nonnull
    @Override
    public Stream<Map.Entry<String, ConfigurateDataTree>> asObject() {
        Preconditions.checkState(this.node.hasMapChildren(), "node does not have map children");
        return this.node.getChildrenMap().entrySet().stream()
                        .map(entry -> Maps.immutableEntry(entry.getKey().toString(), new ConfigurateDataTree(entry.getValue())));
    }

    @Nonnull
    @Override
    public Stream<ConfigurateDataTree> asArray() {
        Preconditions.checkState(this.node.hasListChildren(), "node does not have list children");
        return this.node.getChildrenList().stream().map(ConfigurateDataTree::new);
    }

    @Nonnull
    @Override
    public Stream<Map.Entry<Integer, ConfigurateDataTree>> asIndexedArray() {
        Preconditions.checkState(this.node.hasListChildren(), "node does not have list children");
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<Map.Entry<Integer, ConfigurateDataTree>>() {
            private final Iterator<? extends ConfigurationNode> iterator = ConfigurateDataTree.this.node.getChildrenList().iterator();
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

    @Nonnull
    @Override
    public String asString() {
        return this.node.getValue(Types::asString);
    }

    @Nonnull
    @Override
    public Number asNumber() {
        return this.node.getValue(Types::asDouble);
    }

    @Override
    public int asInt() {
        return this.node.getValue(Types::asInt);
    }

    @Override
    public double asDouble() {
        return this.node.getValue(Types::asDouble);
    }

    @Override
    public boolean asBoolean() {
        return this.node.getValue(Types::asBoolean);
    }
}
