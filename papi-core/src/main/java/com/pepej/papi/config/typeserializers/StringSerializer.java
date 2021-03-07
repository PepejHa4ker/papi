package com.pepej.papi.config.typeserializers;

import com.pepej.papi.text.Text;
import com.pepej.papi.utils.Log;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

public class StringSerializer implements TypeSerializer<String> {

    public static final StringSerializer INSTANCE = new StringSerializer();
    private int counter;

    private StringSerializer() {
    }

    @Override
    public String deserialize(Type type, ConfigurationNode node) {
        Log.info("%s", ++counter);
        return Text.colorize(node.getString());

    }

    @Override
    public void serialize(Type type, String s, ConfigurationNode node) throws SerializationException {
        Log.info("%s", ++counter);
        node.set(Text.decolorize(s));
    }
}