package com.pepej.papi.scoreboard;

import javax.annotation.Nonnull;

/**
 * An object which provides {@link Scoreboard} instances.
 */
public interface ScoreboardProvider {

    /**
     * Gets the scoreboard provided by this instance.
     *
     * @return the scoreboard
     */
    @Nonnull
    Scoreboard getScoreboard();

}
