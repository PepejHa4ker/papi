package com.pepej.papi.internal;

import com.pepej.papi.events.Events;
import com.pepej.papi.math.effect.Effects;
import com.pepej.papi.math.vector.Vector3d;
import com.pepej.papi.plugin.PapiJavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.awt.*;

@PapiImplementationPlugin(moduleName = "papi-core")
public final class StandalonePlugin extends PapiJavaPlugin {


    @Override
    protected void onPluginEnable() {
        Events.subscribe(PlayerInteractEvent.class)
              .handler(event -> {
                  Player player = event.getPlayer();
                  Location location = player.getLocation();
                  Effects.getWingsEffect(new Vector3d(location.getX(), location.getY(), location.getZ()), location.getYaw(), location.getPitch())
                         .forEach(point -> {
                             Effects.spawnColoredRedstoneParticle(point, player.getWorld(), Color.RED);
                         });
              })
              .bindWith(this);
    }
}