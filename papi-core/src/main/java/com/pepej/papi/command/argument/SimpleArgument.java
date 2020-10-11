package com.pepej.papi.command.argument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class SimpleArgument implements Argument {
    protected final int index;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected final Optional<String> value;

    public SimpleArgument(int index, @Nullable String value) {
        this.index = index;
        this.value = Optional.ofNullable(value);
    }

    @Override
    public int index() {
        return this.index;
    }

    @Nonnull
    @Override
    public Optional<String> value() {
        return this.value;
    }

    @Override
    public boolean isPresent() {
        return this.value.isPresent();
    }
}
