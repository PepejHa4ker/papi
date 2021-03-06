package com.pepej.papi.command;

import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.terminable.Terminable;
import com.pepej.papi.terminable.TerminableConsumer;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import javax.annotation.Nonnull;
import java.util.List;


/**
 * Represents a command
 */
public interface Command extends Terminable {

    /**
     * Registers this command with the server, via the given plugin instance
     *
     * @param aliases the aliases for the command
     */
    void register(@NonNull String... aliases);

    /**
     * Registers this command with the server, via the given plugin instance, and then binds it with the composite terminable.
     *
     * @param consumer the terminable consumer to bind with
     * @param aliases the aliases for the command
     */
    default void registerAndBind(@NonNull TerminableConsumer consumer, @NonNull String... aliases) {
        this.register(aliases);
        this.bindWith(consumer);
    }

    /**
     * Calls the command handler
     *
     * @param context the contexts for the command
     * @throws CommandInterruptException if command was interrupted
     */
    void call(@NonNull CommandContext<? extends CommandSender> context) throws CommandInterruptException;

    /**
     * Calls the command tab completer
     *
     * @param context the contexts for the command
     * @return a {@link List} with the completions
     */
    List<String> callTabCompleter(@Nonnull CommandContext<? extends CommandSender> context) throws CommandInterruptException;
}
