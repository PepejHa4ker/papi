package com.pepej.papi.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class PapiEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
