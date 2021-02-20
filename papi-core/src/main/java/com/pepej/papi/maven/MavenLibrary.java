package com.pepej.papi.maven;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.*;

/**
 * Annotation to indicate a required library for a class.
 */
@Documented
@Repeatable(MavenLibraries.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MavenLibrary {

    /**
     * The full name of the library. (like gradle style)
     *
     * @return the full id of the library
     */

    @NonNull
    String value() default "";

    /**
     * The group id of the library
     *
     * @return the group id of the library
     */
    @NonNull
    String groupId() default "";

    /**
     * The artifact id of the library
     *
     * @return the artifact id of the library
     */
    @NonNull
    String artifactId() default "";

    /**
     * The version of the library
     *
     * @return the version of the library
     */
    @NonNull
    String version() default "";

    /**
     * The repo where the library can be obtained from
     *
     * @return the repo where the library can be obtained from
     */
    @NonNull
    Repository repo() default @Repository(url = "https://repo1.maven.org/maven2");

}
