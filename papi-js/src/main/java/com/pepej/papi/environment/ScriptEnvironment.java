package com.pepej.papi.environment;

import com.pepej.papi.ScriptController;
import com.pepej.papi.environment.loader.EnvironmentScriptLoader;
import com.pepej.papi.environment.registry.ScriptRegistry;
import com.pepej.papi.environment.settings.EnvironmentSettings;
import com.pepej.papi.exports.ExportRegistry;

import java.nio.file.Path;

/**
 * Represents an isolated environment in which scripts run
 *
 * Each environment operates within a given root {@link Path directory}, under a
 * {@link ScriptController}.
 */
public interface ScriptEnvironment extends AutoCloseable {

    /**
     * Gets the script controller which created this environment
     *
     * @return the parent controller
     */
    ScriptController getController();

    /**
     * Gets the environment settings
     *
     * @return the settings
     */
    EnvironmentSettings getSettings();

    /**
     * Gets the environments root scripts directory
     *
     * @return the root directory of this environment
     */
    Path getDirectory();

    /**
     * Gets the script loader used by this environment.
     *
     * <p>Each environment has it's own loader.</p>
     *
     * @return the script loader
     */
    EnvironmentScriptLoader getLoader();

    /**
     * Gets the script registry, containing all loaded scripts within this
     * environment
     *
     * @return the script registry
     */
    ScriptRegistry getScriptRegistry();

    /**
     * Gets the export registry for this environment
     *
     * @return the export registry
     */
    ExportRegistry getExportRegistry();

}
