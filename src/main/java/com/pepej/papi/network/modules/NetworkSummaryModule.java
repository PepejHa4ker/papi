package com.pepej.papi.network.modules;

import com.pepej.papi.Commands;
import com.pepej.papi.Events;
import com.pepej.papi.Schedulers;
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

import javax.annotation.Nonnull;
import java.time.Instant;
import java.util.Map;

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
    public void setup(@Nonnull TerminableConsumer consumer) {
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
        Players.msg(sender, "&7[&anetwork&7] &f< Network summary >.");
        Players.msg(sender, "&7[&anetwork&7] &7" + this.network.getOverallPlayerCount() + " total players online.");
        Players.msg(sender, "&7[&anetwork&7]");

        this.network.getServers().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .forEach(server -> {
                        String id = (server.getId().equals(this.instanceData.getId()) ? "&a" : "&b") + server.getId();
                        if (!server.isOnline()) {
                            long lastPing = server.getLastPing();
                            if (lastPing == 0) {
                                return;
                            }

                            String lastSeen = DurationFormatter.CONCISE.format(Time.diffToNow(Instant.ofEpochMilli(lastPing)));
                            Players.msg(sender, "&7[&anetwork&7] " + id + " &7- last online " + lastSeen + " ago");
                        } else {
                            Tps tps = server.getMetadata("tps", Tps.class);
                            String tpsInfo = "";
                            if (tps != null) {
                                tpsInfo = " &7- " + tps.toFormattedString();
                            }
                            Players.msg(sender, "&7[&anetwork&7] " + id + " &7- &b" + server.getOnlinePlayers().size() + "&7/" + server.getMaxPlayers() + tpsInfo);
                        }
                    });
    }
}
