package com.pepej.papi.config.typeserializers;

import com.pepej.papi.time.DurationFormatter;
import com.pepej.papi.time.DurationParser;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationTypeSerializer implements TypeSerializer<Duration> {

    public static final DurationTypeSerializer INSTANCE = new DurationTypeSerializer();

    private DurationTypeSerializer() {
    }

    @Override
    public Duration deserialize(final Type type, final ConfigurationNode node) throws SerializationException {
        return DurationParser.parse(node.getString());
    }

    @Override
    public void serialize(final Type type, @Nullable final Duration obj, final ConfigurationNode node) throws SerializationException {
        node.set(String.class, DurationFormatter.LONG.format(obj));
    }
}
