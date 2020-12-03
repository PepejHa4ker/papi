package com.pepej.papi.gson;

import com.google.gson.*;
import com.pepej.papi.datatree.DataTree;
import com.pepej.papi.gson.typeadapters.BukkitSerializableAdapterFactory;
import com.pepej.papi.gson.typeadapters.GsonSerializableAdapterFactory;
import com.pepej.papi.gson.typeadapters.JsonElementTreeSerializer;
import net.kyori.text.serializer.gson.GsonComponentSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;

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

    @NonNull
    public static Gson standard() {
        return STANDARD_GSON;
    }

    @NonNull
    public static Gson prettyPrinting() {
        return PRETTY_PRINT_GSON;
    }

    @NonNull
    public static JsonParser parser() {
        return PARSER;
    }

    @NonNull
    public static JsonObject readObject(@NonNull Reader reader) {
        return PARSER.parse(reader).getAsJsonObject();
    }

    @NonNull
    public static JsonObject readObject(@NonNull String s) {
        return PARSER.parse(s).getAsJsonObject();
    }

    public static void writeObject(@NonNull Appendable writer, @NonNull JsonObject object) {
        standard().toJson(object, writer);
    }

    public static void writeObjectPretty(@NonNull Appendable writer, @NonNull JsonObject object) {
        prettyPrinting().toJson(object, writer);
    }

    public static void writeElement(@NonNull Appendable writer, @NonNull JsonElement element) {
        standard().toJson(element, writer);
    }

    public static void writeElementPretty(@NonNull Appendable writer, @NonNull JsonElement element) {
        prettyPrinting().toJson(element, writer);
    }

    @NonNull
    public static String toString(@NonNull JsonElement element) {
        return Objects.requireNonNull(standard().toJson(element));
    }

    @NonNull
    public static String toStringPretty(@NonNull JsonElement element) {
        return Objects.requireNonNull(prettyPrinting().toJson(element));
    }

    private GsonProvider() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    @NonNull
    @Deprecated
    public static Gson get() {
        return standard();
    }

    @NonNull
    @Deprecated
    public static Gson getPrettyPrinting() {
        return prettyPrinting();
    }

}
