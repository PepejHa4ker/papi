package com.pepej.papi.internal;

import com.google.common.eventbus.EventBus;
import com.pepej.papi.events.Events;
import com.pepej.papi.plugin.PapiJavaPlugin;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.ApiStatus;


@PapiImplementationPlugin(moduleName = StandalonePlugin.MODULE_ID)
@ApiStatus.Internal
public final class StandalonePlugin extends PapiJavaPlugin {

    public static final String MODULE_ID = "Papi Core";

}






