package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * Defines a class target with a constant, known value.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ClassTarget {

    /**
     * Gets the value.
     *
     * @return the value
     */
    @NonNull Class<?> value();

    /**
     * A {@link TargetResolver} for the {@link ClassTarget} annotation.
     */
    TargetResolver RESOLVER = new TargetResolver() {
        @Override
        public @NonNull Optional<Class<?>> lookupClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException {
            return Optional.ofNullable(shadowClass.getAnnotation(ClassTarget.class)).map(ClassTarget::value);
        }
    };

}
