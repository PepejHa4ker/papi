package com.pepej.papi.config.typeserializers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.pepej.papi.gson.converter.GsonConverters;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public final class GsonTypeSerializer implements TypeSerializer<JsonElement> {
    private static final TypeToken<JsonElement> TYPE = TypeToken.of(JsonElement.class);

    public static final GsonTypeSerializer INSTANCE = new GsonTypeSerializer();

    private GsonTypeSerializer() {
    }


    @Override
    public JsonElement deserialize(Type type, ConfigurationNode from) throws SerializationException {
        if (from.isList()) {
            List<? extends ConfigurationNode> childrenList = from.childrenList();
            JsonArray array = new JsonArray();
            for (ConfigurationNode node : childrenList) {
                array.add(node.get(JsonElement.class));
            }
            return array;
        }

        if (from.isMap()) {
            Map<Object, ? extends ConfigurationNode> childrenMap = from.childrenMap();
            JsonObject object = new JsonObject();
            for (Map.Entry<Object, ? extends ConfigurationNode> ent : childrenMap.entrySet()) {
                object.add(ent.getKey().toString(), ent.getValue().get(JsonElement.class));
            }
            return object;
        }

        Object val = from.get(Object.class);
        return GsonConverters.IMMUTABLE.wrap(val);

    }

    @Override
    public void serialize(Type type, @Nullable JsonElement from, ConfigurationNode to) throws SerializationException {
        if (from.isJsonPrimitive()) {
            JsonPrimitive primitive = from.getAsJsonPrimitive();
            to.set(GsonConverters.IMMUTABLE.unwarpPrimitive(primitive));
        } else if (from.isJsonNull()) {
            to.set(null);
        } else if (from.isJsonArray()) {
            JsonArray array = from.getAsJsonArray();
            // ensure 'to' is a list node
            to.set(ImmutableList.of());
            for (JsonElement element : array) {
                serialize(TYPE.getType(), element, to.appendListNode());
            }
        } else if (from.isJsonObject()) {
            JsonObject object = from.getAsJsonObject();
            // ensure 'to' is a map node
            to.set(ImmutableMap.of());
            for (Map.Entry<String, JsonElement> ent : object.entrySet()) {
                serialize(TYPE.getType(), ent.getValue(), to.node(ent.getKey()));
            }
        }
    }
}
