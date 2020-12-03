package com.pepej.papi.npc;


import net.citizensnpcs.api.npc.NPC;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A special implementation of {@link Npc} using Citizens.
 */
public interface CitizensNpc extends Npc {

    /**
     * Gets the "real" citizens NPC instance
     *
     * @return the npc
     */
    @NonNull
    NPC getNpc();

}
