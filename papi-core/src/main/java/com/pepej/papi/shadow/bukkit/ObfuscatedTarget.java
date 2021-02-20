package com.pepej.papi.shadow.bukkit;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.TargetResolver;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * Defines a class, method or field target with a value that varies between package versions.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ObfuscatedTarget {

    /**
     * Gets the mappings.
     *
     * @return the mappings
     */
    Mapping[] value();

    /**
     * A {@link TargetResolver} for the {@link ObfuscatedTarget} annotation.
     */
    TargetResolver RESOLVER = new TargetResolver() {
        @Override
        public @NonNull Optional<Class<?>> lookupClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException {
            String className = Optional.ofNullable(shadowClass.getAnnotation(ObfuscatedTarget.class))
                                       .flatMap(annotation -> Arrays.stream(annotation.value())
                                                                    .filter(mapping -> PackageVersion.runtimeVersion() == mapping.version())
                                                                    .findFirst()
                                       )
                                       .map(Mapping::value)
                                       .orElse(null);

            if (className == null) {
                return Optional.empty();
            }
            return Optional.of(Class.forName(className));
        }

        @Override
        public @NonNull Optional<String> lookupMethod(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
            return Optional.ofNullable(shadowMethod.getAnnotation(ObfuscatedTarget.class))
                           .flatMap(annotation -> Arrays.stream(annotation.value())
                                                        .filter(mapping -> PackageVersion.runtimeVersion() == mapping.version())
                                                        .findFirst()
                           )
                           .map(Mapping::value);
        }

        @Override
        public @NonNull Optional<String> lookupField(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
            return Optional.ofNullable(shadowMethod.getAnnotation(ObfuscatedTarget.class))
                           .flatMap(annotation -> Arrays.stream(annotation.value())
                                                        .filter(mapping -> PackageVersion.runtimeVersion() == mapping.version())
                                                        .findFirst()
                           )
                           .map(Mapping::value);
        }
    };

}
