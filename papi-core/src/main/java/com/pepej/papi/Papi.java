package com.pepej.papi;

import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.plugin.PapiPlugin;
import com.pepej.papi.services.Services;
import com.pepej.papi.services.ServicesManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Optional;

/**
 * Base class for papi, which mainly just proxies calls to {@link Bukkit#getServer()} for convenience.
 */
public final class Papi {


    /**
     * Gets the plugin which is "hosting" papi.
     *
     * @return the host plugin
     */
    public static PapiPlugin hostPlugin() {
        return LoaderUtils.getPlugin();
    }

    public static Server server() {
        return Bukkit.getServer();
    }

    public static ConsoleCommandSender console() {
        return server().getConsoleSender();
    }

    public static PluginManager plugins() {
        return server().getPluginManager();
    }

    public static ServicesManager services() {
        return ServicesManager.obtain();
    }

    public static BukkitScheduler bukkitScheduler() {
        return server().getScheduler();
    }

    @Nullable
    public static <T> T serviceNullable(Class<T> clazz) {
        return Services.get(clazz).orElse(null);
    }

    public static <T> Optional<T> service(Class<T> clazz) {
        return Services.get(clazz);
    }

    public static void executeCommand(String command) {
        server().dispatchCommand(console(), command);
    }

    @Nullable
    public static World worldNullable(String name) {
        return server().getWorld(name);
    }

    public static Optional<World> world(String name) {
        return Optional.ofNullable(worldNullable(name));
    }

    private Papi() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}