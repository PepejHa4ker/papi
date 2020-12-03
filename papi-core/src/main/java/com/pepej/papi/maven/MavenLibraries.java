package com.pepej.papi.maven;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.*;

/**
 * Annotation to indicate the required libraries for a class.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibraries {

    @NonNull
    MavenLibrary[] value() default {};

}
