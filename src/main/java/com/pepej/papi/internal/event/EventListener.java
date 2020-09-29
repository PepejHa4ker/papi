package com.pepej.papi.internal.event;

import com.pepej.papi.Events;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;

public class EventListener implements TerminableModule {

    @Override
    public void setup(@Nonnull final TerminableConsumer consumer) {
        Events.subscribe(PlayerJoinEvent.class)
              .handler(e -> {
                  //Event logic
              }).bindWith(consumer);
    }
}
