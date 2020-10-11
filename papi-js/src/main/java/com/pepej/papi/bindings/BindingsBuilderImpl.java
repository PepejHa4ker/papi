package com.pepej.papi.bindings;

import javax.script.Bindings;
import java.util.function.Consumer;

final class BindingsBuilderImpl implements BindingsBuilder {
    private final Bindings bindings;

    BindingsBuilderImpl(Bindings bindings) {
        this.bindings = bindings;
    }

    @Override
    public BindingsBuilder put(String name, Object object) {
        this.bindings.put(name, object);
        return this;
    }

    @Override
    public BindingsBuilder apply(Consumer<Bindings> action) {
        action.accept(this.bindings);
        return this;
    }

    @Override
    public Bindings build() {
        return this.bindings;
    }
}

