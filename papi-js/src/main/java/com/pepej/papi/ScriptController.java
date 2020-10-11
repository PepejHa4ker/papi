package com.pepej.papi;

import com.pepej.papi.environment.settings.EnvironmentSettings;
import com.pepej.papi.environment.ScriptEnvironment;
import com.pepej.papi.internal.ScriptControllerImpl;
import com.pepej.papi.logging.SystemLogger;

import java.nio.file.Path;
import java.util.Collection;

/**
 * Controls the execution and management of {@link ScriptEnvironment}s.
 */
public interface ScriptController {

    /**
     * Creates a new {@link Builder} using the internal implementation.
     *
     * @return the builder
     */
    static Builder builder() {
        //noinspection deprecation
        return ScriptControllerImpl.builder();
    }

    /**
     * Gets the {@link ScriptEnvironment}s being processed by the controller
     *
     * @return the environments
     */
    Collection<ScriptEnvironment> getEnvironments();

    /**
     * Sets up a new {@link ScriptEnvironment} in the given load directory.
     *
     * @param loadDirectory the directory
     * @param settings the environment settings
     * @return the new environment
     * @throws UnsupportedOperationException if the controller does not support
     * setting up new environments after construction
     */
    ScriptEnvironment setupNewEnvironment(Path loadDirectory, EnvironmentSettings settings);

    /**
     * Sets up a new {@link ScriptEnvironment} in the given load directory.
     *
     * @param loadDirectory the directory
     * @return the new environment
     * @throws UnsupportedOperationException if the controller does not support
     * setting up new environments after construction
     */
    default ScriptEnvironment setupNewEnvironment(Path loadDirectory) {
        return setupNewEnvironment(loadDirectory, EnvironmentSettings.defaults());
    }

    /**
     * Shuts down this script controller
     */
    void shutdown();

    /**
     * Builds a {@link ScriptController}
     */
    interface Builder {

        /**
         * Add a directory to be handled by this script controller
         *
         * @param loadDirectory the directory
         * @return this builder
         */
        Builder withDirectory(Path loadDirectory);

        /**
         * Defines the logger to use.
         *
         * @param logger the logger
         * @return this builder
         */
        Builder logger(SystemLogger logger);

        /**
         * Defines the default {@link EnvironmentSettings} to use when this
         * controller creates new {@link ScriptEnvironment}s.
         *
         * @param settings the default settings
         * @return this builder
         */
        Builder defaultEnvironmentSettings(EnvironmentSettings settings);

        /**
         * Builds a new {@link ScriptController} from the settings defined in
         * this builder
         *
         * @return a new controller
         */
        ScriptController build();

    }

}
