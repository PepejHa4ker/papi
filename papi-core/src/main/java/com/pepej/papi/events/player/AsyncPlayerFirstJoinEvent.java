package com.pepej.papi.events.player;


import com.pepej.papi.events.PapiEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an event that is called when a player first join the server
 * Calling async
 */
@EqualsAndHashCode(callSuper = true)
@Value(staticConstructor = "of")
public class AsyncPlayerFirstJoinEvent extends PapiEvent {

    @NonNull Player player;
    long joinedTime;

    private AsyncPlayerFirstJoinEvent(final @NonNull Player player, final long joinedTime) {
        super(true);
        this.player = player;
        this.joinedTime = joinedTime;
    }
}
