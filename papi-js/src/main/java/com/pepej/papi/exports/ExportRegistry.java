package com.pepej.papi.exports;

import java.util.Collection;

/**
 * A registry of {@link Export}s shared between scripts.
 *
 * <p>Allows scripts to share persistent state, or provide a resource in a known
 * namespace.</p>
 *
 * <p>Some scripts will be designed to be totally stateless, and may use exports
 * to store state between invocations.</p>
 */
public interface ExportRegistry {

    /**
     * Creates a new standalone {@link ExportRegistry}.
     *
     * @return a new export registry
     */
    static ExportRegistry create() {
        return new ExportRegistryImpl();
    }

    /**
     * Gets an export
     *
     * @param name the name of the export
     * @param <T> the export type
     * @return the export
     */
    <T> Export<T> get(String name);

    /**
     * Gets a pointer to an export
     *
     * @param name the name of the export
     * @param <T> the export type
     * @return a pointer
     * @see Export.Pointer
     */
    default <T> Export.Pointer<T> pointer(String name) {
        return this.<T>get(name).pointer();
    }

    /**
     * Deletes an export
     *
     * @param name the name of the export to remove.
     */
    void remove(String name);

    /**
     * Returns a collection of all known exports.
     *
     * @return a collection of known exports
     */
    Collection<Export<?>> getAll();

}
