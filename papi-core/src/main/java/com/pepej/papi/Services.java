package com.pepej.papi;

import com.pepej.papi.internal.LoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;
import java.util.Optional;

/**
 * Utility class for interacting with the Bukkit {@link ServicesManager}.
 */
public final class Services {

    /**
     * Loads a service instance, throwing a {@link IllegalStateException} if no registration is
     * present.
     *
     * @param clazz the service class
     * @param <T> the service class type
     * @return the service instance
     */
    @NonNull
    public static <T> T load(@NonNull Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        return get(clazz).orElseThrow(() -> new IllegalStateException("No registration present for service '" + clazz.getName() + "'"));
    }

    /**
     * Loads a service instance
     *
     * @param clazz the service class
     * @param <T> the service class type
     * @return the service instance, as an optional
     */
    @NonNull
    public static <T> Optional<T> get(@NonNull Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        RegisteredServiceProvider<T> registration = Bukkit.getServicesManager().getRegistration(clazz);
        if (registration == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(registration.getProvider());
    }

    /**
     * Provides a service.
     *
     * @param clazz the service class
     * @param instance the service instance
     * @param plugin the plugin to register the service to
     * @param priority the priority to register the service instance at
     * @param <T> the service class type
     * @return the same service instance
     */
    @NonNull
    public static <T> T provide(@NonNull Class<T> clazz, @NonNull T instance, @NonNull Plugin plugin, @NonNull ServicePriority priority) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(priority, "priority");
        Bukkit.getServicesManager().register(clazz, instance, plugin, priority);
        return instance;
    }

    /**
     * Provides a service.
     *
     * @param clazz the service class
     * @param instance the service instance
     * @param priority the priority to register the service instance at
     * @param <T> the service class type
     * @return the same service instance
     */
    @NonNull
    public static <T> T provide(@NonNull Class<T> clazz, @NonNull T instance, @NonNull ServicePriority priority) {
        return provide(clazz, instance, LoaderUtils.getPlugin(), priority);
    }

    /**
     * Provides a service.
     *
     * @param clazz the service class
     * @param instance the service instance
     * @param <T> the service class type
     * @return the same service instance
     */
    @NonNull
    public static <T> T provide(@NonNull Class<T> clazz, @NonNull T instance) {
        return provide(clazz, instance, ServicePriority.Normal);
    }

    private Services() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}