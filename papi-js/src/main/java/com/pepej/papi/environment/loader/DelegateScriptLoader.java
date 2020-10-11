package com.pepej.papi.environment.loader;

import com.pepej.papi.environment.ScriptEnvironment;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ScriptLoader} which delegates calls to a parent,
 * but keeps track of the files watched via its instance.
 */
public class DelegateScriptLoader implements ScriptLoader {
    private final ScriptLoader parent;
    private final Set<String> paths = new HashSet<>();

    public DelegateScriptLoader(ScriptLoader parent) {
        this.parent = parent;
    }

    @Override
    public ScriptEnvironment getEnvironment() {
        return this.parent.getEnvironment();
    }

    @Override
    public void watchAll(Collection<String> paths) {
        for (String s : paths) {
            if (this.paths.contains(s)) {
                continue;
            }

            this.paths.add(s);
            this.parent.watch(s);
        }
    }

    @Override
    public void unwatchAll(Collection<String> paths) {
        for (String s : paths) {
            if (!this.paths.contains(s)) {
                continue;
            }

            this.paths.remove(s);
            this.parent.unwatch(s);
        }
    }

    @Override
    public void close() {
        this.parent.unwatchAll(this.paths);
        this.paths.clear();
    }
}
