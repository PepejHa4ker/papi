package com.pepej.papi.events.server;

import com.pepej.papi.events.PapiEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an event that is called when a server update
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class ServerUpdateEvent extends PapiEvent {
    @NonNull ServerUpdateType updateType;
}
