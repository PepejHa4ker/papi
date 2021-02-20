package com.pepej.papi.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Represents base class for all papi events
 */
public abstract class PapiEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    protected PapiEvent() {}

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    protected PapiEvent(final boolean async) {
        super(async);
    }


}
