package com.pepej.papi.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.*;
import java.util.Arrays;

public final class Reflection {

    public static Class<?> getCallerClass(int level) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String rawFQN = trace[level+1].toString().split("\\(")[0];
        try {
            return Class.forName(rawFQN.substring(0, rawFQN.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void assertCallerClass(int level, Class<?>... allowedClasses) {
        Class<?> callerClass = getCallerClass(level);

        for (Class<?> allowedClass : allowedClasses) {
            if (allowedClass == callerClass) {
                return;
            }
        }
        throw new IllegalStateException("Invalid caller class " + callerClass + " should be one of " + Arrays.toString(allowedClasses));
    }

    public static void ensureStatic(Member member) {
        if (!Modifier.isStatic(member.getModifiers())) {
            throw new IllegalArgumentException();
        }
    }

    public static <T> @NonNull T getInstance(@NonNull Class<T> returnType, @NonNull Class<? extends T> implementationType) {
        try {
            Method getInstanceMethod = implementationType.getDeclaredMethod("getInstance");
            ensureStatic(getInstanceMethod);
            if (getInstanceMethod.getParameterCount() != 0) {
                throw new IllegalArgumentException();
            }
            if (!returnType.isAssignableFrom(getInstanceMethod.getReturnType())) {
                throw new IllegalArgumentException();
            }
            ensureAccessible(getInstanceMethod);
            //noinspection unchecked
            return (T) getInstanceMethod.invoke(null);
        } catch (Exception e) {
            // ignore
        }

        if (implementationType.isEnum()) {
            T[] enumConstants = implementationType.getEnumConstants();
            if (enumConstants.length == 1) {
                return enumConstants[0];
            }
        }

        try {
            Field instanceField = implementationType.getDeclaredField("instance");
            ensureStatic(instanceField);
            if (!returnType.isAssignableFrom(instanceField.getType())) {
                throw new IllegalArgumentException();
            }
            ensureAccessible(instanceField);
            //noinspection unchecked
            return (T) instanceField.get(null);
        } catch (Exception e) {
            // ignore
        }

        try {
            Field instanceField = implementationType.getDeclaredField("INSTANCE");
            ensureStatic(instanceField);
            if (!returnType.isAssignableFrom(instanceField.getType())) {
                throw new IllegalArgumentException();
            }
            ensureAccessible(instanceField);
            //noinspection unchecked
            return (T) instanceField.get(null);
        } catch (Exception e) {
            // ignore
        }

        try {
            Constructor<? extends T> constructor = implementationType.getDeclaredConstructor();
            ensureAccessible(constructor);
            return constructor.newInstance();
        } catch (Exception e) {
            // ignore
        }

        throw new RuntimeException("Unable to obtain an instance of " + implementationType.getName());
    }

    public static void ensureAccessible(AccessibleObject accessibleObject) {
        if (!accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
    }

    public static void ensureModifiable(Field field) {
        if (Modifier.isFinal(field.getModifiers())) {
            try {
                Field modifierField = Field.class.getDeclaredField("modifiers");
                modifierField.setAccessible(true);
                modifierField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Field findField(Class<?> searchClass, String fieldName) {
        Field field = null;
        do {
            try {
                field = searchClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                searchClass = searchClass.getSuperclass();
            }
        } while (field == null && searchClass != Object.class);
        return field;
    }

    private Reflection() {

    }

}
