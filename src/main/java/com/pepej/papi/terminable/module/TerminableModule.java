package com.pepej.papi.terminable.module;

import com.pepej.papi.terminable.Terminable;
import com.pepej.papi.terminable.TerminableConsumer;

import javax.annotation.Nonnull;

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
    void setup(@Nonnull TerminableConsumer consumer);

    /**
     * Registers this terminable with a terminable consumer
     *
     * @param consumer the terminable consumer
     */
    default void bindModuleWith(@Nonnull TerminableConsumer consumer) {
        consumer.bindModule(this);
    }

}