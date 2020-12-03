package com.pepej.papi.hologram;

import com.google.gson.JsonElement;
import com.pepej.papi.Services;
import com.pepej.papi.gson.GsonSerializable;
import com.pepej.papi.serialize.Position;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * A simple hologram utility.
 */
public interface Hologram extends BaseHologram, GsonSerializable {

    /**
     * Creates and returns a new hologram
     *
     * <p>Note: the hologram will not be spawned automatically.</p>
     *
     * @param position the position of the hologram
     * @param lines the initial lines to display
     * @return the new hologram.
     */
    @NonNull
    static Hologram create(@NonNull Position position, @NonNull List<String> lines) {
        return Services.load(HologramFactory.class).newHologram(position, lines);
    }

    static Hologram deserialize(JsonElement element) {
        return Services.load(HologramFactory.class).deserialize(element);
    }

    /**
     * Updates the lines displayed by this hologram
     *
     * <p>This method does not refresh the actual hologram display. {@link #spawn()} must be called for these changes
     * to apply.</p>
     *
     * @param lines the new lines
     */
    void updateLines(@NonNull List<String> lines);

}
