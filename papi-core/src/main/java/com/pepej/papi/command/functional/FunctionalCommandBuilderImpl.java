package com.pepej.papi.command.functional;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.pepej.papi.command.Command;
import com.pepej.papi.command.context.CommandContext;
import com.pepej.papi.cooldown.Cooldown;
import com.pepej.papi.cooldown.CooldownMap;
import com.pepej.papi.text.Text;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

class FunctionalCommandBuilderImpl<T extends CommandSender> implements FunctionalCommandBuilder<T> {
    private final ImmutableList.Builder<Predicate<CommandContext<?>>> predicates;
    private @Nullable String permission;
    private @Nullable String permissionMessage;
    private @Nullable String description;

    private FunctionalCommandBuilderImpl(final @Nonnull ImmutableList.Builder<Predicate<CommandContext<?>>> predicates, @Nullable String permission, @Nullable String permissionMessage, @Nullable String description) {
        this.predicates = predicates;
        this.permission = permission;
        this.permissionMessage = permissionMessage;
        this.description = description;
    }

    FunctionalCommandBuilderImpl() {
        this(ImmutableList.builder(), null, null, null);
    }

    public FunctionalCommandBuilder<T> description(final @Nonnull String description) {
        Objects.requireNonNull(description, "description");
        this.description = description;
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test) {
        this.predicates.add((Predicate<CommandContext<?>>) test);
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertPermission(final @Nonnull String permission, final @Nullable String failureMessage) {
        Objects.requireNonNull(permission, "permission");
        this.permission = permission;
        this.permissionMessage = failureMessage;
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertOp(final @Nonnull String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender().isOp()) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<Player> assertPlayer(final @Nonnull String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender() instanceof Player) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        // cast the generic type
        return new FunctionalCommandBuilderImpl<>(this.predicates, this.permission, this.permissionMessage, this.description);
    }

    @Override
    public FunctionalCommandBuilder<ConsoleCommandSender> assertConsole(final @Nonnull String failureMessage) {
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            if (context.sender() instanceof ConsoleCommandSender) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        // cast the generic type
        return new FunctionalCommandBuilderImpl<>(this.predicates, this.permission, this.permissionMessage, this.description);
    }

    @Override
    public FunctionalCommandBuilder<T> assertUsage(final  @Nonnull String usage, final @Nonnull String failureMessage) {
        Objects.requireNonNull(usage, "usage");
        Objects.requireNonNull(failureMessage, "failureMessage");

        List<String> usageParts = Splitter.on(" ").splitToList(usage);

        int requiredArgs = 0;
        for (String usagePart : usageParts) {
            if (!usagePart.startsWith("[") && !usagePart.endsWith("]")) {
                // assume it's a required argument
                requiredArgs++;
            }
        }

        int finalRequiredArgs = requiredArgs;
        this.predicates.add(context -> {
            if (context.args().size() >= finalRequiredArgs) {
                return true;
            }

            context.reply(failureMessage.replace("{usage}", "/" + context.label() + " " + usage));
            return false;
        });

        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertArgument(final int index, final @Nonnull Predicate<String> test ,final @Nonnull String failureMessage) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            String arg = context.rawArg(index);
            if (test.test(arg)) {
                return true;
            }
            context.reply(failureMessage.replace("{arg}", arg).replace("{index}", Integer.toString(index)));
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertCooldown(final int cooldownTime, final @Nonnull TimeUnit unit, final @Nonnull String cooldownMessage) {
        CooldownMap<CommandSender> commandCooldown = CooldownMap.create(Cooldown.of(cooldownTime, unit));
        this.predicates.add(context -> {
            if (commandCooldown.test(context.sender())) {
                return true;
            }

            context.sender().sendMessage(Text.colorize(cooldownMessage.replace("{cooldown}", commandCooldown.remainingTime(context.sender(), unit) + "")));
            return false;
        });
        return this;
    }

    @Override
    public FunctionalCommandBuilder<T> assertSender(final @Nonnull Predicate<T> test, final @Nonnull String failureMessage) {
        Objects.requireNonNull(test, "test");
        Objects.requireNonNull(failureMessage, "failureMessage");
        this.predicates.add(context -> {
            //noinspection unchecked
            T sender = (T) context.sender();
            if (test.test(sender)) {
                return true;
            }

            context.reply(failureMessage);
            return false;
        });
        return this;
    }

    @Override
    public Command handler(FunctionalCommandHandler handler) {
        Objects.requireNonNull(handler, "handler");
        return new FunctionalCommand(this.predicates.build(), handler, permission, permissionMessage, description);
    }
}
