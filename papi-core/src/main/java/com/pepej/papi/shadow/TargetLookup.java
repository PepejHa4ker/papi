package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Base implementation of {@link TargetResolver} that delegates to the default + other registered
 * resolvers.
 */
final class TargetLookup implements TargetResolver {

    private final @NonNull List<TargetResolver> resolvers = new CopyOnWriteArrayList<>(Arrays.asList(
            ClassTarget.RESOLVER,
            Target.RESOLVER,
            DynamicClassTarget.RESOLVER,
            DynamicMethodTarget.RESOLVER,
            DynamicFieldTarget.RESOLVER,
            FuzzyFieldTargetResolver.INSTANCE
    ));

    TargetLookup() {

    }

    public void registerResolver(@NonNull TargetResolver targetResolver) {
        Objects.requireNonNull(targetResolver, "targetResolver");
        if (!this.resolvers.contains(targetResolver)) {
            this.resolvers.add(0, targetResolver);
        }
    }

    @Override
    public @NonNull Optional<Class<?>> lookupClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException {
        for (TargetResolver resolver : this.resolvers) {
            Optional<Class<?>> result = resolver.lookupClass(shadowClass);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<String> lookupMethod(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
        for (TargetResolver resolver : this.resolvers) {
            Optional<String> result = resolver.lookupMethod(shadowMethod, shadowClass, targetClass);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    @Override
    public @NonNull Optional<String> lookupField(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
        for (TargetResolver resolver : this.resolvers) {
            Optional<String> result = resolver.lookupField(shadowMethod, shadowClass, targetClass);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

}
