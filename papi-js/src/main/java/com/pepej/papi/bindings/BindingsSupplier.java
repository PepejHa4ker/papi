package com.pepej.papi.bindings;

import com.pepej.papi.environment.script.Script;

import java.util.Map;

/**
 * Supplies a set of bindings for scripts to use at runtime.
 */
@FunctionalInterface
public interface BindingsSupplier {

    /**
     * Returns a {@link BindingsSupplier} that encapsulates a single binding.
     *
     * @param name the name of the binding
     * @param value the corresponding value
     * @return the resultant bindings supplier
     */
    static BindingsSupplier singleBinding(String name, Object value) {
        return (script, accumulator) -> accumulator.put(name, value);
    }

    /**
     * Returns a {@link BindingsSupplier} that encapsulates a map of objects.
     *
     * @param map the map of bindings
     * @return the resultant bindings supplier
     */
    static BindingsSupplier ofMap(Map<String, Object> map) {
        return (script, accumulator) -> {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                accumulator.put(entry.getKey(), entry.getValue());
            }
        };
    }

    /**
     * Supplies this suppliers bindings for the given script.
     *
     * @param script the script the bindings are for
     * @param accumulator the accumulator
     */
    void supplyBindings(Script script, BindingsBuilder accumulator);

}
