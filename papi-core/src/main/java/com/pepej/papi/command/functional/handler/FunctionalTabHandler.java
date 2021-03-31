package com.pepej.papi.command.functional.handler;

import com.pepej.papi.command.CommandInterruptException;
import com.pepej.papi.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

public interface FunctionalTabHandler<T extends CommandSender> {

    /**
     * Executes the tab completer using the given command context and returns the completions.
     *
     * @param c the command context
     * @return a {@link List} with the completions
     */
    @Nullable
    List<String> handle(CommandContext<T> c) throws CommandInterruptException;



}
