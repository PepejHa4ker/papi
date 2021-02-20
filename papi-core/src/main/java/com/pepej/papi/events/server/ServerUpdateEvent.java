package com.pepej.papi.events.server;

import com.pepej.papi.events.PapiEvent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an event that is called when a server update
 */
@EqualsAndHashCode(callSuper = true)
@Value(staticConstructor = "of")
public class ServerUpdateEvent extends PapiEvent {
    @NonNull Type type;

    private ServerUpdateEvent(final @NonNull Type type) {
        super(false);
        this.type = type;
    }

    @AllArgsConstructor
    public enum Type {
        TICK(1),
        SECOND(20),
        MINUTE(20*60),
        HOUR(20*60*60),
        DAY(20*60*60*24);

        private final long delayTicks;

        public long getDelayTicks() {
            return delayTicks;
        }
    }
}
