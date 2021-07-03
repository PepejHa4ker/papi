package com.pepej.papi.plugin;

import com.pepej.papi.config.ConfigFactory;
import com.pepej.papi.dependency.DependencyLoader;
import com.pepej.papi.events.Events;
import com.pepej.papi.events.player.AsyncPlayerFirstJoinEvent;
import com.pepej.papi.events.server.ServerUpdateEvent;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.internal.PapiImplementationPlugin;
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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
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
    public final @NotNull ClassLoader getClassloader() {
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
        LoaderUtils.getPapiImplementationPlugins()
                   .forEach(module -> Log.info("Loaded papi implementation module &d%s&a successfully",
                           module.getClass().getDeclaredAnnotation(PapiImplementationPlugin.class).moduleName()));
        LoaderUtils.getPapiBasedPlugins()
                   .stream()
                   .filter(plugin -> !LoaderUtils.getPapiImplementationPlugins().contains(plugin))
                   .forEach(plugin -> Log.info("Loaded papi based plugin &d%s&a successfully", plugin.getName()));


        Events.subscribe(PlayerJoinEvent.class)
              .filterNot(e -> e.getPlayer().hasPlayedBefore())
              .handler(event -> Events.callAsync(AsyncPlayerFirstJoinEvent.of(event.getPlayer(), System.currentTimeMillis())))
              .bindWith(terminableRegistry);

        Arrays.stream(ServerUpdateEvent.Type.values()).forEach(value -> Schedulers.builder()
                                                                                  .sync()
                                                                                  .every(value.getDelayTicks())
                                                                                  .run(() -> Events.callSync(ServerUpdateEvent.of(value)))
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

    @NotNull
    @Override
    public <T extends AutoCloseable> T bind(@NotNull T terminable) {
        return terminableRegistry.bind(terminable);
    }

    @NotNull
    @Override
    public <T extends TerminableModule> T bindModule(@NotNull T module) {
        return terminableRegistry.bindModule(module);
    }

    @NotNull
    @Override
    public final <T extends Listener> T registerListener(@NotNull T listener) {
        Objects.requireNonNull(listener, "listener");
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    public final <T extends CommandExecutor> @NotNull T registerCommand(@NotNull T command, @NotNull String... aliases) {
        return registerCommand(command, null, null, null, aliases);
    }

    @NotNull
    @Override
    public final <T extends CommandExecutor> T registerCommand(@NotNull T command, String permission, String permissionMessage, String description, @NotNull String... aliases) {
        return CommandMapUtil.registerCommand(this, command, permission, permissionMessage, description, aliases);
    }

    @Nullable
    @Override
    public final <T> T getService(@NotNull Class<T> service) {
        return Services.getNullable(service);
    }

    @Override
    public final <T> void provideService(@NotNull Class<T> clazz, @NotNull T instance, @NotNull ServicePriority priority) {
        Services.provide(clazz, instance, priority);
    }

    @Override
    public final <T> void provideService(@NotNull Class<T> clazz, @NotNull T instance) {
        provideService(clazz, instance, ServicePriority.NORMAL);
    }

    @Override
    public <T> void provideService(@NotNull Class<T> service) {
        Services.provide(service);
    }

    @Override
    public final boolean isPluginPresent(@NotNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public final <T extends Plugin> T getPluginNullable(@NotNull String name, @NotNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends Plugin> @NotNull Optional<T> getPlugin(@NotNull String name, @NotNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return Optional.of((T) getServer().getPluginManager().getPlugin(name));
    }


    private File getRelativeFile(@NotNull String name) {
        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    @Override
    public final @NotNull File getBundledFile(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    @Override
    public final @NotNull YamlConfiguration loadConfig(@NotNull String file) {
        Objects.requireNonNull(file, "file");
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

    @NotNull
    @Override
    public final ConfigurationNode loadConfigNode(@NotNull String file) {
        Objects.requireNonNull(file, "file");
        return ConfigFactory.gson().load(getBundledFile(file));
    }

}