package com.pepej.papi.shadow;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.util.Optional;

/**
 * Defines a class target with a dynamic value, calculated on demand by a function.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicClassTarget {

    /**
     * Gets the loading function class.
     *
     * <p>An instance of the function is retrieved/constructed on demand by the implementation in
     * the following order.</p>
     * <p></p>
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
         * Computes the target class for the given {@code shadowClass}.
         *
         * @param shadowClass the shadow class to compute a target for
         * @return the target
         * @throws ClassNotFoundException if the resultant target class cannot be loaded
         */
        @NonNull Class<?> computeClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException;
    }

    /**
     * A {@link TargetResolver} for the {@link DynamicClassTarget} annotation.
     */
    TargetResolver RESOLVER = new TargetResolver() {
        @Override
        public @NonNull Optional<Class<?>> lookupClass(@NonNull Class<? extends Shadow> shadowClass) throws ClassNotFoundException {
            DynamicClassTarget annotation = shadowClass.getAnnotation(DynamicClassTarget.class);
            if (annotation == null) {
                return Optional.empty();
            }

            return Optional.of(Reflection.getInstance(DynamicClassTarget.Function.class, annotation.value()).computeClass(shadowClass));
        }
    };

}
