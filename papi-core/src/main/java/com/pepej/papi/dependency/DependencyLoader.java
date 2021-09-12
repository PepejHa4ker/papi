package com.pepej.papi.dependency;

import com.google.common.base.Suppliers;
import com.pepej.papi.dependency.loader.PapiURLClassLoader;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.shadow.ClassTarget;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.Target;
import com.pepej.papi.utils.Log;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.function.Supplier;


/**
 * Resolves {@link Dependency} annotations for a class, and loads the dependency
 * into the classloader.
 */
public final class DependencyLoader {

    private static final Supplier<PapiURLClassLoader> URL_INJECTOR = Suppliers.memoize(() -> PapiURLClassLoader.create((URLClassLoader) LoaderUtils.getPlugin().getClass().getClassLoader()));

    /**
     * Resolves all {@link Dependency} annotations on the given object.
     *
     * @param object the object to load libraries for.
     */
    public static void loadAll(Object object) {
        loadAll(object.getClass());
    }

    /**
     * Resolves all {@link Dependency} annotations
     */
    public static void loadAll() {
        for (Class<?> annotatedClass : LoaderUtils.getPlugin().getScanner().getTypesAnnotatedWith(Dependency.class)) {
            loadAll(annotatedClass);
        }
    }

    /**
     * Resolves all {@link Dependency} annotations on the given class.
     *
     * @param clazz the class to load libraries for.
     */
    public static void loadAll(Class<?> clazz) {
        Dependency[] libs = clazz.getDeclaredAnnotationsByType(Dependency.class);

        for (Dependency lib : libs) {
            if (!lib.value().isEmpty()) {
                String[] strings = lib.value().split(":");
                load(strings[0], strings[1], strings[2], lib.repo().url());
            } else {
                load(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().url());
            }
        }
    }

    public static void load(String groupId, String artifactId, String version) {
        load(groupId, artifactId, version, Repository.DEFAULT_MAVEN_REPOSITORY);
    }

    public static void load(String groupId, String artifactId, String version, String repoUrl) {
        load(new DependencyValue(groupId, artifactId, version, repoUrl));
    }

    @SneakyThrows
    public static void load(DependencyValue d) {
        Log.info("Loading dependency &d'%s:%s:%s'&a from&d %s", d.getGroupId(), d.getArtifactId(), d.getVersion(), d.getRepoUrl());
        String name = d.getArtifactId() + "-" + d.getVersion();

        File saveLocation = new File(getLibFolder(), name + ".jar");
        if (!saveLocation.exists()) {
            Log.info("Dependency &d'%s'&a is not already in the libraries folder. Attempting to download...", name);
            URL url = d.getUrl();
            @Cleanup InputStream is = url.openStream();
            Files.copy(is, saveLocation.toPath());
            Log.info("Dependency &d'%s'&a successfully downloaded.", name);
        }

        if (!saveLocation.exists()) {
            Log.severe("Unable to download dependency: %s", d);
            return;
        }


        try {
            URL_INJECTOR.get().addURL(saveLocation.toURI().toURL());
        } catch (Exception e) {
            Log.severe("Something went wrong while loading dependency %s", saveLocation.toURI().toURL());
            return;
        }
        Log.info("Dependency &d'%s'&a successfully loaded.", name);
    }

    private static File getLibFolder() {
        File pluginDataFolder = LoaderUtils.getPlugin().getDataFolder();
        File libs = new File(pluginDataFolder, "libraries");
        libs.mkdirs();
        return libs;
    }

    @EqualsAndHashCode
    @ToString
    @Value
    private static class DependencyValue {
        String groupId;
        String artifactId;
        String version;
        String repoUrl;

        public URL getUrl() throws MalformedURLException {
            String repo = this.repoUrl;
            if (!repo.endsWith("/")) {
                repo += "/";
            }
            repo += "%s/%s/%s/%s-%s.jar";

            String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
            return new URL(url);
        }

    }

}