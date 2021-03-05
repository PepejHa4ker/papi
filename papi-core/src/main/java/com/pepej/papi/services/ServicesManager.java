package com.pepej.papi.services;


import com.pepej.papi.plugin.PapiPlugin;

import java.util.Collection;
import java.util.List;

/**
 * Manages services and service providers. Services are an interface
 * specifying a list of methods that a provider must implement. Providers are
 * implementations of these services. A provider can be queried from the
 * services manager in order to use a service (if one is available). If
 * multiple plugins register a service, then the service with the highest
 * priority takes precedence.
 */
public interface ServicesManager {

    static ServicesManager obtain() {
        return SimpleServicesManager.obtain();
    }

    /**
     * Register a provider of a service.
     *
     * @param <T>      Provider
     * @param service  service class
     * @param provider provider to register
     * @param plugin   plugin with the provider
     * @param priority priority of the provider
     */
    <T> void register(Class<T> service, T provider, PapiPlugin plugin, ServicePriority priority);

    /**
     * Unregister all the providers registered by a particular plugin.
     *
     * @param plugin The plugin
     */
    void unregisterAll(PapiPlugin plugin);

    /**
     * Unregister a particular provider for a particular service.
     *
     * @param service  The service interface
     * @param provider The service provider implementation
     */
    void unregister(Class<?> service, Object provider);

    /**
     * Unregister a particular provider.
     *
     * @param provider The service provider implementation
     */
    void unregister(Object provider);

    /**
     * Queries for a provider. This may return if no provider has been
     * registered for a service. The highest priority provider is returned.
     *
     * @param <T>     The service interface
     * @param service The service interface
     * @return provider or null
     */
    <T> T load(Class<T> service);

    /**
     * Queries for a provider registration. This may return if no provider
     * has been registered for a service.
     *
     * @param <T>     The service interface
     * @param service The service interface
     * @return provider registration or null
     */
    <T> RegisteredServiceProvider<T> getRegistration(Class<T> service);

    /**
     * Get registrations of providers for a plugin.
     *
     * @param plugin The plugin
     * @return provider registration or null
     */
    List<RegisteredServiceProvider<?>> getRegistrations(PapiPlugin plugin);

    /**
     * Get registrations of providers for a service. The returned list is
     * unmodifiable.
     *
     * @param <T>     The service interface
     * @param service The service interface
     * @return list of registrations
     */
    <T> Collection<RegisteredServiceProvider<T>> getRegistrations(Class<T> service);

    /**
     * Get a list of known services. A service is known if it has registered
     * providers for it.
     *
     * @return list of known services
     */
    Collection<Class<?>> getKnownServices();

    /**
     * Returns whether a provider has been registered for a service. Do not
     * check this first only to call <code>load(service)</code> later, as that
     * would be a non-thread safe situation.
     *
     * @param <T>     service
     * @param service service to check
     * @return whether there has been a registered provider
     */
    <T> boolean isProvidedFor(Class<T> service);

}
