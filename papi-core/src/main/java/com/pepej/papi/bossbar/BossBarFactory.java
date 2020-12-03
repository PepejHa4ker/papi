package com.pepej.papi.bossbar;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A object which can create {@link BossBar}s.
 */
public interface BossBarFactory {

    /**
     * Creates a new boss bar.
     *
     * @return the new boss bar
     */
    @NonNull
    BossBar newBossBar();

}
