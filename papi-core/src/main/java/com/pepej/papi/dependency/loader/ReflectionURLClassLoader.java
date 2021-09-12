package com.pepej.papi.dependency.loader;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

class ReflectionURLClassLoader implements PapiURLClassLoader {

    private static final Method ADD_URL_METHOD;
    private final ClassLoader classLoader;

    static {
        Method addUrlMethod;
        try {
            addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addUrlMethod.setAccessible(true);
        } catch (Exception e) {
            addUrlMethod = null;
        }
        ADD_URL_METHOD = addUrlMethod;
    }

    static boolean isSupported() {
        return ADD_URL_METHOD != null;
    }

    ReflectionURLClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public @NotNull ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public void addURL(@NotNull URL url) {
        try {
            ADD_URL_METHOD.invoke(getClassLoader(), url);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }


}
