package com.pepej.papi.hologram;

import com.pepej.papi.serialize.Position;
import com.pepej.papi.terminable.Terminable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Base interface for holograms.
 */
public interface BaseHologram extends Terminable {

    /**
     * Spawns the hologram
     */
    void spawn();

    /**
     * delete the hologram
     */
    void delete();

    /**
     * Check if the hologram is currently spawned
     *
     * @return true if spawned and active, or false otherwise
     */
    boolean isSpawned();

    /**
     * Gets the ArmorStands that hold the lines for this hologram
     *
     * @return the ArmorStands holding the lines
     */
    @Nonnull
    Collection<ArmorStand> getArmorStands();

    /**
     * Gets the ArmorStand holding the specified line
     *
     * @param line the line
     * @return the ArmorStand holding this line
     */
    @Nullable
    ArmorStand getArmorStand(int line);

    /**
     * Updates the position of the hologram and respawns it
     *
     * @param position the new position
     */
    void updatePosition(@Nonnull Position position);

    /**
     * Sets a click callback for this hologram
     *
     * @param clickCallback the click callback, or null to unregister any existing callback
     */
    void setClickCallback(@Nullable Consumer<Player> clickCallback);

}
