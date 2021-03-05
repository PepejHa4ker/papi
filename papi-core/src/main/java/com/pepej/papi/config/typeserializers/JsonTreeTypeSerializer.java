package com.pepej.papi.config.typeserializers;

import com.google.gson.JsonElement;
import com.pepej.papi.datatree.ConfigurateDataTree;
import com.pepej.papi.datatree.DataTree;
import com.pepej.papi.datatree.GsonDataTree;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.util.Objects;

public final class JsonTreeTypeSerializer implements TypeSerializer<DataTree> {

    public static final JsonTreeTypeSerializer INSTANCE = new JsonTreeTypeSerializer();

    @Override
    public DataTree deserialize(@NonNull Type type, ConfigurationNode node) throws SerializationException  {
        return DataTree.from(Objects.requireNonNull(node.get(JsonElement.class)));
    }

    @Override
    public void serialize(@NonNull Type type, DataTree dataTree, @NonNull ConfigurationNode node) throws SerializationException {
        if (dataTree instanceof GsonDataTree) {
            node.set(JsonElement.class, ((GsonDataTree) dataTree).getElement());
        } else if (dataTree instanceof ConfigurateDataTree) {
            node.set(((ConfigurateDataTree) dataTree).getNode());
        }
    }
}
