package com.pepej.papi.command;

import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

import static com.pepej.papi.text.Text.colorize;

/**
 * Exception thrown when the handling of a command should be interrupted.
 *
 * <p>This exception is silently swallowed by the command processing handler.</p>
 */
public class CommandInterruptException extends Exception {

    /**
     * Makes an assertion about a condition.
     *
     * <p>When used inside a command, command processing will be gracefully halted
     * if the condition is not true.</p>
     *
     * @param condition the condition
     * @param failMsg the message to send to the player if the assertion fails
     * @throws CommandInterruptException if the assertion fails
     */
    public static void makeAssertion(boolean condition, String failMsg) throws CommandInterruptException {
        if (!condition) {
            throw new CommandInterruptException(failMsg);
        }
    }

    private final Consumer<CommandSender> action;

    public CommandInterruptException(Consumer<CommandSender> action) {
        this.action = action;
    }

    public CommandInterruptException(String message) {
        super(message, null, false, false);
        this.action = cs -> cs.sendMessage(colorize("&c" + message));
    }

    public Consumer<CommandSender> getAction() {
        return this.action;
    }
}
