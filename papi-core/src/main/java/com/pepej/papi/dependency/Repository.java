package com.pepej.papi.dependency;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.*;

/**
 * Represents a maven repository.
 */
@Documented
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

    String DEFAULT_MAVEN_REPOSITORY = "https://repo1.maven.org/maven2";

    /**
     * Gets the base url of the repository.
     *
     * @return the base url of the repository
     */
    @NonNull
    String url();

}
