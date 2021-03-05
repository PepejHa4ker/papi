package com.pepej.papi.config.typeserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.pepej.papi.gson.GsonSerializable;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public final class PapiTypeSerializer implements TypeSerializer<GsonSerializable> {
    private static final TypeToken<JsonElement> JSON_ELEMENT_TYPE = TypeToken.of(JsonElement.class);

    public static final PapiTypeSerializer INSTANCE = new PapiTypeSerializer();

    private PapiTypeSerializer() {
    }


    @Override
    public GsonSerializable deserialize(Type type, ConfigurationNode node) throws SerializationException {
        return GsonSerializable.deserializeRaw(JSON_ELEMENT_TYPE.getRawType(), node.get(JsonElement.class, JsonNull.INSTANCE));
    }

    @Override
    public void serialize(Type type, @Nullable GsonSerializable obj, ConfigurationNode node) throws SerializationException {
        node.set(JsonElement.class, obj.serialize());
    }
}
