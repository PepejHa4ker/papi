package com.pepej.papi.dependency;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.*;

/**
 * Annotation to indicate the required libraries for a class.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Dependencies {

    @NonNull
    Dependency[] value() default {};

}
