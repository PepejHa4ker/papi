package com.pepej.papi.config.typeserializers;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.pepej.papi.datatree.ConfigurateDataTree;
import com.pepej.papi.datatree.DataTree;
import com.pepej.papi.datatree.GsonDataTree;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public final class JsonTreeTypeSerializer implements TypeSerializer<DataTree> {
    private static final TypeToken<JsonElement> JSON_ELEMENT_TYPE = TypeToken.of(JsonElement.class);

    public static final JsonTreeTypeSerializer INSTANCE = new JsonTreeTypeSerializer();

    @Override
    public DataTree deserialize(TypeToken<?> typeToken, ConfigurationNode node) throws ObjectMappingException {
        return DataTree.from(node.getValue(JSON_ELEMENT_TYPE));
    }

    @Override
    public void serialize(TypeToken<?> typeToken, DataTree dataTree, ConfigurationNode node) throws ObjectMappingException {
        if (dataTree instanceof GsonDataTree) {
            node.setValue(JSON_ELEMENT_TYPE, ((GsonDataTree) dataTree).getElement());
        } else if (dataTree instanceof ConfigurateDataTree) {
            node.setValue(((ConfigurateDataTree) dataTree).getNode());
        } else {
            throw new ObjectMappingException("Unknown type: " + dataTree.getClass().getName());
        }
    }
}
