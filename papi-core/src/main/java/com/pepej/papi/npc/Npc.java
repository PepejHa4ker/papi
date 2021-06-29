package com.pepej.papi.npc;


import com.pepej.papi.metadata.MetadataMap;
import com.pepej.papi.terminable.Terminable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.function.Consumer;

/**
 * Represents a NPC (non-player character)
 */
public interface Npc extends Terminable {

    /**
     * Applies a click callback listener to this NPC.
     *
     * @param clickCallback the click callback
     */
    void setClickCallback(@Nullable Consumer<Player> clickCallback);

    /**
     * Gets the NPCs attached metadata map.
     *
     * @return the metadata map
     */
    @NonNull
    MetadataMap getMeta();

    /**
     * Sets the NPCs skin to the skin of the given player.
     *
     * @param skinPlayer the player
     * @deprecated in favour of {@link #setSkin(String, String)}
     */
    @Deprecated
    void setSkin(@NonNull String skinPlayer);

    /**
     * Sets the NPCs skin to the given textures
     *
     * @param textures the textures
     * @param signature the signature of the textures
     */
    void setSkin(@NonNull String textures, @NonNull String signature);

    /**
     * Sets the name of this NPC
     *
     * @param name the name
     */
    void setName(@NonNull String name);

    /**
     * Sets if this NPCs nametag should be shown
     *
     * @param show is the nametag should be shown
     */
    void setShowNametag(boolean show);


    /**
     * Gets the location where this NPC was initially spawned at
     *
     * @return the initial spawn location of the NPC
     */
    @NonNull
    Location getInitialSpawn();

    /**
     * Removes npc from a world
     */
    @Override
    void close() throws Exception;
}
