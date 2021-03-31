package com.pepej.papi.internal;

import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark papi implementation plugin.
 *
 * <p>For internal use only.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ApiStatus.Internal
public @interface PapiImplementationPlugin {

    /**
     * Returns the name of papi submodule
     * @return the papi submodule name
     */
    String value();
}
