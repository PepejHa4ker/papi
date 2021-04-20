package com.pepej.papi.command.functional;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.command.AbstractCommand;
import com.pepej.papi.command.CommandInterruptException;
import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.command.functional.handler.FunctionalCommandHandler;
import com.pepej.papi.command.functional.handler.FunctionalTabHandler;
import com.pepej.papi.utils.Players;
import com.pepej.papi.utils.TabHandlers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static com.pepej.papi.utils.ImmutableCollectors.toList;

@SuppressWarnings("rawtypes")
class FunctionalCommand<T extends CommandSender> extends AbstractCommand {
    private final ImmutableList<Predicate<CommandContext<?>>> predicates;
    private final FunctionalCommandHandler handler;
    private @Nullable final FunctionalTabHandler tabHandler;
    FunctionalCommand(ImmutableList<Predicate<CommandContext<?>>> predicates, FunctionalCommandHandler handler, FunctionalTabHandler tabHandler, @Nullable String permission, @Nullable String permissionMessage, @Nullable String description) {
        this.predicates = predicates;
        this.handler = handler;
        this.tabHandler = tabHandler;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.description = description;
    }

    @Override
    public void call(@NonNull CommandContext<? extends CommandSender> context) throws CommandInterruptException {
        for (Predicate<CommandContext<?>> predicate : this.predicates) {
            if (!predicate.test(context)) {
                return;
            }
        }

        //noinspection unchecked
        this.handler.handle(context);
    }

    @Nullable
    @Override
    public List<String> callTabCompleter(@NonNull CommandContext<? extends CommandSender> context) throws CommandInterruptException {
        if (tabHandler == null) {
            return TabHandlers.players("");
        }
        for (Predicate<CommandContext<?>> predicate : this.predicates) {
            if (!predicate.test(context)) {
                return Arrays.asList("");
            }
        }

        //noinspection unchecked
        return this.tabHandler.handle(context);
    }
}
