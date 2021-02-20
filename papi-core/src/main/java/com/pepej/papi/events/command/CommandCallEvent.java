package com.pepej.papi.events.command;

import com.pepej.papi.command.Command;
import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.event.bus.api.Cancellable;
import com.pepej.papi.events.PapiEvent;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an event that is called when a sender calls the command
 */
@Getter
public class CommandCallEvent<T extends CommandSender> extends PapiEvent implements Cancellable {
    @NonNull private final Command command;
    @NonNull private final CommandSender sender;
    @NonNull private final CommandContext<T> context;
    private boolean cancelled;

    public CommandCallEvent(final @NonNull Command command, final @NonNull T sender, final @NonNull CommandContext<T> context) {
        super(false);
        this.command = command;
        this.sender = sender;
        this.context = context;
        this.cancelled = false;
    }

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(final boolean cancelled) {
        this.cancelled = cancelled;
    }
}
