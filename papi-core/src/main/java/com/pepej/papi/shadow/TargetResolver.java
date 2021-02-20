package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Interface to represent a resolver which can identify the corresponding targets for
 * shadow classes, methods and fields.
 */
public interface TargetResolver {

    /**
     * Attempts to find the corresponding target class for the given shadow class.
     *
     * @param shadowClass the shadow class
     * @return the target, if any
     * @throws ClassNotFoundException if the resultant target class cannot be loaded
     */
    default @NonNull Optional<Class<?>> lookupClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException {
        return Optional.empty();
    }

    /**
     * Attempts to find the corresponding target method name for the given shadow method.
     *
     * @param shadowMethod the shadow method to lookup a target method for
     * @param shadowClass the class defining the shadow method
     * @param targetClass the target class. the resultant method should resolve for this class.
     * @return the target, if any
     */
    default @NonNull Optional<String> lookupMethod(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
        return Optional.empty();
    }

    /**
     * Attempts to find the corresponding target field name for the given shadow method.
     *
     * @param shadowMethod the shadow method to lookup a target field for
     * @param shadowClass the class defining the shadow method
     * @param targetClass the target class. the resultant method should resolve for this class.
     * @return the target, if any
     */
    default @NonNull Optional<String> lookupField(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
        return Optional.empty();
    }

}
