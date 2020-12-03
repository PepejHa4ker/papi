package com.pepej.papi.internal;

import com.pepej.papi.events.Events;
import com.pepej.papi.events.server.ServerUpdateEvent;
import com.pepej.papi.events.server.ServerUpdateType;
import com.pepej.papi.plugin.PapiJavaPlugin;
import com.pepej.papi.scheduler.Schedulers;


@PapiImplementationPlugin
public final class StandalonePlugin extends PapiJavaPlugin {

    @Override
    public void onPluginEnable() {
        for (ServerUpdateType value : ServerUpdateType.values()) {
            Schedulers.async().runRepeating(() -> Events.call(new ServerUpdateEvent(value)), 0L, value.getDelayTicks());
        }
    }

    @Override
    public void onPluginDisable() {}

}
