package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A utility for constructing private method handles.
 */
final class PrivateMethodHandles {

    private static final @NonNull Constructor<MethodHandles.Lookup> LOOKUP_CONSTRUCTOR;
    static {
        try {
            LOOKUP_CONSTRUCTOR = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
            LOOKUP_CONSTRUCTOR.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Returns a {@link MethodHandles.Lookup lookup object} with full capabilities to emulate all
     * supported bytecode behaviors, including private access, on a target class.
     *
     * @param targetClass the target class
     * @return a lookup object for the target class, with private access
     */
    public static MethodHandles.@NonNull Lookup forClass(@NonNull Class<?> targetClass) {
        try {
            return LOOKUP_CONSTRUCTOR.newInstance(targetClass, MethodHandles.Lookup.PUBLIC | MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}