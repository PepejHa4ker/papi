package com.pepej.papi.maven;

import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.shadow.ClassTarget;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.utils.Log;
import lombok.*;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;


/**
 * Resolves {@link MavenLibrary} annotations for a class, and loads the dependency
 * into the classloader.
 */
public final class LibraryLoader {

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
            if (!lib.value().isEmpty()) {
                String[] strings = lib.value().split(":");
                load(strings[0], strings[1], strings[2], lib.repo().url());
            } else {
                load(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().url());
            }
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
            throw new RuntimeException("Unable to download dependency: " + d.toString());
        }

        URLClassLoader classLoader = (URLClassLoader) LoaderUtils.getPlugin().getClass().getClassLoader();
        final URLClassLoaderShadow shadow = ShadowFactory.global().shadow(URLClassLoaderShadow.class, classLoader);

        try {
            shadow.addURL(saveLocation.toURI().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Unable to load dependency: " + saveLocation.toString(), e);
        }

        Log.info("Loaded dependency &d'%s'&a successfully.", name);
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
    private static class Dependency {
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

    @ClassTarget(URLClassLoader.class)
    private interface URLClassLoaderShadow extends Shadow {

        void addURL(URL url);

    }

}