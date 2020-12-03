package com.pepej.papi.scoreboard;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An object which provides {@link Scoreboard} instances.
 */
public interface ScoreboardProvider {

    /**
     * Gets the scoreboard provided by this instance.
     *
     * @return the scoreboard
     */
    @NonNull
    Scoreboard getScoreboard();

}
