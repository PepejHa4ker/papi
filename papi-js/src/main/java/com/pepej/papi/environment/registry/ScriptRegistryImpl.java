package com.pepej.papi.environment.registry;

import com.pepej.papi.closable.CompositeAutoClosable;
import com.pepej.papi.environment.script.Script;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class ScriptRegistryImpl implements ScriptRegistry {
    private final Map<Path, Script> scripts = new HashMap<>();

    @Override
    public void register(Script script) {
        this.scripts.put(script.getPath(), script);
    }

    @Override
    public void unregister(Script script) {
        this.scripts.remove(script.getPath());
    }

    @Override
    public Script getScript(Path path) {
        return this.scripts.get(path);
    }

    @Override
    public Map<Path, Script> getAll() {
        return Collections.unmodifiableMap(this.scripts);
    }

    @Override
    public void close() {
        CompositeAutoClosable.create()
                             .bindAll(this.scripts.values())
                             .closeAndReportExceptions();
    }

}
