package com.pepej.papi.config.typeserializers;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.LinkedHashMap;
import java.util.Map;

public final class BukkitTypeSerializer implements TypeSerializer<ConfigurationSerializable> {
    private static final TypeToken<Map<String, Object>> TYPE = new TypeToken<Map<String, Object>>(){};

    public static final BukkitTypeSerializer INSTANCE = new BukkitTypeSerializer();

    private BukkitTypeSerializer() {
    }

    @Override
    public ConfigurationSerializable deserialize(TypeToken<?> type, ConfigurationNode from) throws ObjectMappingException {
        Map<String, Object> map = from.getValue(TYPE);
        deserializeChildren(map);
        return ConfigurationSerialization.deserializeObject(map);
    }

    @Override
    public void serialize(TypeToken<?> type, ConfigurationSerializable from, ConfigurationNode to) throws ObjectMappingException {
        Map<String, Object> serialized = from.serialize();

        Map<String, Object> map = new LinkedHashMap<>(serialized.size() + 1);
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(from.getClass()));
        map.putAll(serialized);

        to.setValue(map);
    }

    private static void deserializeChildren(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                try {
                    //noinspection unchecked
                    Map<String, Object> value = (Map) entry.getValue();

                    deserializeChildren(value);

                    if (value.containsKey("==")) {
                        entry.setValue(ConfigurationSerialization.deserializeObject(value));
                    }

                } catch (Exception e) {
                    // ignore
                }
            }

            if (entry.getValue() instanceof Number) {
                double doubleVal = ((Number) entry.getValue()).doubleValue();
                int intVal = (int) doubleVal;
                long longVal = (long) doubleVal;

                if (intVal == doubleVal) {
                    entry.setValue(intVal);
                } else if (longVal == doubleVal) {
                    entry.setValue(longVal);
                } else {
                    entry.setValue(doubleVal);
                }
            }
        }
    }
}
