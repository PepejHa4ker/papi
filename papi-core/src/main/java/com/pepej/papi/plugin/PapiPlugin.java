package com.pepej.papi.plugin;

import com.pepej.papi.events.Events;
import com.pepej.papi.terminable.TerminableConsumer;
import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    @NonNull
    <T extends Listener> T registerListener(@NonNull T listener);

    /**
     * Registers a CommandExecutor with the server
     *
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NonNull
    <T extends CommandExecutor> T registerCommand(@NonNull T command, @NonNull String... aliases);

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
    @NonNull
    <T extends CommandExecutor> T registerCommand(@NonNull T command, String permission, String permissionMessage, String description, @NonNull String... aliases);

    /**
     * Gets a service provided by the ServiceManager
     *
     * @param service the service class
     * @param <T> the class type
     * @return the service
     */
    @NonNull
    <T> T getService(@NonNull Class<T> service);

    /**
     * Provides a service to the ServiceManager, bound to this plugin
     *
     * @param <T> the service class type
     * @param clazz the service class
     * @param instance the instance
     * @param priority the priority to register the service at
     */
    <T> void provideService(@NonNull Class<T> clazz, @NonNull T instance, @NonNull ServicePriority priority);

    /**
     * Provides a service to the ServiceManager, bound to this plugin at {@link ServicePriority#Normal}.
     *
     * @param <T> the service class type
     * @param clazz the service class
     * @param instance the instance
     */
    <T> void provideService(@NonNull Class<T> clazz, @NonNull T instance);

    /**
     * Gets if a given plugin is enabled.
     *
     * @param name the name of the plugin
     * @return if the plugin is enabled
     */
    boolean isPluginPresent(@NonNull String name);

    /**
     * Gets a plugin instance for the given plugin name
     * The plugin can be null
     * @param name the name of the plugin
     * @param pluginClass the main plugin class
     * @param <T> the main class type
     * @return the plugin
     */
    @Nullable
    <T extends Plugin> T getPluginNullable(@NonNull String name, @NonNull Class<T> pluginClass);

    /**
     * Gets a plugin instance for the given plugin name
     * @param name the name of the plugin
     * @param pluginClass the main plugin class
     * @param <T> the main class type
     * @return the plugin
     */
    @NonNull
    <T extends Plugin> Optional<T> getPlugin(@NonNull String name, @NonNull Class<T> pluginClass);

    /**
     * Gets a bundled file from the plugins resource folder.
     *
     * <p>If the file is not present, a version of it it copied from the jar.</p>
     *
     * @param name the name of the file
     * @return the file
     */
    @NonNull
    File getBundledFile(@NonNull String name);

    /**
     * Loads a config file from a file name.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @return the config instance
     */
    @NonNull
    YamlConfiguration loadConfig(@NonNull String file);

    /**
     * Loads a config file from a file name.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @return the config instance
     */
    @NonNull
    ConfigurationNode loadConfigNode(@NonNull String file);

    /**
     * Populates a config object.
     *
     * @param file the name of the file
     * @param configObject the config object
     * @param <T> the config object type
     * @return configObject
     */
    @NonNull
    <T> T setupConfig(@NonNull String file, @NonNull T configObject);

    /**
     * Gets the plugin's class loader
     *
     * @return the class loader
     */
    @NonNull
    ClassLoader getClassloader();

}
