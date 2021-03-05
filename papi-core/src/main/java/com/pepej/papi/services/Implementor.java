package com.pepej.papi.services;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Implementor {

    /**
     * Implementor class
     * @return the implementor class
     */
    Class<?> value();

    /**
     * Service priority
     * @return the service priority
     */
    ServicePriority priority() default ServicePriority.NORMAL;
}
