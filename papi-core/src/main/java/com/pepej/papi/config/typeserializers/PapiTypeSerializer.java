package com.pepej.papi.config.typeserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.pepej.papi.gson.GsonSerializable;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PapiTypeSerializer implements TypeSerializer<GsonSerializable> {
    private static final TypeToken<JsonElement> JSON_ELEMENT_TYPE = TypeToken.of(JsonElement.class);

    public static final PapiTypeSerializer INSTANCE = new PapiTypeSerializer();

    private PapiTypeSerializer() {
    }

    @Override
    public GsonSerializable deserialize(TypeToken<?> type, ConfigurationNode node) throws ObjectMappingException {
        return GsonSerializable.deserializeRaw(type.getRawType(), node.getValue(JSON_ELEMENT_TYPE, JsonNull.INSTANCE));
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, GsonSerializable s, ConfigurationNode node) throws ObjectMappingException {
        node.setValue(JSON_ELEMENT_TYPE, s.serialize());
    }
}
