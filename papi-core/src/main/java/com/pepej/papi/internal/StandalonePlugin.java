package com.pepej.papi.internal;

import com.pepej.papi.events.Events;
import com.pepej.papi.plugin.PapiJavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

@PapiImplementationPlugin(moduleName = "papi-core")
public final class StandalonePlugin extends PapiJavaPlugin {


    @Override
    protected void onPluginEnable() {
        Events.subscribe(PlayerInteractEvent.class)
              .handler(event -> {
                  Player player = event.getPlayer();
                  Vector direction = player.getLocation().getDirection();

              })
              .bindWith(this);
    }
}