package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Defines a class, method or field target with a constant, known value.
 */
@java.lang.annotation.Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Target {

    /**
     * Gets the value.
     *
     * @return the value
     */
    @NonNull String value();

    /**
     * A {@link TargetResolver} for the {@link Target} annotation.
     */
    TargetResolver RESOLVER = new TargetResolver() {
        @Override
        public @NonNull Optional<Class<?>> lookupClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException {
            Target annotation = shadowClass.getAnnotation(Target.class);
            if (annotation == null) {
                return Optional.empty();
            }

            return Optional.of(Class.forName(annotation.value()));
        }

        @Override
        public @NonNull Optional<String> lookupMethod(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
            return Optional.ofNullable(shadowMethod.getAnnotation(Target.class)).map(Target::value);
        }

        @Override
        public @NonNull Optional<String> lookupField(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
            return Optional.ofNullable(shadowMethod.getAnnotation(Target.class)).map(Target::value);
        }
    };

}