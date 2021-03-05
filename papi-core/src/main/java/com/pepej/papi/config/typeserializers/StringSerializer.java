package com.pepej.papi.config.typeserializers;

import com.pepej.papi.text.Text;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class StringSerializer implements TypeSerializer<String> {

    public static final StringSerializer INSTANCE = new StringSerializer();

    private StringSerializer() {
    }

    @Override
    public String deserialize(Type type, ConfigurationNode node) {
        return Text.colorize(node.getString());
    }

    @Override
    public void serialize(Type type, String s, ConfigurationNode node) throws SerializationException {
        node.set(Text.decolorize(s));
    }
}