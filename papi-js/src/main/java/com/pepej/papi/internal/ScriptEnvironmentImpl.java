package com.pepej.papi.internal;

import com.pepej.papi.environment.ScriptEnvironment;
import com.pepej.papi.environment.loader.EnvironmentScriptLoader;
import com.pepej.papi.environment.registry.ScriptRegistry;
import com.pepej.papi.exports.ExportRegistry;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptEngine;
import java.io.IOException;
import java.nio.file.Path;

class ScriptEnvironmentImpl implements ScriptEnvironment {

    /** The script controller */
    private final ScriptControllerImpl controller;

    /** The environment settings */
    private final EnvironmentSettingsImpl settings;

    /** The root directory of this environment */
    private final Path directory;

    /** The script registry for scripts loaded in this environment */
    private final ScriptRegistry scriptRegistry;

    /** The script export registry */
    private final ExportRegistry exportRegistry;

    /** The script engine */
    private final ScriptEngine scriptEngine;

    /** The script loader operating within this environment */
    private final EnvironmentScriptLoader loader;

    /** An autoclosable which represents the repeating load task */
    private final AutoCloseable loaderPollingTask;

    public ScriptEnvironmentImpl(ScriptControllerImpl controller, Path directory, EnvironmentSettingsImpl settings) {
        this.controller = controller;
        this.directory = directory;
        this.settings = settings;

        this.scriptRegistry = ScriptRegistry.create();
        this.exportRegistry = ExportRegistry.create();
        this.scriptEngine = new NashornScriptEngineFactory().getScriptEngine(new String[]{"--language=es6"}, ScriptEnvironmentImpl.class.getClassLoader());
        try {
            this.loader = new ScriptLoaderImpl(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.loader.watch(settings.getInitScript());
        this.loader.preload();

        // setup a ticking task on the environments loader
        Duration rate = settings.getPollRate();
        this.loaderPollingTask = settings.getLoadExecutor().scheduleAtFixedRate(this.loader, rate.getDuration(), rate.getUnit());
    }

    @Override
    public ScriptControllerImpl getController() {
        return this.controller;
    }

    @Override
    public EnvironmentSettingsImpl getSettings() {
        return this.settings;
    }

    @Override
    public Path getDirectory() {
        return this.directory;
    }

    @Override
    public EnvironmentScriptLoader getLoader() {
        return this.loader;
    }

    public ScriptEngine getScriptEngine() {
        return this.scriptEngine;
    }

    @Override
    public ScriptRegistry getScriptRegistry() {
        return this.scriptRegistry;
    }

    @Override
    public ExportRegistry getExportRegistry() {
        return this.exportRegistry;
    }

    @Override
    public void close() throws Exception {
        this.loaderPollingTask.close();
        this.loader.close();
        this.scriptRegistry.close();
    }
}
