package com.pepej.papi.events.server;


import lombok.AllArgsConstructor;

/**
 * Server update types, values are presented in ticks
 */
@AllArgsConstructor
public enum ServerUpdateType {
    TICK(1),
    SECOND(20),
    MINUTE(1200),
    HOUR(72000);

    private final long delayTicks;

    public long getDelayTicks() {
        return delayTicks;
    }
}