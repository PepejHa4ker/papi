package com.pepej.papi.bindings;

import javax.script.Bindings;
import java.util.function.Consumer;

/**
 * Chainable bindings builder.
 *
 * @see Bindings
 */
public interface BindingsBuilder {

    /**
     * Creates a new {@link BindingsBuilder}
     *
     * @param bindings the bindings to apply to
     * @return a new builder
     */
    static BindingsBuilder wrap(Bindings bindings) {
        return new BindingsBuilderImpl(bindings);
    }

    /**
     * Adds a binding to the builder
     *
     * @param name the name of the binding
     * @param object the value of the binding
     * @return this builder (for chaining)
     */
    BindingsBuilder put(String name, Object object);

    /**
     * Applies an action to this builder
     *
     * @param action the action to apply
     * @return this builder (for chaining)
     */
    BindingsBuilder apply(Consumer<Bindings> action);

    /**
     * Returns the modified {@link Bindings} instance
     *
     * @return the bindings
     */
    Bindings build();

}

