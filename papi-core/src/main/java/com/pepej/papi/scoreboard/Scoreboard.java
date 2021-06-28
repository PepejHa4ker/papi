package com.pepej.papi.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a Scoreboard on the server
 */
public interface Scoreboard {

    /**
     * Creates a new scoreboard team
     *
     * @param id the id of the team
     * @param title the initial title for the team
     * @param autoSubscribe if players should be automatically subscribed
     * @return the new team
     * @throws IllegalStateException if a team with the same id already exists
     */
    ScoreboardTeam createTeam(String id, String title, boolean autoSubscribe);

    /**
     * Creates a new scoreboard team
     *
     * @param id the id of the team
     * @param title the initial title for the team
     * @return the new team
     * @throws IllegalStateException if a team with the same id already exists
     */
    default ScoreboardTeam createTeam(String id, String title) {
        return createTeam(id, title, true);
    }

    /**
     * Creates a new scoreboard team with an automatically generated id
     *
     * @param title the initial title for the team
     * @param autoSubscribe if players should be automatically subscribed
     * @return the new team
     */
    default ScoreboardTeam createTeam(String title, boolean autoSubscribe) {
        return createTeam(Long.toHexString(System.nanoTime()), title, autoSubscribe);
    }

    /**
     * Creates a new scoreboard team with an automatically generated id
     *
     * @param title the initial title for the team
     * @return the new team
     */
    default ScoreboardTeam createTeam(String title) {
        return createTeam(title, true);
    }

    /**
     * Gets an existing scoreboard team if one with the id exists
     *
     * @param id the id of the team
     * @return the team, if present, otherwise null
     */
    @Nullable
    ScoreboardTeam getTeam(String id);

    /**
     * Removes a scoreboard team from this scoreboard
     *
     * @param id the id of the team
     * @return true if the team was removed successfully
     */
    boolean removeTeam(String id);

    /**
     * Creates a new scoreboard objective
     *
     * @param id the id of the objective
     * @param title the initial title for the objective
     * @param displaySlot the display slot to use for this objective
     * @param autoSubscribe if players should be automatically subscribed
     * @return the new objective
     * @throws IllegalStateException if an objective with the same id already exists
     */
    ScoreboardObjective createObjective(String id, String title, DisplaySlot displaySlot, boolean autoSubscribe);

    /**
     * Creates a new scoreboard objective
     *
     * @param id the id of the objective
     * @param title the initial title for the objective
     * @param displaySlot the display slot to use for this objective
     * @return the new objective
     * @throws IllegalStateException if an objective with the same id already exists
     */
    default ScoreboardObjective createObjective(String id, String title, DisplaySlot displaySlot) {
        return createObjective(id, title, displaySlot, true);
    }

    /**
     * Creates a new scoreboard objective with an automatically generated id
     *
     * @param title the initial title for the objective
     * @param displaySlot the display slot to use for this objective
     * @param autoSubscribe if players should be automatically subscribed
     * @return the new objective
     */
    default ScoreboardObjective createObjective(String title, DisplaySlot displaySlot, boolean autoSubscribe) {
        return createObjective(Long.toHexString(System.nanoTime()), title, displaySlot, autoSubscribe);
    }

    /**
     * Creates a new scoreboard objective with an automatically generated id
     *
     * @param title the initial title for the objective
     * @param displaySlot the display slot to use for this objective
     * @return the new objective
     */
    default ScoreboardObjective createObjective(String title, DisplaySlot displaySlot) {
        return createObjective(title, displaySlot, true);
    }

    /**
     * Gets an existing scoreboard objective if one with the id exists
     *
     * @param id the id of the objective
     * @return the objective, if present, otherwise null
     */
    @Nullable
    ScoreboardObjective getObjective(String id);

    /**
     * Removes a scoreboard objective from this scoreboard
     *
     * @param id the id of the objective
     * @return true if the objective was removed successfully
     */
    boolean removeObjective(String id);

    /**
     * Creates a new per-player scoreboard team
     *
     * @param player the player to make the team for
     * @param id the id of the team
     * @param title the initial title of the team
     * @param autoSubscribe if players should be automatically subscribed
     * @return the new team
     * @throws IllegalStateException if a team with the same id already exists
     */
    ScoreboardTeam createPlayerTeam(Player player, String id, String title, boolean autoSubscribe);

