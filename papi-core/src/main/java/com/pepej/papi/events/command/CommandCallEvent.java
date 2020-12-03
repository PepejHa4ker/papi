package com.pepej.papi.events.command;

import com.pepej.papi.command.Command;
import com.pepej.papi.events.PapiEvent;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents an event that is called when a sender calls the command
 */
@EqualsAndHashCode(callSuper = true)
@Value
public class CommandCallEvent extends PapiEvent {
    @NonNull Command command;
    @NonNull CommandSender caller;
}
