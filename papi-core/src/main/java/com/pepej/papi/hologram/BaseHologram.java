package com.pepej.papi.hologram;

import com.pepej.papi.serialize.Position;
import com.pepej.papi.terminable.Terminable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
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
    @NonNull
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
    void updatePosition(@NonNull Position position);

    void onPluginDisable(@NonNull Plugin plugin);

    void addExpiring(final long ticksDelay);

    void addExpiring(final long delay, final TimeUnit unit);






}
