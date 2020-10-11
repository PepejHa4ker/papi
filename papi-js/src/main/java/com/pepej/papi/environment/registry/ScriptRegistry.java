package com.pepej.papi.environment.registry;

import com.pepej.papi.environment.script.Script;

import java.nio.file.Path;
import java.util.Map;

/**
 * A registry of {@link Script}s
 */
public interface ScriptRegistry extends AutoCloseable {

    static ScriptRegistry create() {
        return new ScriptRegistryImpl();
    }

    /**
     * Registers a script
     *
     * @param script the script to register
     */
    void register(Script script);

    /**
     * Unregisters a script
     *
     * @param script the script to unregister
     */
    void unregister(Script script);

    /**
     * Gets a script by path
     *
     * @param path the path
     * @return a script for the path, or null
     */
    Script getScript(Path path);

    /**
     * Gets all scripts known to this registry
     *
     * @return the scripts
     */
    Map<Path, Script> getAll();

    @Override
    void close();
}
