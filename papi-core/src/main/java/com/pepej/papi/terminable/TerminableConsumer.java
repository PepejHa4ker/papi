package com.pepej.papi.terminable;

import com.pepej.papi.terminable.module.TerminableModule;
import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * Accepts {@link AutoCloseable}s (and by inheritance {@link Terminable}s),
 * as well as {@link TerminableModule}s.
 */
@FunctionalInterface
public interface TerminableConsumer {

    /**
     * Binds with the given terminable.
     *
     * @param <T> the terminable type
     * @param terminable the terminable to bind with
     * @return the same terminable
     */
    @NonNull
    <T extends AutoCloseable> T bind(@NonNull T terminable);

    /**
     * Binds with the given terminable module.
     *
     * @param module the module to bind with
     * @param <T> the module type
     * @return the same module
     */
    @NonNull
    default <T extends TerminableModule> T bindModule(@NonNull T module) {
        module.setup(this);
        return module;
    }

}
