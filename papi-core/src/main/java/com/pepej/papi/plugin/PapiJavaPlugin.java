package com.pepej.papi.plugin;

import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.Services;
import com.pepej.papi.config.ConfigFactory;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.maven.LibraryLoader;
import com.pepej.papi.scheduler.PapiExecutors;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.CommandMapUtil;
import ninja.leaping.configurate.ConfigurationNode;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public abstract class PapiJavaPlugin extends JavaPlugin implements PapiPlugin {

    // the backing terminable registry
    private CompositeTerminable terminableRegistry;

    // are we the plugin that's providing papi?
    private boolean isLoaderPlugin;

    // Used by subclasses to perform logic for plugin load/enable/disable.
    @Override
    public final void onLoad() {
        // LoaderUtils.getPlugin() has the side effect of caching the loader ref
        // do that nice and early. also store whether 'this' plugin is the loader.
        final PapiPlugin loaderPlugin = LoaderUtils.getPlugin();
        this.isLoaderPlugin = this == loaderPlugin;

        this.terminableRegistry = CompositeTerminable.create();

        LibraryLoader.loadAll(getClass());

        // call subclass
        onPluginLoad();
    }

    @Override
    public final void onEnable() {
        // schedule cleanup of the registry
        Schedulers.builder()
                  .async()
                  .after(10, TimeUnit.SECONDS)
                  .every(30, TimeUnit.SECONDS)
                  .run(this.terminableRegistry::cleanup)
                  .bindWith(this.terminableRegistry);

        // setup services
        if (this.isLoaderPlugin) {
            PapiServices.setup(this);
        }

        // call subclass
        onPluginEnable();
    }

    @Override
    public final void onDisable() {

        // call subclass
        onPluginDisable();

        // terminate the registry
        this.terminableRegistry.closeAndReportException();

        if (this.isLoaderPlugin) {
            // shutdown the scheduler
            PapiExecutors.shutdown();
        }
    }

    @NonNull
    @Override
    public <T extends AutoCloseable> T bind(@NonNull T terminable) {
        return this.terminableRegistry.bind(terminable);
    }

    @NonNull
    @Override
    public <T extends TerminableModule> T bindModule(@NonNull T module) {
        return this.terminableRegistry.bindModule(module);
    }

    @NonNull
    @Override
    public <T extends Listener> T registerListener(@NonNull T listener) {
        Objects.requireNonNull(listener, "listener");
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    @NonNull
    @Override
    public <T extends CommandExecutor> T registerCommand(@NonNull T command, String permission, String permissionMessage, String description, @NonNull String... aliases) {
        return CommandMapUtil.registerCommand(this, command, permission, permissionMessage, description, aliases);
    }

    @NonNull
    @Override
    public <T> T getService(@NonNull Class<T> service) {
        return Services.load(service);
    }

    @NonNull
    @Override
    public <T> T provideService(@NonNull Class<T> clazz, @NonNull T instance, @NonNull ServicePriority priority) {
        return Services.provide(clazz, instance, this, priority);
    }

    @NonNull
    @Override
    public <T> T provideService(@NonNull Class<T> clazz, @NonNull T instance) {
        return provideService(clazz, instance, ServicePriority.Normal);
    }

    @Override
    public boolean isPluginPresent(@NonNull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getPlugin(@NonNull String name, @NonNull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    private File getRelativeFile(@NonNull String name) {
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    @NonNull
    @Override
    public File getBundledFile(@NonNull String name) {
        Objects.requireNonNull(name, "name");
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    @NonNull
    @Override
    public YamlConfiguration loadConfig(@NonNull String file) {
        Objects.requireNonNull(file, "file");
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

    @NonNull
    @Override
    public ConfigurationNode loadConfigNode(@NonNull String file) {
        Objects.requireNonNull(file, "file");
        return ConfigFactory.yaml().load(getBundledFile(file));
    }

    @NonNull
    @Override
    public <T> T setupConfig(@NonNull String file, @NonNull T configObject) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(configObject, "configObject");
        File f = getRelativeFile(file);
        ConfigFactory.yaml().load(f, configObject);
        return configObject;
    }

    @NonNull
    @Override
    public ClassLoader getClassloader() {
        return super.getClassLoader();
    }
}