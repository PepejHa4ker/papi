package com.pepej.papi.terminable.module;

import com.pepej.papi.terminable.Terminable;
import com.pepej.papi.terminable.TerminableConsumer;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A terminable module is a class which manipulates and constructs a number
 * of {@link Terminable}s.
 */
public interface TerminableModule {

    /**
     * Performs the tasks to setup this module
     *
     * @param consumer the terminable consumer
     */
    void setup(@NonNull TerminableConsumer consumer);

    /**
     * Registers this terminable with a terminable consumer
     *
     * @param consumer the terminable consumer
     */
    default void bindModuleWith(@NonNull TerminableConsumer consumer) {
        consumer.bindModule(this);
    }

}