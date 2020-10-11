package com.pepej.papi.maven;

import javax.annotation.Nonnull;
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
     * The group id of the library
     *
     * @return the group id of the library
     */
    @Nonnull
    String groupId();

    /**
     * The artifact id of the library
     *
     * @return the artifact id of the library
     */
    @Nonnull
    String artifactId();

    /**
     * The version of the library
     *
     * @return the version of the library
     */
    @Nonnull
    String version();

    /**
     * The repo where the library can be obtained from
     *
     * @return the repo where the library can be obtained from
     */
    @Nonnull
    Repository repo() default @Repository(url = "https://repo1.maven.org/maven2");

}
