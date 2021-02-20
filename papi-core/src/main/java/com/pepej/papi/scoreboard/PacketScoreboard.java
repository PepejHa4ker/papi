package com.pepej.papi.scoreboard;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.google.common.base.Preconditions;
import com.pepej.papi.events.Events;
import com.pepej.papi.plugin.PapiPlugin;
import com.pepej.papi.utils.Players;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Implements {@link Scoreboard} using ProtocolLib.
 *
 * <p>This class as well as all returned instances are thread safe.</p>
 */
public class PacketScoreboard implements Scoreboard {

    // teams & objectives shared by all players.
    // these are automatically subscribed to when players join
    private final Map<String, PacketScoreboardTeam> teams = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, PacketScoreboardObjective> objectives = Collections.synchronizedMap(new HashMap<>());

    // per-player teams & objectives.
    private final Map<UUID, Map<String, PacketScoreboardTeam>> playerTeams = Collections.synchronizedMap(new HashMap<>());
    private final Map<UUID, Map<String, PacketScoreboardObjective>> playerObjectives = Collections.synchronizedMap(new HashMap<>());

    public PacketScoreboard(@NonNull PapiPlugin plugin) {
        Events.subscribe(PlayerJoinEvent.class)
              .handler(this::handlePlayerJoin)
              .bindWith(plugin);
        Events.subscribe(PlayerQuitEvent.class)
              .handler(this::handlePlayerQuit)
              .bindWith(plugin);
    }

    private void handlePlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // auto subscribe to teams
        for (PacketScoreboardTeam t : this.teams.values()) {
            if (t.shouldAutoSubscribe()) {
                t.subscribe(player);
            }
        }

        // auto subscribe to objectives
        for (PacketScoreboardObjective o : this.objectives.values()) {
            if (o.shouldAutoSubscribe()) {
                o.subscribe(player);
            }
        }
    }

    private void handlePlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        for (PacketScoreboardTeam packetScoreboardTeam : this.teams.values()) {
            packetScoreboardTeam.unsubscribe(player, true);
            packetScoreboardTeam.removePlayer(player);
        }

        for (PacketScoreboardObjective packetScoreboardObjective : this.objectives.values()) {
            packetScoreboardObjective.unsubscribe(player, true);
        }

        Map<String, PacketScoreboardObjective> playerObjectives = this.playerObjectives.remove(player.getUniqueId());
        if (playerObjectives != null) playerObjectives.values().forEach(o -> o.unsubscribe(player, true));

        Map<String, PacketScoreboardTeam> playerTeams = this.playerTeams.remove(player.getUniqueId());
        if (playerTeams != null) {
            for (PacketScoreboardTeam t : playerTeams.values()) {
                t.unsubscribe(player, true);
                t.removePlayer(player);
            }
        }
    }

    @Override
    public PacketScoreboardTeam createTeam(String id, String title, boolean autoSubscribe) {
        Preconditions.checkArgument(id.length() <= 16, "id cannot be longer than 16 characters");
        Preconditions.checkState(!this.teams.containsKey(id), "id already exists");

        PacketScoreboardTeam team = new PacketScoreboardTeam(id, title, autoSubscribe);
        if (autoSubscribe) {
            for (Player player : Players.all()) {
                team.subscribe(player);
            }
        }

        this.teams.put(id, team);
        return team;
    }

    @Override
    @Nullable
    public PacketScoreboardTeam getTeam(String id) {
        return this.teams.get(id);
    }

    @Override
    public boolean removeTeam(String id) {
        PacketScoreboardTeam team = this.teams.remove(id);
        if (team == null) {
            return false;
        }

        team.unsubscribeAll();
        return true;
    }

    @Override
    public PacketScoreboardObjective createObjective(String id, String title, DisplaySlot displaySlot, boolean autoSubscribe) {
        Preconditions.checkArgument(id.length() <= 16, "id cannot be longer than 16 characters");
        Preconditions.checkState(!this.objectives.containsKey(id), "id already exists");

        PacketScoreboardObjective objective = new PacketScoreboardObjective(id, title, displaySlot, autoSubscribe);
        if (autoSubscribe) {
            for (Player player : Players.all()) {
                objective.subscribe(player);
            }
        }

        this.objectives.put(id, objective);
        return objective;
    }

    @Override
    @Nullable
    public PacketScoreboardObjective getObjective(String id) {
        return this.objectives.get(id);
    }

    @Override
    public boolean removeObjective(String id) {
        PacketScoreboardObjective objective = this.objectives.remove(id);
        if (objective == null) {
            return false;
        }

        objective.unsubscribeAll();
        return true;
    }

    @Override
    public PacketScoreboardTeam createPlayerTeam(Player player, String id, String title, boolean autoSubscribe) {
        Preconditions.checkArgument(id.length() <= 16, "id cannot be longer than 16 characters");
        Map<String, PacketScoreboardTeam> teams = this.playerTeams.computeIfAbsent(player.getUniqueId(), p -> new HashMap<>());
        Preconditions.checkState(!teams.containsKey(id), "id already exists");

        PacketScoreboardTeam team = new PacketScoreboardTeam(id, title, autoSubscribe);
        if (autoSubscribe) {
            team.subscribe(player);
        }
        teams.put(id, team);

        return team;
    }

    @Override
    @Nullable
    public PacketScoreboardTeam getPlayerTeam(Player player, String id) {
        Map<String, PacketScoreboardTeam> map = this.playerTeams.get(player.getUniqueId());
        if (map == null) {
            return null;
        }

        return map.get(id);
    }

    @Override
    public boolean removePlayerTeam(Player player, String id) {
        Map<String, PacketScoreboardTeam> map = this.playerTeams.get(player.getUniqueId());
        if (map == null) {
            return false;
        }

        PacketScoreboardTeam team = map.remove(id);
        if (team == null) {
            return false;
        }

        team.unsubscribeAll();
        return true;
    }

    @Override
    public PacketScoreboardObjective createPlayerObjective(Player player, String id, String title, DisplaySlot displaySlot, boolean autoSubscribe) {
        Preconditions.checkArgument(id.length() <= 16, "id cannot be longer than 16 characters");
        Map<String, PacketScoreboardObjective> objectives = this.playerObjectives.computeIfAbsent(player.getUniqueId(), p -> new HashMap<>());
        Preconditions.checkState(!objectives.containsKey(id), "id already exists");

        PacketScoreboardObjective objective = new PacketScoreboardObjective(id, title, displaySlot, autoSubscribe);
        if (autoSubscribe) {
            objective.subscribe(player);
        }
        objectives.put(id, objective);

        return objective;
    }

    @Override
    @Nullable
    public PacketScoreboardObjective getPlayerObjective(Player player, String id) {
        Map<String, PacketScoreboardObjective> map = this.playerObjectives.get(player.getUniqueId());
        if (map == null) {
            return null;
        }

        return map.get(id);
    }

    @Override
    public boolean removePlayerObjective(Player player, String id) {
        Map<String, PacketScoreboardObjective> map = this.playerObjectives.get(player.getUniqueId());
        if (map == null) {
            return false;
        }

        PacketScoreboardObjective objective = map.remove(id);
        if (objective == null) {
            return false;
        }

        objective.unsubscribeAll();
        return true;
    }

    static WrappedChatComponent toComponent(String text) {
        return WrappedChatComponent.fromJson(GsonComponentSerializer.gson().serialize(Component.text(text)));
    }

}
