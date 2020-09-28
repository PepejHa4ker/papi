package com.pepej.papi.internal;

import com.pepej.papi.Papi;
import com.pepej.papi.plugin.PapiPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides the instance which loaded the helper classes into the server
 */
public final class LoaderUtils {
    private static PapiPlugin plugin = null;
    private static Thread mainThread = null;

    @Nonnull
    public static synchronized PapiPlugin getPlugin() {
        if (plugin == null) {
            JavaPlugin pl = JavaPlugin.getProvidingPlugin(LoaderUtils.class);
            if (!(pl instanceof PapiPlugin)) {
                throw new IllegalStateException("papi providing plugin does not implement HelperPlugin: " + pl.getClass().getName());
            }
            plugin = (PapiPlugin) pl;

            String pkg = LoaderUtils.class.getPackage().getName();
            pkg = pkg.substring(0, pkg.length() - ".internal".length());

            Bukkit.getLogger().info("[Papi] papi (" + pkg + ") bound to plugin " + plugin.getName() + " - " + plugin.getClass().getName());

            setup();
        }

        return plugin;
    }

    public static Set<Plugin> getPapiImplementationPlugins() {
        return Stream.concat(
                Stream.<Plugin>of(getPlugin()),
                Arrays.stream(Papi.plugins().getPlugins())
                      .filter(pl -> pl.getClass().isAnnotationPresent(PapiImplementationPlugin.class))
        ).collect(Collectors.toSet());
    }

    public static Set<PapiPlugin> getPapiPlugins() {
        return Stream.concat(
                Stream.of(getPlugin()),
                Arrays.stream(Papi.plugins().getPlugins())
                      .filter(pl -> pl instanceof PapiPlugin)
                      .map(pl -> (PapiPlugin) pl)
        ).collect(Collectors.toSet());
    }

    @Nonnull
    public static synchronized Thread getMainThread() {
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
