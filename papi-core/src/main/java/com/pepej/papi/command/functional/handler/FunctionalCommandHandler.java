package com.pepej.papi.command.functional.handler;

import com.pepej.papi.command.Command;
import com.pepej.papi.command.CommandInterruptException;
import com.pepej.papi.command.context.CommandContext;
import org.bukkit.command.CommandSender;

/**
 * Represents a handler for a {@link Command}
 *
 * @param <T> the sender type
 */
@FunctionalInterface
public interface FunctionalCommandHandler<T extends CommandSender>  {

    /**
     * Executes the handler using the given command context
     *
     * @param context the command context
     * @throws CommandInterruptException if command was interrupted
     */
    void handle(CommandContext<T> context) throws CommandInterruptException;

}
