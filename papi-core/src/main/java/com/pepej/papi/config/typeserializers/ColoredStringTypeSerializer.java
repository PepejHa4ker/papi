package com.pepej.papi.config.typeserializers;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.text.Text;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public class ColoredStringTypeSerializer implements TypeSerializer<String> {

    public static final ColoredStringTypeSerializer INSTANCE = new ColoredStringTypeSerializer();

    private ColoredStringTypeSerializer() {
    }

    @Override
    public String deserialize(TypeToken<?> type, ConfigurationNode node) {
        return Text.colorize(node.getString());
    }

    @Override
    public void serialize(TypeToken<?> type, String s, ConfigurationNode node) {
        node.setValue(Text.decolorize(s));
    }
}
