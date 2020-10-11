package com.pepej.papi.environment.script;

import com.pepej.papi.logging.ScriptLogger;
import com.pepej.papi.terminable.composite.CompositeTerminable;

import java.nio.file.Path;
import java.util.Set;

/**
 * Represents an individual script
 */
public interface Script extends AutoCloseable {

    /**
     * Gets the name of the script, usually formed from the scripts
     * {@link #getPath() path} {@link Path#getFileName() file name}.
     *
     * @return the name of the script
     */
    String getName();

    /**
     * Gets the path of the script.
     *
     * <p>The path is relative to the loader directory.</p>
     *
     * @return the path
     */
    Path getPath();

    /**
     * Gets the scripts logger instance
     *
     * @return the logger
     */
    ScriptLogger getLogger();

    /**
     * Gets the scripts composite closable registry.
     *
     * @return the scripts closable registry
     */
    CompositeTerminable getClosables();

    /**
     * Gets the other scripts depended on by this script.
     *
     * @return this scripts dependencies
     */
    Set<Path> getDependencies();

    /**
     * Marks that this script depends on another script.
     *
     * @param path the other script
     */
    void depend(String path);

    /**
     * Marks that this script depends on another script.
     *
     * @param path the other script
     */
    void depend(Path path);

}
