package com.pepej.papi.command;

import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.terminable.Terminable;
import com.pepej.papi.terminable.TerminableConsumer;

import javax.annotation.Nonnull;

/**
 * Represents a command
 */
public interface Command extends Terminable {

    /**
     * Registers this command with the server, via the given plugin instance
     *
     * @param aliases the aliases for the command
     */
    void register(@Nonnull String... aliases);

    /**
     * Registers this command with the server, via the given plugin instance, and then binds it with the composite terminable.
     *
     * @param consumer the terminable consumer to bind with
     * @param aliases the aliases for the command
     */
    default void registerAndBind(@Nonnull TerminableConsumer consumer, @Nonnull String... aliases) {
        register(aliases);
        bindWith(consumer);
    }

    /**
     * Calls the command handler
     *
     * @param context the contexts for the command
     */
    void call(@Nonnull CommandContext<?> context) throws CommandInterruptException;

}
