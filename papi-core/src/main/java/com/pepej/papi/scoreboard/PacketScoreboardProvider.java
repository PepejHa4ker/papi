package com.pepej.papi.scoreboard;

import com.pepej.papi.plugin.PapiPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Implementation of {@link ScoreboardProvider} for {@link PacketScoreboard}s.
 */
public final class PacketScoreboardProvider implements ScoreboardProvider {
    private final PapiPlugin plugin;
    private PacketScoreboard scoreboard = null;

    public PacketScoreboardProvider(PapiPlugin plugin) {
        this.plugin = plugin;
    }

    @NonNull
    @Override
    public synchronized PacketScoreboard getScoreboard() {
        if (this.scoreboard == null) {
            this.scoreboard = new PacketScoreboard(this.plugin);
        }
        return this.scoreboard;
    }
}