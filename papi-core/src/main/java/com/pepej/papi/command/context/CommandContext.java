package com.pepej.papi.command.context;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.command.argument.Argument;
import com.pepej.papi.utils.Players;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the context for a given command execution
 *
 * @param <T> the sender type
 */
public interface CommandContext<T extends CommandSender> {

    /**
     * Gets the sender who executed the command
     *
     * @return the sender who executed the command
     */
    @Nonnull
    T sender();

    /**
     * Sends a message to the {@link #sender()}.
     *
     * @param message the message to send
     */
    default void reply(String... message) {
        Players.msg(sender(), message);
    }

    /**
     * Gets an immutable list of the supplied arguments
     *
     * @return an immutable list of the supplied arguments
     */
    @Nonnull
    ImmutableList<String> args();

    /**
     * Gets the argument at a the given index
     *
     * @param index the index
     * @return the argument
     */
    @Nonnull
    Argument arg(int index);

    /**
     * Gets the argument at the given index.
     * Returns null if no argument is present at that index.
     *
     * @param index the index
     * @return the argument, or null if one was not present
     */
    @Nullable
    String rawArg(int index);

    /**
     * Gets the command label which was used to execute this command
     *
     * @return the command label which was used to execute this command
     */
    @Nonnull
    String label();

}
