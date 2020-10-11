package com.pepej.papi.plugin;

import com.pepej.papi.Events;
import com.pepej.papi.terminable.TerminableConsumer;
import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;

public interface PapiPlugin extends Plugin, TerminableConsumer {

    /**
     * Register a listener with the server.
     *
     * <p>{@link Events} should be used instead of this method in most cases.</p>
     *
     * @param listener the listener to register
     * @param <T> the listener class type
     * @return the listener
     */
    @Nonnull
    <T extends Listener> T registerListener(@Nonnull T listener);

    /**
     * Registers a CommandExecutor with the server
     *
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @Nonnull
    default <T extends CommandExecutor> T registerCommand(@Nonnull T command, @Nonnull String... aliases) {
        return registerCommand(command, null, null, null, aliases);
    }

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
    @Nonnull
    <T extends CommandExecutor> T registerCommand(@Nonnull T command, String permission, String permissionMessage, String description, @Nonnull String... aliases);

    /**
     * Gets a service provided by the ServiceManager
     *
     * @param service the service class
     * @param <T> the class type
     * @return the service
     */
    @Nonnull
    <T> T getService(@Nonnull Class<T> service);

    /**
     * Provides a service to the ServiceManager, bound to this plugin
     *
     * @param clazz the service class
     * @param instance the instance
     * @param priority the priority to register the service at
     * @param <T> the service class type
     * @return the instance
     */
    @Nonnull
    <T> T provideService(@Nonnull Class<T> clazz, @Nonnull T instance, @Nonnull ServicePriority priority);

    /**
     * Provides a service to the ServiceManager, bound to this plugin at {@link ServicePriority#Normal}.
     *
     * @param clazz the service class
     * @param instance the instance
     * @param <T> the service class type
     * @return the instance
     */
    @Nonnull
    <T> T provideService(@Nonnull Class<T> clazz, @Nonnull T instance);

    /**
     * Gets if a given plugin is enabled.
     *
     * @param name the name of the plugin
     * @return if the plugin is enabled
     */
    boolean isPluginPresent(@Nonnull String name);

    /**
     * Gets a plugin instance for the given plugin name
     *
     * @param name the name of the plugin
     * @param pluginClass the main plugin class
     * @param <T> the main class type
     * @return the plugin
     */
    @Nullable
    <T> T getPlugin(@Nonnull String name, @Nonnull Class<T> pluginClass);

    /**
     * Gets a bundled file from the plugins resource folder.
     *
     * <p>If the file is not present, a version of it it copied from the jar.</p>
     *
     * @param name the name of the file
     * @return the file
     */
    @Nonnull
    File getBundledFile(@Nonnull String name);

    /**
     * Loads a config file from a file name.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @return the config instance
     */
    @Nonnull
    YamlConfiguration loadConfig(@Nonnull String file);

    /**
     * Loads a config file from a file name.
     *
     * <p>Behaves in the same was as {@link #getBundledFile(String)} when the file is not present.</p>
     *
     * @param file the name of the file
     * @return the config instance
     */
    @Nonnull
    ConfigurationNode loadConfigNode(@Nonnull String file);

    /**
     * Populates a config object.
     *
     * @param file the name of the file
     * @param configObject the config object
     * @param <T> the config object type
     */
    @Nonnull
    <T> T setupConfig(@Nonnull String file, @Nonnull T configObject);

    /**
     * Gets the plugin's class loader
     *
     * @return the class loader
     */
    @Nonnull
    ClassLoader getClassloader();

}
