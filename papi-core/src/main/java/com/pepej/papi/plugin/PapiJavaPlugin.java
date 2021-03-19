package com.pepej.papi.plugin;

import com.pepej.papi.config.ConfigFactory;
import com.pepej.papi.events.Events;
import com.pepej.papi.events.player.AsyncPlayerFirstJoinEvent;
import com.pepej.papi.events.server.ServerUpdateEvent;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.internal.PapiImplementationPlugin;
import com.pepej.papi.dependency.DependencyLoader;
import com.pepej.papi.scheduler.PapiExecutors;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.services.ServicePriority;
import com.pepej.papi.services.Services;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.CommandMapUtil;
import com.pepej.papi.utils.Log;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public abstract class PapiJavaPlugin extends JavaPlugin implements PapiPlugin {

    // the backing terminable registry
    private CompositeTerminable terminableRegistry;

    @Override
    public final @NonNull ClassLoader getClassloader() {
        return super.getClassLoader();
    }

    // are we the plugin that's providing papi?
    private boolean isLoaderPlugin;

    // Used by subclasses to perform logic for plugin onPluginLoad/onPluginEnable/onPluginDisable.
    @Override
    public final void onLoad() {
        // LoaderUtils.getPlugin() has the side effect of caching the loader ref
        // do that nice and early. also store whether 'this' plugin is the loader.ану п
        final PapiPlugin loaderPlugin = LoaderUtils.getPlugin();
        this.isLoaderPlugin = this == loaderPlugin;

        this.terminableRegistry = CompositeTerminable.create();

        DependencyLoader.loadAll(getClass());

        // call subclass
        onPluginLoad();
    }

    @Override
    public final void onEnable() {
        if (isLoaderPlugin) {
            handleInternalTasks();
        }
        // call subclass
        onPluginEnable();
    }

    private void handleInternalTasks() {
        LoaderUtils.getPapiBasedPlugins()
                   .stream()
                   .filter(plugin -> !LoaderUtils.getPapiImplementationPlugins().contains(plugin))
                   .forEach(plugin -> Log.info("Loaded papi based plugin &d%s&a successfully", plugin.getName()));
        LoaderUtils.getPapiImplementationPlugins()
                   .forEach(module -> Log.info("Loaded papi implementation module &d%s&a successfully",
                           module.getClass().getDeclaredAnnotation(PapiImplementationPlugin.class).moduleName()));


        Events.subscribe(PlayerJoinEvent.class)
              .filterNot(e -> e.getPlayer().hasPlayedBefore())
              .handler(event -> Events.callAsync(AsyncPlayerFirstJoinEvent.of(event.getPlayer(), System.currentTimeMillis())))
              .bindWith(terminableRegistry);

        Arrays.stream(ServerUpdateEvent.Type.values()).forEach(value -> Schedulers.builder()
                                                                                  .async()
                                                                                  .every(value.getDelayTicks())
                                                                                  .run(() -> Events.call(ServerUpdateEvent.of(value)))
                                                                                  .bindWith(terminableRegistry));
        Schedulers.builder()
                  .async()
                  .after(10, TimeUnit.SECONDS)
                  .every(30, TimeUnit.SECONDS)
                  .run(terminableRegistry::cleanup)
                  .bindWith(terminableRegistry);

        // setup services
        if (isLoaderPlugin) {
            PapiServices.setup(this);
        }
    }

    @Override
    public final void onDisable() {

        // call subclass
        onPluginDisable();

        // terminate the registry
        terminableRegistry.closeAndReportException();

        if (isLoaderPlugin) {
            // shutdown the scheduler
            PapiExecutors.shutdown();
        }
    }

    @NonNull
    @Override
    public <T extends AutoCloseable> T bind(@NonNull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @NonNull
    @Override
    public <T extends TerminableModule> T bindModule(@NonNull T module) {
        return terminableRegistry.bindModule(module);
    }

    @NonNull
    @Override
    public final <T extends Listener> T registerListener(@NonNull T listener) {
        Objects.requireNonNull(listener, "listener");
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    public final <T extends CommandExecutor> T registerCommand(@NonNull T command, @NonNull String... aliases) {
        return registerCommand(command, null, null, null, aliases);
    }

    @NonNull
    @Override
    public final <T extends CommandExecutor> T registerCommand(@NonNull T command, String permission, String permissionMessage, String description, @NonNull String... aliases) {
        return CommandMapUtil.registerCommand(this, command, permission, permissionMessage, description, aliases);
    }

    @NonNull
    @Override
    public final <T> T getService(@NonNull Class<T> service) {
        return Services.load(service);
    }

    @Override
    public final <T> void provideService(@NonNull Class<T> clazz, @NonNull T instance, @NonNull ServicePriority priority) {
        Services.provide(clazz, instance, priority);
    }

    @Override
    public final <T> void provideService(@NonNull Class<T> clazz, @NonNull T instance) {
        provideService(clazz, instance, ServicePriority.NORMAL);
    }

    @Override
    public <T> void provideService(@NonNull Class<T> service) {
        Services.provide(service);
    }

    @Override
    public final boolean isPluginPresent(@NonNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T extends Plugin> T getPluginNullable(@NonNull String name, @NonNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public final <T extends Plugin> Optional<T> getPlugin(@NonNull String name, @NonNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return Optional.of((T) getServer().getPluginManager().getPlugin(name));
    }


    private File getRelativeFile(@NonNull String name) {
        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    @NonNull
    @Override
    public final File getBundledFile(@NonNull String name) {
        Objects.requireNonNull(name, "name");
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    @NonNull
    @Override
    public final YamlConfiguration loadConfig(@NonNull String file) {
        Objects.requireNonNull(file, "file");
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

    @NonNull
    @Override
    public final ConfigurationNode loadConfigNode(@NonNull String file) {
        Objects.requireNonNull(file, "file");
        return ConfigFactory.gson().load(getBundledFile(file));
    }



}