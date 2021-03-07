package com.pepej.papi.config.typeserializers;

import com.google.common.reflect.TypeToken;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BukkitTypeSerializer implements TypeSerializer<ConfigurationSerializable> {
    private static final TypeToken<Map<String, Object>> TYPE = new TypeToken<Map<String, Object>>(){};

    public static final BukkitTypeSerializer INSTANCE = new BukkitTypeSerializer();

    private BukkitTypeSerializer() {
    }


    private static void deserializeChildren(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map) {
                try {
                    //noinspection unchecked
                    Map<String, Object> value = (Map<String, Object>) entry.getValue();

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

    @Override
    @SuppressWarnings("unchecked")
    public ConfigurationSerializable deserialize(Type type, ConfigurationNode node) throws SerializationException {
        Map<String, Object> map = (Map<String, Object>) node.get(TYPE.getRawType());
        deserializeChildren(map);
        return ConfigurationSerialization.deserializeObject(map);
    }

    @Override
    public void serialize(Type type, @Nullable ConfigurationSerializable obj, ConfigurationNode node) throws SerializationException {
        Map<String, Object> serialized = obj.serialize();

        Map<String, Object> map = new LinkedHashMap<>(serialized.size() + 1);
        map.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(obj.getClass()));
        map.putAll(serialized);

        node.set(map);
    }
}
