package com.pepej.papi.bossbar;

import javax.annotation.Nonnull;

/**
 * A object which can create {@link BossBar}s.
 */
public interface BossBarFactory {

    /**
     * Creates a new boss bar.
     *
     * @return the new boss bar
     */
    @Nonnull
    BossBar newBossBar();

}
