package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Marks a "shadow" interface.
 *
 * <p>{@link Shadow}s are implemented at runtime by the {@link ShadowFactory}.</p>
 */
public interface Shadow {

    /**
     * Gets the shadow class that was defined when this {@link Shadow} was
     * constructed.
     *
     * @return the shadow class
     */
    @NonNull Class<? extends Shadow> getShadowClass();

    /**
     * Gets the target (handle) object for this shadow.
     *
     * <p>Will return null for static shadows.</p>
     *
     * @return the shadow target
     */
    @Nullable Object getShadowTarget();

}
