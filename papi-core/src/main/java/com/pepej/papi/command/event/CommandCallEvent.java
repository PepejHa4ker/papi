package com.pepej.papi.command.event;

import com.pepej.papi.command.Command;
import com.pepej.papi.event.PapiEvent;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;

public class CommandCallEvent extends PapiEvent {

    private final Command command;
    private final CommandSender caller;

    public Command getCommand() {
        return command;
    }

    public CommandSender getCaller() {
        return caller;
    }

    public CommandCallEvent(@Nonnull final Command command, @Nonnull final CommandSender caller) {
        this.command = command;
        this.caller = caller;

    }
}
