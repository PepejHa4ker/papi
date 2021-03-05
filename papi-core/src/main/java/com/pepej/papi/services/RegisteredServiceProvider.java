package com.pepej.papi.services;

import com.pepej.papi.plugin.PapiPlugin;

/**
 * A registered service provider.
 *
 * @param <T> Service
 */
public class RegisteredServiceProvider<T> implements Comparable<RegisteredServiceProvider<?>> {

    private final Class<T> service;
    private final PapiPlugin plugin;
    private final T provider;
    private final ServicePriority priority;

    public RegisteredServiceProvider(Class<T> service, T provider, ServicePriority priority, PapiPlugin plugin) {

        this.service = service;
        this.plugin = plugin;
        this.provider = provider;
        this.priority = priority;
    }

    public Class<T> getService() {
        return service;
    }

    public PapiPlugin getPlugin() {
        return plugin;
    }

    public T getProvider() {
        return provider;
    }

    public ServicePriority getPriority() {
        return priority;
    }

    public int compareTo(RegisteredServiceProvider<?> other) {
        if (priority.ordinal() == other.getPriority().ordinal()) {
            return 0;
        } else {
            return priority.ordinal() < other.getPriority().ordinal() ? 1 : -1;
        }
    }
}
