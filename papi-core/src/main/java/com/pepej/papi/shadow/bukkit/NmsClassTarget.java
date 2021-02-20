package com.pepej.papi.shadow.bukkit;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.TargetResolver;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

/**
 * Defines a class target relative to the versioned 'net.minecraft.server' package.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NmsClassTarget {

    /**
     * Gets the value of the class, relative to the versioned package.
     *
     * @return the value
     */
    @NonNull String value();

    /**
     * A {@link TargetResolver} for the {@link NmsClassTarget} annotation.
     */
    TargetResolver RESOLVER = new TargetResolver() {
        @Override
        public @NonNull Optional<Class<?>> lookupClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException {
            NmsClassTarget annotation = shadowClass.getAnnotation(NmsClassTarget.class);
            if (annotation == null) {
                return Optional.empty();
            }

            return Optional.of(PackageVersion.runtimeVersion().nmsClass(annotation.value()));
        }
    };

}
