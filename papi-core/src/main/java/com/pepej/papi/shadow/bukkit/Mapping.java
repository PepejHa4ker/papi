package com.pepej.papi.shadow.bukkit;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Represents a target value for a specific {@link PackageVersion}.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {

    /**
     * Gets the package version.
     *
     * @return the package version
     */
    PackageVersion version();

    /**
     * Gets the target value.
     *
     * @return the value
     */
    String value();

}

