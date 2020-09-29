package com.pepej.papi.internal.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class BukkitEventListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        //Event logic
    }
}
