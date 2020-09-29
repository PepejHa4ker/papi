package com.pepej.papi.plugin.ap;


import javax.annotation.Nonnull;
import java.lang.annotation.*;

/**
 * Annotation to automatically generate plugin.yml files for helper projects
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
    @Nonnull
    String name();

    /**
     * The plugin version
     *
     * @return the plugin version
     */
    @Nonnull
    String version() default "";

    /**
     * A description of the plugin
     *
     * @return a description of the plugin
     */
    @Nonnull
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
    @Nonnull
    String[] authors() default {};

    /**
     * A website for the plugin
     *
     * @return a website for the plugin
     */
    @Nonnull
    String website() default "";

    /**
     * A list of dependencies for the plugin
     *
     * @return a list of dependencies for the plugin
     */
    @Nonnull
    PluginDependency[] depends() default {};

    /**
     * A list of hard dependencies for the plugin
     *
     * @return a list of hard dependencies for the plugin
     */
    @Nonnull
    String[] hardDepends() default {};

    /**
     * A list of soft dependencies for the plugin
     *
     * @return a list of soft dependencies for the plugin
     */
    @Nonnull
    String[] softDepends() default {};

    /**
     * A list of plugins which should be loaded before this plugin
     *
     * @return a list of plugins which should be loaded before this plugin
     */
    @Nonnull
    String[] loadBefore() default {};

}