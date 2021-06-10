package com.pepej.papi.plugin;

import com.pepej.papi.events.Events;
import com.pepej.papi.services.ServicePriority;
import com.pepej.papi.terminable.TerminableConsumer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.Optional;

public interface PapiPlugin extends Plugin, TerminableConsumer {

    /**
     * Method that calls when plugin is loading
     */
    default void onPluginLoad() {}

    /**
     * Method that calls when plugin is enabling
     */
    default void onPluginEnable() {}

    /**
     * Method that calls when plugin is disabling
     */
    default void onPluginDisable() {}


    /**
     * Register a listener with the server.
     *
     * <p>{@link Events} should be used instead of this method in most cases.</p>
     *
     * @param listener the listener to register
     * @param <T> the listener class type
     * @return the listener
     */
    @NotNull
    <T extends Listener> T registerListener(@NotNull T listener);

    /**
     * Registers a CommandExecutor with the server
     *
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NotNull
    <T extends CommandExecutor> T registerCommand(@NotNull T command, @NotNull String... aliases);

    /**
     * Registers a CommandExecutor with the server
     *
     * @param command the command instance
     * @param permission the command permission
     * @param permissionMessage the message sent when the sender doesn't the required permission
     * @param description the command description
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NotNull
    <T extends CommandExecutor> T registerCommand(@NotNull T command, String permission, String permissionMessage, String description, @NotNull String... aliases);

    /**
     * Gets a service provided by the ServiceManager
     *
     * @param service the service class
     * @param <T> the class type
     * @return the service
     */
    @NotNull
    <T> T getService(@NotNull Class<T> service);

    /**
     * Provides a service to the ServiceManager,
     *
     * @param <T> the service class type
     * @param service the service class
     * @param instance the instance
     * @param priority the priority to register the service at
     */
    <T> void provideService(@NotNull Class<T> service, @NotNull T instance, @NotNull ServicePriority priority);

    /**
     * Provides a service to the ServiceManager, {@link ServicePriority#NORMAL}.
     *
     * @param <T> the service class type
     * @param service the service class
     * @param instance the instance
     */
    <T> void provideService(@NotNull Class<T> service, @NotNull T instance);

    /**
     * Provides a service to the ServiceManager, {@link ServicePriority#NORMAL}.
     *
     * @param <T> the service class type
     * @param service the service class
     */
    <T> void provideService(@NotNull Class<T> service);

    /**
     * Gets if a given plugin is enabled.
     *
     * @param name the name of the plugin
     * @return if the plugin is enabled
     */
    boolean isPluginPresent(@NotNull String name);

    /**
     * Gets a plugin instance for the given plugin name
     * The plugin can be null
     * @param name the name of the plugin
     * @param pluginClass the main plugin class
     * @param <T> the main class type
     * @return the plugin
     */
    @Nullable
    <T extends Plugin> T getPluginNullable(@NotNull String name, @NotNull Class<T> pluginClass);

    /**
     * Gets a plugin instance for the given plugin name
     * @param name the name of the plugin
     * @param pluginClass the main plugin class
     * @param <T> the main class type
     * @return the plugin
     */
    @NotNull
    <T extends Plugin> Optional<T> getPlugin(@NotNull String name, @NotNull Class<T> pluginClass);

    /**
     * Gets a bundled file from the plugins resource folder.
     *
     * <p>If the file is not present, a version of it it copied from the jar.</p>
     *
     * @param name the name of the file
     * @return the file
     */
    @NotNull
    File getBundledFile(@NotNull String name);

    /**
     * Loads a config file from a file name.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @return the config instance
     */
    @NotNull
    YamlConfiguration loadConfig(@NotNull String file);

    /**
     * Loads a config file from a file name.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @return the config instance
     */
    @NotNull
    ConfigurationNode loadConfigNode(@NotNull String file);

    /**
     * Gets the plugin's class loader
     *
     * @return the class loader
     */
    @NotNull
    ClassLoader getClassloader();

}
