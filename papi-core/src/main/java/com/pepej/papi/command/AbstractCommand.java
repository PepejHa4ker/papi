package com.pepej.papi.command;

import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.command.context.ImmutableCommandContext;
import com.pepej.papi.events.Events;
import com.pepej.papi.events.command.CommandCallEvent;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.utils.CommandMapUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.List;


/**
 * An abstract implementation of {@link Command} and {@link CommandExecutor}
 */

public abstract class AbstractCommand implements Command, CommandExecutor, TabExecutor {

    protected @Nullable String permission;
    protected @Nullable String permissionMessage;
    protected @Nullable String description;

    @Override
    public void register(@NonNull String... aliases) {
        LoaderUtils.getPlugin().registerCommand(this, permission, permissionMessage, description, aliases);
    }

    @Override
    public void close() {
        CommandMapUtil.unregisterCommand(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        CommandContext<CommandSender> context = new ImmutableCommandContext<>(sender, label, args);
        try {
            if (Events.callAndReturn(new CommandCallEvent<>(this, sender, context)).isCancelled()) {
                return true;
            }
            call(context);
        } catch (CommandInterruptException e) {
            e.getAction().accept(context.sender());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        CommandContext<CommandSender> context = new ImmutableCommandContext<>(sender, label, args);
        try {
            return callTabCompleter(context);
        } catch (CommandInterruptException e) {
            e.getAction().accept(context.sender());
        }
        return null;
    }
}
