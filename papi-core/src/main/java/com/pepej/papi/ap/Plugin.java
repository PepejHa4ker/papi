package com.pepej.papi.ap;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.annotation.*;

/**
 * Annotation to automatically generate plugin.yml files for papi projects
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Plugin {

    /**
     * The name of the plugin
     *
     * @return the name of the plugin
     */
    @NonNull
    String name();

    /**
     * The plugin version
     *
     * @return the plugin version
     */
    @NonNull
    String version() default "";

    /**
     * A description of the plugin
     *
     * @return a description of the plugin
     */
    @NonNull
    String description() default "";

    /**
     * The api version of the plugin
     *
     * @return the api version of the plugin
     */
    String apiVersion() default "";

    /**
     * The authors of the plugin
     *
     * @return the author of the plugin
     */
    @NonNull
    String[] authors() default {};

    /**
     * A website for the plugin
     *
     * @return a website for the plugin
     */
    @NonNull
    String website() default "";

    /**
     * A list of dependencies for the plugin
     *
     * @return a list of dependencies for the plugin
     */
    @NonNull
    PluginDependency[] depends() default {};

    /**
     * A list of hard dependencies for the plugin
     *
     * @return a list of hard dependencies for the plugin
     */
    @NonNull
    String[] hardDepends() default {};

    /**
     * A list of soft dependencies for the plugin
     *
     * @return a list of soft dependencies for the plugin
     */
    @NonNull
    String[] softDepends() default {};

    /**
     * A list of plugins which should be loaded before this plugin
     *
     * @return a list of plugins which should be loaded before this plugin
     */
    @NonNull
    String[] loadBefore() default {};

}