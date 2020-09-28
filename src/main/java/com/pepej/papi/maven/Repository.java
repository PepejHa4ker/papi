package com.pepej.papi.maven;

import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * Represents a maven repository.
 */
@Documented
@Target(ElementType.LOCAL_VARIABLE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {

    /**
     * Gets the base url of the repository.
     *
     * @return the base url of the repository
     */
    @Nonnull
    String url();

}
