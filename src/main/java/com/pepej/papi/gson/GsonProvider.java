package com.pepej.papi.gson;

import com.google.gson.*;
import com.pepej.papi.datatree.DataTree;
import com.pepej.papi.gson.typeadapters.BukkitSerializableAdapterFactory;
import com.pepej.papi.gson.typeadapters.GsonSerializableAdapterFactory;
import com.pepej.papi.gson.typeadapters.JsonElementTreeSerializer;
import net.kyori.text.serializer.gson.GsonComponentSerializer;

import javax.annotation.Nonnull;
import java.io.Reader;
import java.util.Objects;

/**
 * Provides static instances of Gson
 */
public final class GsonProvider {

    private static final Gson STANDARD_GSON = GsonComponentSerializer.populate(new GsonBuilder())
                                                                     .registerTypeHierarchyAdapter(DataTree.class, JsonElementTreeSerializer.INSTANCE)
                                                                     .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
                                                                     .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
                                                                     .serializeNulls()
                                                                     .disableHtmlEscaping()
                                                                     .create();

    private static final Gson PRETTY_PRINT_GSON = GsonComponentSerializer.populate(new GsonBuilder())
                                                                         .registerTypeHierarchyAdapter(DataTree.class, JsonElementTreeSerializer.INSTANCE)
                                                                         .registerTypeAdapterFactory(GsonSerializableAdapterFactory.INSTANCE)
                                                                         .registerTypeAdapterFactory(BukkitSerializableAdapterFactory.INSTANCE)
                                                                         .serializeNulls()
                                                                         .disableHtmlEscaping()
                                                                         .setPrettyPrinting()
                                                                         .create();

    private static final JsonParser PARSER = new JsonParser();

    @Nonnull
    public static Gson standard() {
        return STANDARD_GSON;
    }

    @Nonnull
    public static Gson prettyPrinting() {
        return PRETTY_PRINT_GSON;
    }

    @Nonnull
    public static JsonParser parser() {
        return PARSER;
    }

    @Nonnull
    public static JsonObject readObject(@Nonnull Reader reader) {
        return PARSER.parse(reader).getAsJsonObject();
    }

    @Nonnull
    public static JsonObject readObject(@Nonnull String s) {
        return PARSER.parse(s).getAsJsonObject();
    }

    public static void writeObject(@Nonnull Appendable writer, @Nonnull JsonObject object) {
        standard().toJson(object, writer);
    }

    public static void writeObjectPretty(@Nonnull Appendable writer, @Nonnull JsonObject object) {
        prettyPrinting().toJson(object, writer);
    }

    public static void writeElement(@Nonnull Appendable writer, @Nonnull JsonElement element) {
        standard().toJson(element, writer);
    }

    public static void writeElementPretty(@Nonnull Appendable writer, @Nonnull JsonElement element) {
        prettyPrinting().toJson(element, writer);
    }

    @Nonnull
    public static String toString(@Nonnull JsonElement element) {
        return Objects.requireNonNull(standard().toJson(element));
    }

    @Nonnull
    public static String toStringPretty(@Nonnull JsonElement element) {
        return Objects.requireNonNull(prettyPrinting().toJson(element));
    }

    private GsonProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @Nonnull
    @Deprecated
    public static Gson get() {
        return standard();
    }

    @Nonnull
    @Deprecated
    public static Gson getPrettyPrinting() {
        return prettyPrinting();
    }

}
