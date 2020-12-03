package com.pepej.papi.network.modules;

import com.pepej.papi.event.bus.EventBus;
import com.pepej.papi.event.bus.Subscribers;
import com.pepej.papi.network.Network;
import com.pepej.papi.network.event.NetworkEvent;
import com.pepej.papi.network.event.ServerConnectEvent;
import com.pepej.papi.network.event.ServerDisconnectEvent;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.Players;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NetworkStatusModule implements TerminableModule {
    private final Network network;

    public NetworkStatusModule(Network network) {
        this.network = network;
    }

    @Override
    public void setup(@NonNull TerminableConsumer consumer) {
        EventBus<NetworkEvent> bus = this.network.getEventBus();

        Subscribers.register(bus, ServerConnectEvent.class, event -> {
            broadcast("&7[&anetwork&7] &b" + event.getId() + " &7connected.");
        }).bindWith(consumer);

        Subscribers.register(bus, ServerDisconnectEvent.class, event -> {
            if (event.getReason() != null && !event.getReason().isEmpty()) {
                broadcast("&7[&anetwork&7] &b" + event.getId() + " &7disconnected. (reason: " + event.getReason() + ")");
            } else {
                broadcast("&7[&anetwork&7] &b" + event.getId() + " &7disconnected. (reason unknown)");
            }

        }).bindWith(consumer);
    }

    private static void broadcast(String message) {
        Players.stream()
               .filter(p -> p.hasPermission("papi.networkstatus.alerts"))
               .forEach(p -> Players.msg(p, message));
    }
}
