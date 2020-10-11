package com.pepej.papi.environment.loader;

import com.pepej.papi.environment.ScriptEnvironment;

import java.util.Arrays;
import java.util.Collection;

/**
 * An object capable of loadings scripts and monitoring them for updates.
 */
public interface ScriptLoader extends AutoCloseable {

    /**
     * Gets the script environment this loader is operating in
     *
     * @return the parent environment
     */
    ScriptEnvironment getEnvironment();

    /**
     * Loads and watches a script
     *
     * @param paths the path to watch
     */
    default void watch(String... paths) {
        this.watchAll(Arrays.asList(paths));
    }

    /**
     * Loads and watches a collection of scripts
     *
     * @param paths the paths to watch
     */
    void watchAll(Collection<String> paths);

    /**
     * Unloads a script
     *
     * @param paths the path to unwatch
     */
    default void unwatch(String... paths) {
        this.unwatchAll(Arrays.asList(paths));
    }

    /**
     * Unloads a collection of scripts
     *
     * @param paths the paths to unwatch
     */
    void unwatchAll(Collection<String> paths);

    @Override
    void close() throws Exception;
}
