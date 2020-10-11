package com.pepej.papi.hologram.individual;

import com.pepej.papi.Services;
import com.pepej.papi.hologram.BaseHologram;
import com.pepej.papi.serialize.Position;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
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
    @Nonnull
    static IndividualHologram create(@Nonnull Position position, @Nonnull List<HologramLine> lines) {
        return Services.load(IndividualHologramFactory.class).newHologram(position, lines);
    }

    /**
     * Updates the lines displayed by this hologram
     *
     * <p>This method does not refresh the actual hologram display. {@link #spawn()} must be called for these changes
     * to apply.</p>
     *
     * @param lines the new lines
     */
    void updateLines(@Nonnull List<HologramLine> lines);

    /**
     * Returns a copy of the available viewers of the hologram.
     *
     * @return a {@link Set} of players.
     */
    @Nonnull
    Set<Player> getViewers();

    /**
     * Adds a viewer to the hologram.
     *
     * @param player the player
     */
    void addViewer(@Nonnull Player player);

    /**
     * Removes a viewer from the hologram.
     *
     * @param player the player
     */
    void removeViewer(@Nonnull Player player);

    /**
     * Check if there are any viewers for the hologram.
     *
     * @return any viewers
     */
    default boolean hasViewers() {
        return this.getViewers().size() > 0;
    }
}
