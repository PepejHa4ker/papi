package com.pepej.papi.command.functional;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.pepej.papi.command.AbstractCommand;
import com.pepej.papi.command.CommandInterruptException;
import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.command.event.CommandCallEvent;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Predicate;

class FunctionalCommand extends AbstractCommand {
    private final ImmutableList<Predicate<CommandContext<?>>> predicates;
    private final FunctionalCommandHandler handler;

    FunctionalCommand(ImmutableList<Predicate<CommandContext<?>>> predicates, FunctionalCommandHandler handler, @Nullable String permission, @Nullable String permissionMessage, @Nullable String description) {
        this.predicates = predicates;
        this.handler = handler;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.description = description;
    }

    @Override
    public void call(@Nonnull CommandContext<?> context) throws CommandInterruptException {
        for (Predicate<CommandContext<?>> predicate : this.predicates) {
            if (!predicate.test(context)) {
                return;
            }
        }

        context.arg(0).parseOrFail(TypeToken.of(Integer.class));
        Bukkit.getPluginManager().callEvent(new CommandCallEvent(this, context.sender()));
        //noinspection unchecked
        this.handler.handle(context);
    }
}
