package com.pepej.papi.gson.typeadapters;

import com.google.gson.*;
import com.pepej.papi.datatree.DataTree;
import com.pepej.papi.datatree.GsonDataTree;

import java.lang.reflect.Type;

public final class JsonElementTreeSerializer implements JsonSerializer<DataTree>, JsonDeserializer<DataTree> {
    public static final JsonElementTreeSerializer INSTANCE = new JsonElementTreeSerializer();

    private JsonElementTreeSerializer() {

    }

    @Override
    public DataTree deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DataTree.from(json);
    }

    @Override
    public JsonElement serialize(DataTree src, Type typeOfSrc, JsonSerializationContext context) {
        return ((GsonDataTree) src).getElement();
    }
}
