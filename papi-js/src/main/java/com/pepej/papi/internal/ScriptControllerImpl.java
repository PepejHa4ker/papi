package com.pepej.papi.internal;

import com.pepej.papi.ScriptController;
import com.pepej.papi.closable.CompositeAutoClosable;
import com.pepej.papi.environment.settings.EnvironmentSettings;
import com.pepej.papi.environment.ScriptEnvironment;
import com.pepej.papi.logging.SystemLogger;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public final class ScriptControllerImpl implements ScriptController {

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static ScriptController.Builder builder() {
        return new Builder();
    }

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static EnvironmentSettings defaultSettings() {
        return EnvironmentSettingsImpl.defaults();
    }

    @Deprecated
    @SuppressWarnings("DeprecatedIsStillUsed")
    public static EnvironmentSettings.Builder newSettingsBuilder() {
        return EnvironmentSettingsImpl.builder();
    }

    /**
     * The sub environments originating from this controller
     */
    private final Map<Path, ScriptEnvironment> environments = new HashMap<>();

    // various settings and properties defined when the controller was created.
    private final SystemLogger logger;
    private final EnvironmentSettings defaultSettings;

    private ScriptControllerImpl(Builder builder) {
        this.logger = builder.logger.get();
        this.defaultSettings = builder.settings;

        // setup the initial environments
        for (Path path : builder.directories) {
            setupNewEnvironment(path);
        }
    }

    @Override
    public void shutdown() {
        CompositeAutoClosable.create()
                             .bindAll(this.environments.values())
                             .closeAndReportExceptions();
    }


    @Override
    public Collection<ScriptEnvironment> getEnvironments() {
        return Collections.unmodifiableCollection(this.environments.values());
    }

    @Override
    public synchronized ScriptEnvironment setupNewEnvironment(Path loadDirectory, EnvironmentSettings settings) {
        if (this.environments.containsKey(loadDirectory)) {
            throw new IllegalStateException("Already an environment setup at path " + loadDirectory.toString());
        }

        // merge the provided setting with out defaults
        EnvironmentSettings mergedSettings = this.defaultSettings.toBuilder().mergeSettingsFrom(settings).build();

        // create a new environment
        ScriptEnvironmentImpl environment = new ScriptEnvironmentImpl(this, loadDirectory, (EnvironmentSettingsImpl) mergedSettings);
        // store a ref to the new environment in the controller
        this.environments.put(loadDirectory, environment);
        return environment;
    }


    SystemLogger getLogger() {
        return this.logger;
    }

    private static final class Builder implements ScriptController.Builder {
        private final Set<Path> directories = new HashSet<>();
        private Supplier<SystemLogger> logger = FallbackSystemLogger.INSTANCE;
        private EnvironmentSettings settings = EnvironmentSettings.defaults();

        @Override
        public Builder withDirectory(Path loadDirectory) {
            this.directories.add(Objects.requireNonNull(loadDirectory, "loadDirectory"));
            return this;
        }

        @Override
        public Builder logger(SystemLogger logger) {
            Objects.requireNonNull(logger, "logger");
            this.logger = () -> logger;
            return this;
        }

        @Override
        public Builder defaultEnvironmentSettings(EnvironmentSettings settings) {
            this.settings = Objects.requireNonNull(settings, "settings");
            return this;
        }

        @Override
        public ScriptController build() {
            return new ScriptControllerImpl(this);
        }
    }

}
