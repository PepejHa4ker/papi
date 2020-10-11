package com.pepej.papi.environment.loader;

/**
 * The environments script loader
 */
public interface EnvironmentScriptLoader extends ScriptLoader, Runnable {

    /**
     * Script loading loop
     */
    @Override
    void run();

    /**
     * Tries to recursively load scripts.
     */
    void preload();

}
