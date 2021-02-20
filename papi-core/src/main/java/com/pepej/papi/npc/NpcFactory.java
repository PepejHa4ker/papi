package com.pepej.papi.npc;


import com.pepej.papi.terminable.Terminable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an object which can create {@link Npc}s.
 */
public interface NpcFactory extends Terminable {

    /**
     * Spawns a NPC at the given location
     *
     * @param location the location to spawn the npc at
     * @param nametag the nametag to give the npc
     * @param skinPlayer the username of the player whose skin the NPC should have
     * @return the created npc
     * @deprecated in favour of {@link #spawnNpc(Location, String, String, String)}
     */
    @NonNull
    @Deprecated
    Npc spawnNpc(@NonNull Location location, @NonNull String nametag, @NonNull String skinPlayer);

    /**
     * Spawns a NPC at the given location
     *
     * @param location the location to spawn the npc at
     * @param nametag the nametag to give the npc
     * @param skinTextures the skin textures the NPC should have
     * @param skinSignature the signature of the provided textures
     * @return the created npc
     */
    @NonNull
    Npc spawnNpc(@NonNull Location location, @NonNull String nametag, @NonNull String skinTextures, @NonNull String skinSignature);

    /**
     * check if NPC spawned by papi
     *
     * @param entity the entity to check
     * @return true if entity has been spawned by papi
     */
    boolean isPapiNPC(@NonNull Entity entity);

    @Override
    void close();
}
