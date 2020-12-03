package com.pepej.papi.command;

import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.command.context.ImmutableCommandContext;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.utils.CommandMapUtil;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * An abstract implementation of {@link Command} and {@link CommandExecutor}
 */

public abstract class AbstractCommand implements Command, CommandExecutor {

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
            call(context);
        } catch (CommandInterruptException e) {
            e.getAction().accept(context.sender());
        }
        return true;
    }
}
