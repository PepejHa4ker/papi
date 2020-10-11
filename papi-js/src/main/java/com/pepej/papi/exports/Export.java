package com.pepej.papi.exports;

import java.util.function.Supplier;

/**
 * A namespaced value wrapper.
 *
 * @param <T> the export type
 */
public interface Export<T> {

    /**
     * Gets the name of the export
     *
     * @return the name
     */
    String name();

    /**
     * Returns a pointer to this export
     *
     * @return a pointer
     */
    Pointer<T> pointer();

    /**
     * Gets the current value of the export
     *
     * @return the current value
     */
    T get();

    /**
     * Gets the current value of the export, or returns the other if a value
     * isn't present.
     *
     * @param other the other value
     * @return the value
     */
    T get(T other);

    /**
     * Sets the value of the export
     *
     * @param value the value to set
     *  @return this
     */
    Export<T> put(T value);

    /**
     * Sets the value of the export if a value isn't already present,
     * then returns the export
     *
     * @param value the value to set if absent
     * @return this
     */
    Export<T> putIfAbsent(T value);

    /**
     * Uses the provided function to compute a value if one isn't already present.
     *
     * @param other the other value
     * @return the value
     */
    Export<T> computeIfAbsent(Supplier<? extends T> other);

    /**
     * Gets if this export has a value
     *
     * @return true if this export has a value
     */
    boolean containsValue();

    /**
     * Clears the export
     */
    void clear();

    /**
     * A pointer to the value of an export.
     *
     * <p>Can be used in scripts to simplify the process of obtaining an export
     * whose instance is likely to change during runtime.</p>
     *
     * <p>e.g.</p>
     *
     * <code>
     *     const someExport = exports.pointer("example-namespace");
     *     someExport().doSomething();
     * </code>
     *
     * @param <T> the type
     */
    interface Pointer<T> extends Supplier<T> {

    }

}