package com.pepej.papi.maven;

import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.utils.Log;
import lombok.*;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;


/**
 * Resolves {@link MavenLibrary} annotations for a class, and loads the dependency
 * into the classloader.
 */
public final class LibraryLoader {
    private static final Method ADD_URL_METHOD;

    static {
        try {
            ADD_URL_METHOD = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            ADD_URL_METHOD.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolves all {@link MavenLibrary} annotations on the given object.
     *
     * @param object the object to load libraries for.
     */
    public static void loadAll(Object object) {
        loadAll(object.getClass());
    }

    /**
     * Resolves all {@link MavenLibrary} annotations on the given class.
     *
     * @param clazz the class to load libraries for.
     */
    public static void loadAll(Class<?> clazz) {
        MavenLibrary[] libs = clazz.getDeclaredAnnotationsByType(MavenLibrary.class);

        for (MavenLibrary lib : libs) {
            load(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().url());
        }
    }

    public static void load(String groupId, String artifactId, String version) {
        load(groupId, artifactId, version, "https://repo1.maven.org/maven2");
    }

    public static void load(String groupId, String artifactId, String version, String repoUrl) {
        load(new Dependency(groupId, artifactId, version, repoUrl));
    }

    @SneakyThrows
    public static void load(Dependency d) {
        Log.info(String.format("Loading dependency %s:%s:%s from %s", d.getGroupId(), d.getArtifactId(), d.getVersion(), d.getRepoUrl()));
        String name = d.getArtifactId() + "-" + d.getVersion();

        File saveLocation = new File(getLibFolder(), name + ".jar");
        if (!saveLocation.exists()) {
            Log.info("Dependency '" + name + "' is not already in the libraries folder. Attempting to download...");
            URL url = d.getUrl();
            @Cleanup InputStream is = url.openStream();
            Files.copy(is, saveLocation.toPath());
            Log.info("Dependency '" + name + "' successfully downloaded.");
        }

        if (!saveLocation.exists()) {
            throw new RuntimeException("Unable to download dependency: " + d.toString());
        }

        URLClassLoader classLoader = (URLClassLoader) LoaderUtils.getPlugin().getClass().getClassLoader();
        try {
            ADD_URL_METHOD.invoke(classLoader, saveLocation.toURI().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dependency: " + saveLocation.toString(), e);
        }

        Log.info("Loaded dependency '" + name + "' successfully.");
    }

    private static File getLibFolder() {
        File pluginDataFolder = LoaderUtils.getPlugin().getDataFolder();
        File pluginsDir = pluginDataFolder.getParentFile();

        File papiDir = new File(pluginsDir, "papi");
        File libs = new File(papiDir, "libraries");
        libs.mkdirs();
        return libs;
    }

    @EqualsAndHashCode
    @ToString
    @Value
    public static class Dependency {
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