package com.pepej.papi.command.context;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.command.argument.Argument;
import com.pepej.papi.command.argument.SimpleArgument;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ImmutableCommandContext<T extends CommandSender> implements CommandContext<T> {
    private final T sender;
    private final String label;
    private final ImmutableList<String> args;

    public ImmutableCommandContext(T sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = ImmutableList.copyOf(args);
    }

    @Nonnull
    @Override
    public T sender() {
        return this.sender;
    }

    @Nonnull
    @Override
    public ImmutableList<String> args() {
        return this.args;
    }

    @Nonnull
    @Override
    public Argument arg(int index) {
        return new SimpleArgument(index, rawArg(index));
    }

    @Nullable
    @Override
    public String rawArg(int index) {
        if (index < 0 || index >= this.args.size()) {
            return null;
        }
        return this.args.get(index);
    }

    @Nonnull
    @Override
    public String label() {
        return this.label;
    }
}
