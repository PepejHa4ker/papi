package com.pepej.papi.plugin;

import com.pepej.papi.Schedulers;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PapiBukkitPlugin extends JavaPlugin implements PapiPlugin {

    // the backing terminable registry
    private CompositeTerminable terminableRegistry;

    // are we the plugin that's providing papi?
    private boolean isLoaderPlugin;

    // Used by subclasses to perform logic for plugin load/enable/disable.
    protected void load() {}
    protected void enable() {}
    protected void disable() {}

    @Override
    public final void onLoad() {
        // LoaderUtils.getPlugin() has the side effect of caching the loader ref
        // do that nice and early. also store whether 'this' plugin is the loader.
        final PapiPlugin loaderPlugin = LoaderUtils.getPlugin();
        this.isLoaderPlugin = this == loaderPlugin;

        this.terminableRegistry = CompositeTerminable.create();

        LibraryLoader.loadAll(getClass());

        // call subclass
        load();
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
        enable();
    }

    @Override
    public final void onDisable() {

        // call subclass
        disable();

        // terminate the registry
        this.terminableRegistry.closeAndReportException();

        if (this.isLoaderPlugin) {
            // shutdown the scheduler
            PapiExecutors.shutdown();
        }
    }

    @Nonnull
    @Override
    public <T extends AutoCloseable> T bind(@Nonnull T terminable) {
        return this.terminableRegistry.bind(terminable);
    }

    @Nonnull
    @Override
    public <T extends TerminableModule> T bindModule(@Nonnull T module) {
        return this.terminableRegistry.bindModule(module);
    }

    @Nonnull
    @Override
    public <T extends Listener> T registerListener(@Nonnull T listener) {
        Objects.requireNonNull(listener, "listener");
        getServer().getPluginManager().registerEvents(listener, this);
        return listener;
    }

    @Nonnull
    @Override
    public <T extends CommandExecutor> T registerCommand(@Nonnull T command, String permission, String permissionMessage, String description, @Nonnull String... aliases) {
        return CommandMapUtil.registerCommand(this, command, permission, permissionMessage, description, aliases);
    }

    @Nonnull
    @Override
    public <T> T getService(@Nonnull Class<T> service) {
        return Services.load(service);
    }

    @Nonnull
    @Override
    public <T> T provideService(@Nonnull Class<T> clazz, @Nonnull T instance, @Nonnull ServicePriority priority) {
        return Services.provide(clazz, instance, this, priority);
    }

    @Nonnull
    @Override
    public <T> T provideService(@Nonnull Class<T> clazz, @Nonnull T instance) {
        return provideService(clazz, instance, ServicePriority.Normal);
    }

    @Override
    public boolean isPluginPresent(@Nonnull String name) {
        return getServer().getPluginManager().getPlugin(name) != null;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getPlugin(@Nonnull String name, @Nonnull Class<T> pluginClass) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(pluginClass, "pluginClass");
        return (T) getServer().getPluginManager().getPlugin(name);
    }

    private File getRelativeFile(@Nonnull String name) {
        getDataFolder().mkdirs();
        return new File(getDataFolder(), name);
    }

    @Nonnull
    @Override
    public File getBundledFile(@Nonnull String name) {
        Objects.requireNonNull(name, "name");
        File file = getRelativeFile(name);
        if (!file.exists()) {
            saveResource(name, false);
        }
        return file;
    }

    @Nonnull
    @Override
    public YamlConfiguration loadConfig(@Nonnull String file) {
        Objects.requireNonNull(file, "file");
        return YamlConfiguration.loadConfiguration(getBundledFile(file));
    }

    @Nonnull
    @Override
    public ConfigurationNode loadConfigNode(@Nonnull String file) {
        Objects.requireNonNull(file, "file");
        return ConfigFactory.yaml().load(getBundledFile(file));
    }

    @Nonnull
    @Override
    public <T> T setupConfig(@Nonnull String file, @Nonnull T configObject) {
        Objects.requireNonNull(file, "file");
        Objects.requireNonNull(configObject, "configObject");
        File f = getRelativeFile(file);
        ConfigFactory.yaml().load(f, configObject);
        return configObject;
    }

    @Nonnull
    @Override
    public ClassLoader getClassloader() {
        return super.getClassLoader();
    }
}