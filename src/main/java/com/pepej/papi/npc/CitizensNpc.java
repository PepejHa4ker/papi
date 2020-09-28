package com.pepej.papi.npc;


import net.citizensnpcs.api.npc.NPC;

import javax.annotation.Nonnull;

/**
 * A special implementation of {@link Npc} using Citizens.
 */
public interface CitizensNpc extends Npc {

    /**
     * Gets the "real" citizens NPC instance
     *
     * @return the npc
     */
    @Nonnull
    NPC getNpc();

}
