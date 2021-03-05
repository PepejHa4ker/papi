package com.pepej.papi.shadow;

import com.pepej.papi.utils.Reflection;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Defines a method target with a dynamic value, calculated on demand by a function.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicMethodTarget {

    /**
     * Gets the loading function class.
     *
     * <p>An instance of the function is retrieved/constructed on demand by the implementation in
     * the following order.</p>
     * <ul>
     * <li>a static method named {@code getInstance} accepting no parameters and returning an instance of the implementation.</li>
     * <li>via a single enum constant, if the loading function class is an enum following the enum singleton pattern.</li>
     * <li>a static field named {@code instance} with the same type as and containing an instance of the implementation.</li>
     * <li>a static field named {@code INSTANCE} with the same type as and containing an instance of the implementation.</li>
     * <li>a no-args constructor</li>
     * </ul>
     *
     * <p>Values defined for this property should be aware of this, and ensure an instance can be
     * retrieved/constructed.</p>
     *
     * @return the loading function class
     */
    @NonNull Class<? extends Function> value();

    /**
     * A functional interface encapsulating the target value computation.
     */
    @FunctionalInterface
    interface Function {
        /**
         * Computes the target method for the given {@code shadowMethod}.
         *
         * @param shadowMethod the shadow method to compute a method target for
         * @param shadowClass the class defining the shadow method
         * @param targetClass the target class. the resultant method target should resolve for this class.
         * @return the target
         */
        @NonNull String computeMethod(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass);
    }

    /**
     * A {@link TargetResolver} for the {@link DynamicMethodTarget} annotation.
     */
    TargetResolver RESOLVER = new TargetResolver() {
        @Override
        public @NonNull Optional<String> lookupMethod(@NonNull Method shadowMethod, @NonNull Class<? extends Shadow> shadowClass, @NonNull Class<?> targetClass) {
            DynamicMethodTarget annotation = shadowMethod.getAnnotation(DynamicMethodTarget.class);
            if (annotation == null) {
                return Optional.empty();
            }

            return Optional.of(Reflection.getInstance(DynamicMethodTarget.Function.class, annotation.value()).computeMethod(shadowMethod, shadowClass, targetClass));
        }
    };

}
