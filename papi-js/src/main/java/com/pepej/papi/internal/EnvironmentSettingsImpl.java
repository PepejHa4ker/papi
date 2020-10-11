package com.pepej.papi.internal;


import com.pepej.papi.environment.settings.EnvironmentSettings;
import com.pepej.papi.bindings.BindingsSupplier;
import com.pepej.papi.environment.loader.ScriptLoadingExecutor;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

class EnvironmentSettingsImpl implements EnvironmentSettings {
    private static final Supplier<ScriptLoadingExecutor> DEFAULT_LOAD_EXECUTOR = () -> ScriptLoadingExecutor.usingJavaScheduler(Executors.newSingleThreadScheduledExecutor());
    private static final Executor DEFAULT_RUN_EXECUTOR = Runnable::run;
    private static final Duration DEFAULT_POLL_RATE = new Duration(1, TimeUnit.SECONDS);
    private static final String DEFAULT_INIT_SCRIPT = "init.js";

    private static final EnvironmentSettings DEFAULT = builder().build();

    static EnvironmentSettings defaults() {
        return DEFAULT;
    }

    static EnvironmentSettings.Builder builder() {
        return new Builder();
    }

    private final ScriptLoadingExecutor loadExecutor;
    private final Executor runExecutor;
    private final Set<BindingsSupplier> bindings;
    private final Set<String> packageImports;
    private final Set<String> typeImports;
    private final Duration pollRate;
    private final String initScript;

    private EnvironmentSettingsImpl(Builder builder) {
        this.pollRate = builder.pollRate;
        this.loadExecutor = builder.loadExecutor;
        this.runExecutor = builder.runExecutor;
        this.bindings = Collections.unmodifiableSet(new HashSet<>(builder.bindings));
        this.packageImports = Collections.unmodifiableSet(new LinkedHashSet<>(builder.packageImports));
        this.typeImports = Collections.unmodifiableSet(new LinkedHashSet<>(builder.typeImports));
        this.initScript = builder.initScript;
    }

    public ScriptLoadingExecutor getLoadExecutor() {
        if (this.loadExecutor == null) {
            return DEFAULT_LOAD_EXECUTOR.get();
        }
        return this.loadExecutor;
    }

    public Executor getRunExecutor() {
        if (this.runExecutor == null) {
            return DEFAULT_RUN_EXECUTOR;
        }
        return this.runExecutor;
    }

    public Set<BindingsSupplier> getBindings() {
        return this.bindings;
    }

    public Set<String> getPackageImports() {
        return this.packageImports;
    }

    public Set<String> getTypeImports() {
        return this.typeImports;
    }

    public Duration getPollRate() {
        if (this.pollRate == null) {
            return DEFAULT_POLL_RATE;
        }
        return this.pollRate;
    }

    public String getInitScript() {
        if (this.initScript == null) {
            return DEFAULT_INIT_SCRIPT;
        }
        return this.initScript;
    }

    private static final class Builder implements EnvironmentSettings.Builder {
        private ScriptLoadingExecutor loadExecutor = null;
        private Executor runExecutor = null;
        private final Set<BindingsSupplier> bindings = new HashSet<>();
        private final Set<String> packageImports = new LinkedHashSet<>();
        private final Set<String> typeImports = new LinkedHashSet<>();
        private Duration pollRate = null;
        private String initScript = null;

        @Override
        public Builder mergeSettingsFrom(EnvironmentSettings other) {
            Objects.requireNonNull(other, "other");
            EnvironmentSettingsImpl that = (EnvironmentSettingsImpl) other;

            if (that.loadExecutor != null) {
                this.loadExecutor = that.loadExecutor;
            }
            if (that.runExecutor != null) {
                this.runExecutor = that.runExecutor;
            }
            this.bindings.addAll(that.bindings);
            this.packageImports.addAll(that.packageImports);
            this.typeImports.addAll(that.typeImports);
            if (that.pollRate != null) {
                this.pollRate = that.pollRate;
            }
            return this;
        }

        @Override
        public Builder loadExecutor(ScriptLoadingExecutor executor) {
            Objects.requireNonNull(executor, "executor");
            this.loadExecutor = executor;
            return this;
        }

        @Override
        public Builder runExecutor(Executor executor) {
            this.runExecutor = Objects.requireNonNull(executor, "executor");
            return this;
        }

        @Override
        public Builder withBindings(BindingsSupplier supplier) {
            this.bindings.add(Objects.requireNonNull(supplier, "supplier"));
            return this;
        }

        @Override
        public Builder withDefaultPackageImport(String packageName) {
            this.packageImports.add(packageName);
            return this;
        }

        @Override
        public EnvironmentSettings.Builder withDefaultPackageImports(Collection<String> packageNames) {
            this.packageImports.addAll(packageNames);
            return this;
        }

        @Override
        public Builder withDefaultTypeImport(String type) {
            this.typeImports.add(type);
            return this;
        }

        @Override
        public EnvironmentSettings.Builder withDefaultTypeImports(Collection<String> types) {
            this.typeImports.addAll(types);
            return this;
        }

        @Override
        public Builder pollRate(long time, TimeUnit unit) {
            this.pollRate = new Duration(time, Objects.requireNonNull(unit, "unit"));
            return this;
        }

        @Override
        public EnvironmentSettings.Builder initScript(String path) {
            this.initScript = Objects.requireNonNull(path, "path");
            return this;
        }

        @Override
        public EnvironmentSettings build() {
            return new EnvironmentSettingsImpl(this);
        }
    }
}
