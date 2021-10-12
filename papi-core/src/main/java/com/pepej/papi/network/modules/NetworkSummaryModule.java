package com.pepej.papi.network.modules;

import com.pepej.papi.command.Commands;
import com.pepej.papi.events.Events;
import com.pepej.papi.network.Server;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.event.filter.EventFilters;
import com.pepej.papi.messaging.InstanceData;
import com.pepej.papi.network.Network;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.time.DurationFormatter;
import com.pepej.papi.time.Time;
import com.pepej.papi.utils.Players;
import com.pepej.papi.utils.Tps;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.util.Comparator;
import java.util.Map;

import static com.pepej.papi.utils.Players.MessageType.NONE;

public class NetworkSummaryModule implements TerminableModule {
    private final Network network;
    private final InstanceData instanceData;
    private final String[] commandAliases;

    public NetworkSummaryModule(Network network, InstanceData instanceData) {
        this(network, instanceData, new String[]{"networksummary", "netsum"});
    }

    public NetworkSummaryModule(Network network, InstanceData instanceData, String[] commandAliases) {
        this.network = network;
        this.instanceData = instanceData;
        this.commandAliases = commandAliases;
    }

    @Override
    public void setup(@NonNull TerminableConsumer consumer) {
        Commands.create()
                .assertPermission("papi.networksummary")
                .handler(c -> sendSummary(c.sender()))
                .registerAndBind(consumer, this.commandAliases);

        Events.subscribe(PlayerJoinEvent.class, EventPriority.MONITOR)
                .filter(EventFilters.playerHasPermission("papi.networksummary.onjoin"))
                .handler(e -> Schedulers.sync().runLater(() -> sendSummary(e.getPlayer()), 1))
                .bindWith(consumer);
    }

    public void sendSummary(CommandSender sender) {
        Players.msg(sender, NONE, "&7[&anetwork&7] &f< Network summary >.");
        Players.msg(sender, NONE,"&7[&anetwork&7] &7" + this.network.getOverallPlayerCount() + " total players online.");
        Players.msg(sender, NONE,"&7[&anetwork&7]");

        this.network.getServers().values().stream()
                .sorted(Comparator.comparingInt(value -> value.getOnlinePlayers().size()))
                .forEach(server -> {
                    String id = (server.getId().equals(this.instanceData.getId()) ? "&a" : "&b") + server.getId();
                    if (!server.isOnline()) {
                        long lastPing = server.getLastPing();
                        if (lastPing == 0) {
                            return;
                        }
                        String lastSeen = DurationFormatter.CONCISE.format(Time.diffToNow(Instant.ofEpochMilli(lastPing)));
                        Players.msg(sender, NONE,"&7[&anetwork&7] " + id + " &7- last online " + lastSeen + " ago");
                    } else {
                        Players.msg(sender, NONE,"&7[&anetwork&7] " + id + " &7- &b" + server.getOnlinePlayers().size() + "&7/" + server.getMaxPlayers());
                    }
                });
    }
}
