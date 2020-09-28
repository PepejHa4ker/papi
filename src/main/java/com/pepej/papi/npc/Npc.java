package com.pepej.papi.npc;


import com.pepej.papi.metadata.MetadataMap;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

/**
 * Represents a NPC (non-player character)
 */
public interface Npc {

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
    @Nonnull
    MetadataMap getMeta();

    /**
     * Sets the NPCs skin to the skin of the given player.
     *
     * @param skinPlayer the player
     * @deprecated in favour of {@link #setSkin(String, String)}
     */
    @Deprecated
    void setSkin(@Nonnull String skinPlayer);

    /**
     * Sets the NPCs skin to the given textures
     *
     * @param textures the textures
     * @param signature the signature of the textures
     */
    void setSkin(@Nonnull String textures, @Nonnull String signature);

    /**
     * Sets the name of this NPC
     *
     * @param name the name
     */
    void setName(@Nonnull String name);

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
    @Nonnull
    Location getInitialSpawn();

}
