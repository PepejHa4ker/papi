package com.pepej.papi.hologram;


import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.pepej.papi.gson.GsonSerializable;
import com.pepej.papi.serialize.Position;
import com.pepej.papi.services.Implementor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A object which can create {@link Hologram}s.
 */
@Implementor(BukkitHologramFactory.class)
public interface HologramFactory {

    /**
     * Creates a new hologram.
     *
     * @param position the position of the hologram
     * @param lines the initial lines to display
     * @return the new hologram
     */
    @NonNull
    Hologram newHologram(@NonNull Position position, @NonNull List<String> lines);

    @NonNull
    Hologram newHologram(@NonNull Position position, @NonNull String... lines);
    /**
     * Deserializes a hologram instance from its {@link GsonSerializable serialized} form.
     *
     * @param element the data
     * @return the hologram
     */
    @NonNull
    default Hologram deserialize(JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        JsonObject object = element.getAsJsonObject();

        Preconditions.checkArgument(object.has("position"));
        Preconditions.checkArgument(object.has("lines"));

        Position position = Position.deserialize(object.get("position"));
        JsonArray lineArray = object.get("lines").getAsJsonArray();
        List<String> lines = new ArrayList<>();
        for (JsonElement e : lineArray) {
            lines.add(e.getAsString());
        }

        return newHologram(position, lines);
    }

}
