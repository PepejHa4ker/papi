package com.pepej.papi.config.typeserializers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.pepej.papi.gson.converter.GsonConverters;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

import java.util.List;
import java.util.Map;

public final class GsonTypeSerializer implements TypeSerializer<JsonElement> {
    private static final TypeToken<JsonElement> TYPE = TypeToken.of(JsonElement.class);

    public static final GsonTypeSerializer INSTANCE = new GsonTypeSerializer();

    private GsonTypeSerializer() {
    }

    @Override
    public JsonElement deserialize(TypeToken<?> type, ConfigurationNode from) throws ObjectMappingException {
        if (from.getValue() == null) {
            return JsonNull.INSTANCE;
        }

        if (from.hasListChildren()) {
            List<? extends ConfigurationNode> childrenList = from.getChildrenList();
            JsonArray array = new JsonArray();
            for (ConfigurationNode node : childrenList) {
                array.add(node.getValue(TYPE));
            }
            return array;
        }

        if (from.hasMapChildren()) {
            Map<Object, ? extends ConfigurationNode> childrenMap = from.getChildrenMap();
            JsonObject object = new JsonObject();
            for (Map.Entry<Object, ? extends ConfigurationNode> ent : childrenMap.entrySet()) {
                object.add(ent.getKey().toString(), ent.getValue().getValue(TYPE));
            }
            return object;
        }

        Object val = from.getValue();
        try {
            return GsonConverters.IMMUTABLE.wrap(val);
        } catch (IllegalArgumentException e) {
            throw new ObjectMappingException(e);
        }
    }

    @Override
    public void serialize(TypeToken<?> type, JsonElement from, ConfigurationNode to) throws ObjectMappingException {
        if (from.isJsonPrimitive()) {
            JsonPrimitive primitive = from.getAsJsonPrimitive();
            to.setValue(GsonConverters.IMMUTABLE.unwarpPrimitive(primitive));
        } else if (from.isJsonNull()) {
            to.setValue(null);
        } else if (from.isJsonArray()) {
            JsonArray array = from.getAsJsonArray();
            // ensure 'to' is a list node
            to.setValue(ImmutableList.of());
            for (JsonElement element : array) {
                serialize(TYPE, element, to.getAppendedNode());
            }
        } else if (from.isJsonObject()) {
            JsonObject object = from.getAsJsonObject();
            // ensure 'to' is a map node
            to.setValue(ImmutableMap.of());
            for (Map.Entry<String, JsonElement> ent : object.entrySet()) {
                serialize(TYPE, ent.getValue(), to.getNode(ent.getKey()));
            }
        } else {
            throw new ObjectMappingException("Unknown element type: " + from.getClass());
        }
    }
}
