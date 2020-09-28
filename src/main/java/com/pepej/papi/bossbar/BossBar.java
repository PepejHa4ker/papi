package com.pepej.papi.bossbar;

import com.pepej.papi.terminable.Terminable;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Generic interface for bossbars.
 */
public interface BossBar extends Terminable {

    /**
     * Gets the title of this boss bar
     *
     * @return the title of the bar
     */
    @Nonnull
    String title();

    /**
     * Sets the title of this boss bar
     *
     * @param title the title of the bar
     * @return this bar (for chaining)
     */
    @Nonnull
    BossBar title(@Nonnull String title);

    /**
     * Gets the progress of the bar between 0.0 and 1.0
     *
     * @return the progress of the bar
     */
    double progress();

    /**
     * Sets the progress of the bar. Values should be between 0.0 (empty) and
     * 1.0 (full)
     *
     * @param progress the progress of the bar
     * @return this bar (for chaining)
     */
    @Nonnull
    BossBar progress(double progress);

    /**
     * Gets the color of this boss bar
     *
     * @return the color of the bar
     */
    @Nonnull
    BossBarColor color();

    /**
     * Sets the color of this boss bar.
     *
     * @param color the color of the bar
     * @return this bar (for chaining)
     */
    @Nonnull
    BossBar color(@Nonnull BossBarColor color);

    /**
     * Gets the style of this boss bar
     *
     * @return the style of the bar
     */
    @Nonnull
    BossBarStyle style();

    /**
     * Sets the bar style of this boss bar
     *
     * @param style the style of the bar
     * @return this bar (for chaining)
     */
    @Nonnull
    BossBar style(@Nonnull BossBarStyle style);

    /**
     * Gets if the boss bar is displayed to attached players.
     *
     * @return visible status
     */
    boolean visible();

    /**
     * Set if the boss bar is displayed to attached players.
     *
     * @param visible visible status
     * @return this bar (for chaining)
     */
    @Nonnull
    BossBar visible(boolean visible);

    /**
     * Returns all players viewing this boss bar
     *
     * @return a immutable list of players
     */
    @Nonnull
    List<Player> players();

    /**
     * Adds the player to this boss bar causing it to display on their screen.
     *
     * @param player the player to add
     */
    void addPlayer(@Nonnull Player player);

    /**
     * Removes the player from this boss bar causing it to be removed from their
     * screen.
     *
     * @param player the player to remove
     */
    void removePlayer(@Nonnull Player player);

    /**
     * Adds the players to this boss bar causing it to display on their screens.
     *
     * @param players the players to add
     */
    default void addPlayers(@Nonnull Iterable<Player> players) {
        for (Player player : players) {
            addPlayer(player);
        }
    }

    /**
     * Removes the players from this boss bar causing it to be removed from their
     * screens.
     *
     * @param players the players to remove
     */
    default void removePlayers(@Nonnull Iterable<Player> players) {
        for (Player player : players) {
            removePlayer(player);
        }
    }

    /**
     * Removes all players from this boss bar
     */
    void removeAll();

    @Override
    void close();
}
