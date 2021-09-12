package com.pepej.papi.dependency.loader;

import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.net.URLClassLoader;

public interface PapiURLClassLoader {


    static PapiURLClassLoader create(URLClassLoader classLoader) {
        if (ReflectionURLClassLoader.isSupported()) {
            return new ReflectionURLClassLoader(classLoader);
        } else if (UnsafeURLClassLoader.isSupported()) {
            return new UnsafeURLClassLoader(classLoader);
        } else {
            return Noop.INSTANCE;
        }

    }

    @NotNull
    ClassLoader getClassLoader();

    void addURL(@NotNull URL url) throws Exception;
}


class Noop implements PapiURLClassLoader {
    static final Noop INSTANCE = new Noop();

    private Noop() {

    }

    @Override
    public @NotNull ClassLoader getClassLoader() {
        return null;
    }

    @Override
    public void addURL(@NotNull URL url) {
        throw new UnsupportedOperationException();
    }


}
