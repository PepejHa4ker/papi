package com.pepej.papi.maven;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * Annotation to indicate the required libraries for a class.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibraries {

    @Nonnull
    MavenLibrary[] value() default {};

}
