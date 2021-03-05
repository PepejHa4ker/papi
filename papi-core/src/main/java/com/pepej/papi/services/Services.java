package com.pepej.papi.services;

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
        RegisteredServiceProvider<T> registration = ServicesManager.obtain().getRegistration(clazz);
        if (registration == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(registration.getProvider());
    }
    @NonNull
    public static <T> T provide(@NonNull Class<T> clazz) {
        return ServicesManager.obtain().register(clazz);
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
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(priority, "priority");
        ServicesManager.obtain().register(clazz, instance, priority);
        return instance;
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
        return provide(clazz, instance, ServicePriority.NORMAL);
    }

    private Services() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}