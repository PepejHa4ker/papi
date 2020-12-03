package com.pepej.papi.command.context;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.command.argument.Argument;
import com.pepej.papi.command.argument.SimpleArgument;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;


public class ImmutableCommandContext<T extends CommandSender> implements CommandContext<T> {
    private final T sender;
    private final String label;
    private final ImmutableList<String> args;

    public ImmutableCommandContext(T sender, String label, String[] args) {
        this.sender = sender;
        this.label = label;
        this.args = ImmutableList.copyOf(args);
    }

    @NonNull
    @Override
    public T sender() {
        return this.sender;
    }

    @NonNull
    @Override
    public ImmutableList<String> args() {
        return this.args;
    }

    @NonNull
    @Override
    public Argument arg(int index) {
        return new SimpleArgument(index, rawArg(index));
    }

    @NonNull
    @Override
    public String rawArg(int index) {
        if (index < 0 || index >= this.args.size()) {
            throw new IllegalArgumentException();
        }
        return this.args.get(index);
    }

    @NonNull
    @Override
    public String label() {
        return this.label;
    }
}
