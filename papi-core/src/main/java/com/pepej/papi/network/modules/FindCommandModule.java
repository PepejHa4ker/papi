package com.pepej.papi.network.modules;

import com.pepej.papi.command.Commands;
import com.pepej.papi.network.Network;
import com.pepej.papi.profiles.Profile;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.Players;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FindCommandModule implements TerminableModule {
    private final Network network;
    private final String[] commandAliases;

    public FindCommandModule(Network network) {
        this(network, new String[]{"find"});
    }

    public FindCommandModule(Network network, String[] commandAliases) {
        this.network = network;
        this.commandAliases = commandAliases;
    }

    @Override
    public void setup(@NonNull TerminableConsumer consumer) {
        Commands.create()
                .assertPermission("papi.find")
                .handler(c -> {
                    String player = c.arg(0).parseOrFail(String.class).toLowerCase();

                    Map<String, List<Profile>> matches = this.network.getServers().entrySet().stream()
                                                                     .sorted(Map.Entry.comparingByKey())
                                                                     .collect(Collectors.toMap(Map.Entry::getKey,
                                                                             s -> s.getValue().getOnlinePlayers().values().stream()
                                                                                   .filter(profile -> profile.getName().isPresent() && profile.getName().get().toLowerCase().contains(player))
                                                                                   .sorted(Comparator.comparing(p -> p.getName().get()))
                                                                                   .collect(Collectors.toList()),
                                                                             (l1, l2) -> Stream.concat(l1.stream(), l2.stream()).collect(Collectors.toList()),
                                                                             LinkedHashMap::new
                                                                     ));
                    matches.values().removeIf(Collection::isEmpty);

                    if (matches.isEmpty()) {
                        Players.msg(c.sender(), "&7[&anetwork&7] &7No players found matching '&f" + player + "&7'.");
                    } else {
                        Players.msg(c.sender(), "&7[&anetwork&7] &7Player search results for '&f" + player + "&7':");
                        matches.forEach((server, profiles) -> {
                            profiles.forEach(profile -> {
                                Players.msg(c.sender(), "&7[&anetwork&7] &f> &2" + profile.getName().orElse(profile.getUniqueId().toString()) + " &7connected to &2" + server);
                            });
                        });
                    }
                }).registerAndBind(consumer, this.commandAliases);
    }
}