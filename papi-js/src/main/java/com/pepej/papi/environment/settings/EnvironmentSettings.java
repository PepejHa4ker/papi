package com.pepej.papi.environment.settings;

import com.pepej.papi.bindings.BindingsSupplier;
import com.pepej.papi.environment.ScriptEnvironment;
import com.pepej.papi.environment.loader.ScriptLoadingExecutor;
import com.pepej.papi.internal.ScriptControllerImpl;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Represents the settings for a given {@link ScriptEnvironment}.
 */
public interface EnvironmentSettings {

    /**
     * Creates a new {@link Builder}.
     *
     * @return a new builder
     */
    static Builder builder() {
        //noinspection deprecation
        return ScriptControllerImpl.newSettingsBuilder();
    }

    /**
     * Returns a default set of environment settings
     *
     * @return the default settings
     */
    static EnvironmentSettings defaults() {
        //noinspection deprecation
        return ScriptControllerImpl.defaultSettings();
    }

    /**
     * Returns a builder encapsulating the properties already defined by this
     * instance
     *
     * @return a builder
     */
    default Builder toBuilder() {
        return builder().mergeSettingsFrom(this);
    }

    /**
     * Builds {@link EnvironmentSettings}
     */
    interface Builder {

        /**
         * Applies the settings from the give instance to this builder
         *
         * @param other the other settings
         * @return this builder
         */
        Builder mergeSettingsFrom(EnvironmentSettings other);

        /**
         * Define the executor service used to setup task to poll scripts for
         * changes and load new scripts.
         *
         * @param executor the executor
         * @return this builder
         */
        Builder loadExecutor(ScriptLoadingExecutor executor);

        /**
         * Define the executor used to run scripts
         *
         * @param executor the executor
         * @return this builder
         */
        Builder runExecutor(Executor executor);

        /**
         * Adds a bindings supplier to the settings
         *
         * @param supplier the bindings supplier
         * @return this builder
         */
        Builder withBindings(BindingsSupplier supplier);

        /**
         * Marks that a {@link Package} should be imported by default.
         *
         * @param packageName the name of the package - see {@link Package#getName()}.
         * @return this builder
         */
        Builder withDefaultPackageImport(String packageName);

        /**
         * Marks that {@link Package}s should be imported by default.
         *
         * @param packageNames the package names - see {@link Package#getName()}.
         * @return this builder
         */
        Builder withDefaultPackageImports(Collection<String> packageNames);

        /**
         * Marks that a {@link Class} should be imported by default.
         *
         * @param type the name of the class - see {@link Class#getName()}
         * @return this builder
         */
        Builder withDefaultTypeImport(String type);

        /**
         * Marks that {@link Class}es should be imported by default.
         *
         * @param types class names - see {@link Class#getName()}
         * @return this builder
         */
        Builder withDefaultTypeImports(Collection<String> types);

        /**
         * Define how often the script loader should poll scripts for updates
         *
         * @param time the time
         * @param unit the unit
         * @return this builder
         */
        Builder pollRate(long time, TimeUnit unit);

        /**
         * Defines the init script for the environment
         *
         * @param path the path
         * @return this builder
         */
        Builder initScript(String path);

        /**
         * Builds a new {@link EnvironmentSettings} instance.
         *
         * @return the resultant environment settings
         */
        EnvironmentSettings build();

    }

}

