package com.pepej.papi.hologram.individual;

import com.pepej.papi.Services;
import com.pepej.papi.hologram.BaseHologram;
import com.pepej.papi.hologram.HologramLine;
import com.pepej.papi.serialize.Position;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Set;

public interface IndividualHologram extends BaseHologram {

    /**
     * Creates and returns a new individual hologram
     *
     * <p>Note: the hologram will not be spawned automatically.</p>
     *
     * @param position the position of the hologram
     * @param lines the initial lines to display
     * @return the new hologram.
     */
    @NonNull
    static IndividualHologram create(@NonNull Position position, @NonNull List<HologramLine> lines) {
        final IndividualHologramFactory individualHologramFactory = Services.load(IndividualHologramFactory.class);
        return individualHologramFactory.newHologram(position, lines);
    }

    /**
     * Updates the lines displayed by this hologram
     *
     * <p>This method does not refresh the actual hologram display. {@link #spawn()} must be called for these changes
     * to apply.</p>
     *
     * @param lines the new lines
     */
    void updateLines(@NonNull List<HologramLine> lines);

    /**
     * Returns a copy of the available viewers of the hologram.
     *
     * @return a {@link Set} of players.
     */
    @NonNull
    Set<Player> getViewers();

    /**
     * Adds a viewer to the hologram.
     *
     * @param player the player
     */
    void addViewer(@NonNull Player player);

    /**
     * Removes a viewer from the hologram.
     *
     * @param player the player
     */
    void removeViewer(@NonNull Player player);

    /**
     * Check if there are any viewers for the hologram.
     *
     * @return any viewers
     */
    default boolean hasViewers() {
        return this.getViewers().size() > 0;
    }
}
