package com.pepej.papi.ap;

import org.checkerframework.checker.nullness.qual.NonNull;

public @interface PluginDependency {

    /**
     * The name of the plugin
     *
     * @return the name of the plugin
     */
    @NonNull
    String value();

    /**
     * If this is a "soft" dependency
     *
     * @return true if this is a "soft" dependency
     */
    boolean soft() default false;

}