    /**
     * Creates a new per-player scoreboard team
     *
     * @param player the player to make the team for
     * @param id the id of the team
     * @param title the initial title of the team
     * @return the new team
     * @throws IllegalStateException if a team with the same id already exists
     */
    default ScoreboardTeam createPlayerTeam(Player player, String id, String title) {
        return createPlayerTeam(player, id, title, true);
    }

    /**
     * Creates a new per-player scoreboard team with an automatically generated id
     *
     * @param player the player to make the team for
     * @param title the initial title of the team
     * @param autoSubscribe if players should be automatically subscribed
     * @return the new team
     */
    default ScoreboardTeam createPlayerTeam(Player player, String title, boolean autoSubscribe) {
        return createPlayerTeam(player, Long.toHexString(System.nanoTime()), title, autoSubscribe);
    }

    /**
     * Creates a new per-player scoreboard team with an automatically generated id
     *
     * @param player the player to make the team for
     * @param title the initial title of the team
     * @return the new team
     */
    default ScoreboardTeam createPlayerTeam(Player player, String title) {
        return createPlayerTeam(player, title, true);
    }

    /**
     * Gets an existing per-player scoreboard team if one with the id exists
     *
     * @param player the player to get the team for
     * @param id the id of the team
     * @return the team, if present, otherwise null
     */
    @Nullable
    ScoreboardTeam getPlayerTeam(Player player, String id);

    /**
     * Removes a per-player scoreboard team from this scoreboard
     *
     * @param player the player to remove the team for
     * @param id the id of the team
     * @return true if the team was removed successfully
     */
    boolean removePlayerTeam(Player player, String id);

    /**
     * Creates a new per-player scoreboard objective
     *
     * @param player the player to make the objective for
     * @param id the id of the objective
     * @param title the initial title of the objective
     * @param displaySlot the display slot to use for this objective
     * @param autoSubscribe if players should be automatically subscribed
     * @return the new objective
     * @throws IllegalStateException if an objective with the same id already exists
     */
    ScoreboardObjective createPlayerObjective(Player player, String id, String title, DisplaySlot displaySlot, boolean autoSubscribe);

    /**
     * Creates a new per-player scoreboard objective
     *
     * @param player the player to make the objective for
     * @param id the id of the objective
     * @param title the initial title of the objective
     * @param displaySlot the display slot to use for this objective
     * @return the new objective
     * @throws IllegalStateException if an objective with the same id already exists
     */
    default ScoreboardObjective createPlayerObjective(Player player, String id, String title, DisplaySlot displaySlot) {
        return createPlayerObjective(player, id, title, displaySlot, true);
    }

    /**
     * Creates a new per-player scoreboard objective with an automatically generated id
     *
     * @param player the player to make the objective for
     * @param title the initial title of the objective
     * @param displaySlot the display slot to use for this objective
     * @param autoSubscribe auto-subscribe
     * @return the new objective
     */
    default ScoreboardObjective createPlayerObjective(Player player, String title, DisplaySlot displaySlot, boolean autoSubscribe) {
        return createPlayerObjective(player, Long.toHexString(System.nanoTime()), title, displaySlot, autoSubscribe);
    }

    /**
     * Creates a new per-player scoreboard objective with an automatically generated id
     *
     * @param player the player to make the objective for
     * @param title the initial title of the objective
     * @param displaySlot the display slot to use for this objective
     * @return the new objective
     */
    default ScoreboardObjective createPlayerObjective(Player player, String title, DisplaySlot displaySlot) {
        return createPlayerObjective(player, title, displaySlot, true);
    }

    /**
     * Gets an existing per-player scoreboard objective if one with the id exists
     *
     * @param player the player to get the objective for
     * @param id the id of the objective
     * @return the objective, if present, otherwise null
     */
    @Nullable
    ScoreboardObjective getPlayerObjective(Player player, String id);

    /**
     * Removes a per-player scoreboard objective from this scoreboard
     *
     * @param player the player to remove the objective for
     * @param id the id of the objective
     * @return true if the objective was removed successfully
     */
    boolean removePlayerObjective(Player player, String id);


}
