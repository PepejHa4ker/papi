package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A {@link TargetResolver} for fields to match common "getter" and "setter" method patterns.
 */
final class FuzzyFieldTargetResolver implements TargetResolver {
    static final FuzzyFieldTargetResolver INSTANCE = new FuzzyFieldTargetResolver();

    private static final Pattern GETTER_PATTERN = Pattern.compile("(get)[A-Z].*");
    private static final Pattern GETTER_IS_PATTERN = Pattern.compile("(is)[A-Z].*");
    private static final Pattern SETTER_PATTERN = Pattern.compile("(set)[A-Z].*");

    private FuzzyFieldTargetResolver() {

    }

    @Override
    public @NonNull Optional<String> lookupField(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
        String methodName = shadowMethod.getName();
        Matcher matcher = GETTER_PATTERN.matcher(methodName);
        if (matcher.matches()) {
            return Optional.of(methodName.substring(3, 4).toLowerCase() + methodName.substring(4));
        }

        matcher = GETTER_IS_PATTERN.matcher(methodName);
        if (matcher.matches()) {
            return Optional.of(methodName.substring(2, 3).toLowerCase() + methodName.substring(3));
        }

        matcher = SETTER_PATTERN.matcher(methodName);
        if (matcher.matches()) {
            return Optional.of(methodName.substring(3, 4).toLowerCase() + methodName.substring(4));
        }

        return Optional.empty();
    }
}
