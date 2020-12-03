package com.pepej.papi.internal;

import com.pepej.papi.Papi;
import com.pepej.papi.plugin.PapiPlugin;
import lombok.Synchronized;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

/**
 * Provides the instance which loaded the helper classes into the server
 */
public final class LoaderUtils {
    private static PapiPlugin plugin = null;
    private static Thread mainThread = null;

    @NonNull
    @Synchronized
    public static PapiPlugin getPlugin() {
        if (plugin == null) {
            JavaPlugin pl = JavaPlugin.getProvidingPlugin(LoaderUtils.class);
            if (!(pl instanceof PapiPlugin)) {
                throw new IllegalStateException("papi providing plugin does not implement PapiPlugin: " + pl.getClass().getName());
            }
            plugin = (PapiPlugin) pl;
            String pkg = LoaderUtils.class.getPackage().getName();
            pkg = pkg.substring(0, pkg.length() - 9);

            Bukkit.getLogger().info("[Papi] papi (" + pkg + ") bound to plugin " + plugin.getName() + " - " + plugin.getClass().getName());

            setup();
        }

        return plugin;
    }

    public static Set<Plugin> getPapiImplementationPlugins() {
        return Stream.concat(
                Stream.of(getPlugin()),
                Arrays.stream(Papi.plugins().getPlugins())
                      .filter(pl -> pl.getClass().isAnnotationPresent(PapiImplementationPlugin.class)))
                     .collect(toSet());
    }

    public static Set<PapiPlugin> getPapiPlugins() {
        return Stream.concat(
                Stream.of(getPlugin()),
                Arrays.stream(Papi.plugins().getPlugins())
                      .filter(pl -> pl instanceof PapiPlugin)
                      .map(pl -> (PapiPlugin) pl)
        ).collect(toSet());
    }

    @NonNull
    @Synchronized
    public static Thread getMainThread() {
        if (mainThread == null) {
            if (Bukkit.getServer().isPrimaryThread()) {
                mainThread = Thread.currentThread();
            }
        }
        return mainThread;
    }

    // performs an intial setup for global handlers
    private static void setup() {

        // cache main thread in this class
        getMainThread();
    }

    private LoaderUtils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
