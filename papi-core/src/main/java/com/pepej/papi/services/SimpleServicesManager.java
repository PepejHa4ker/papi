package com.pepej.papi.services;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import lombok.SneakyThrows;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * A simple services manager.
 */
final class SimpleServicesManager implements ServicesManager {

    private static volatile SimpleServicesManager instance;

    static ServicesManager obtain() {
        if (instance == null) {
            synchronized (SimpleServicesManager.class) {
                if (instance == null) {
                    instance = new SimpleServicesManager();
                }
            }
        }
        return instance;
    }


    /**
     * Map of providers.
     */
    private final Map<Class<?>, List<RegisteredServiceProvider<?>>> providers = new HashMap<>();

    @SneakyThrows
    @Override
    @SuppressWarnings("unchecked")
    public <T> T register(Class<T> service) {
        RegisteredServiceProvider<T> registeredProvider;
        T provider;
        synchronized (providers) {
            Implementor implementor = service.getAnnotation(Implementor.class);
            if (implementor == null) {
                throw new IllegalArgumentException("Can't find @Implementor annotation on Service class");
            }
            Class<?> specifiedProviderClass = implementor.value();
            if (!service.isAssignableFrom(specifiedProviderClass)) {
                throw new IllegalStateException("Specified provider isn't instanceof Service class");
            }
            if (!Modifier.isPublic(specifiedProviderClass.getModifiers())) {
                throw new IllegalArgumentException("Implementation class must be a public!");
            }
            Class<T> providerClass = (Class<T>) specifiedProviderClass;
            provider = providerClass.getDeclaredConstructor().newInstance();
            for (Field field : providerClass.getDeclaredFields()) {
                field.setAccessible(true);
                final Service serviceAnn = field.getDeclaredAnnotation(Service.class);
                if (serviceAnn == null) {
                    continue;
                }
                RegisteredServiceProvider<?> registration = getRegistration(field.getType());
                if (registration == null) {
                    throw new IllegalArgumentException("No registration present for type " + field.getType());
                }
                field.set(provider, registration.getProvider());
            }
            Constructor<?> constructor = providerClass.getDeclaredConstructor();
            if (constructor.getParameterCount() != 0) {
                throw new IllegalArgumentException("No default constructor present");
            }
            List<RegisteredServiceProvider<?>> registered = providers.computeIfAbsent(service, k -> new ArrayList<>());
            registeredProvider = new RegisteredServiceProvider<>(service, provider, implementor.priority());
            // Insert the provider into the collection, much more efficient big O than sort
            int position = Collections.binarySearch(registered, registeredProvider);
            if (position < 0) {
                registered.add(-(position + 1), registeredProvider);
            }
            else {
                registered.add(position, registeredProvider);
            }

        }
        return provider;
    }

    /**
     * Register a provider of a service.
     *
     * @param <T>      Provider
     * @param service  service class
     * @param provider provider to register
     * @param priority priority of the provider
     */
    @SneakyThrows
    public <T> void register(Class<T> service, T provider, ServicePriority priority) {
        RegisteredServiceProvider<T> registeredProvider;
        synchronized (providers) {
            List<RegisteredServiceProvider<?>> registered = providers.computeIfAbsent(service, k -> new ArrayList<>());
            for (Field field : provider.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                final Service serviceAnn = field.getDeclaredAnnotation(Service.class);
                if (serviceAnn == null) {
                    continue;
                }
                RegisteredServiceProvider<?> registration = getRegistration(field.getType());
                if (registration == null) {
                    throw new IllegalArgumentException("No registration present for type " + field.getType());
                }
                field.set(provider, registration.getProvider());
            }
            registeredProvider = new RegisteredServiceProvider<>(service, provider, priority);

            // Insert the provider into the collection, much more efficient big O than sort
            int position = Collections.binarySearch(registered, registeredProvider);
            if (position < 0) {
                registered.add(-(position + 1), registeredProvider);
            }
            else {
                registered.add(position, registeredProvider);
            }

        }
    }

    /**
     * Unregister a particular provider for a particular service.
     *
     * @param service  The service interface
     * @param provider The service provider implementation
     */
    public void unregister(Class<?> service, Object provider) {
        synchronized (providers) {
            Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = providers.entrySet().iterator();

            try {
                while (it.hasNext()) {
                    Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();

                    // We want a particular service
                    if (entry.getKey() != service) {
                        continue;
                    }

                    Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

                    try {
                        // Removed entries that are from this plugin

                        while (it2.hasNext()) {
                            RegisteredServiceProvider<?> registered = it2.next();

                            if (registered.getProvider() == provider) {
                                it2.remove();
                            }
                        }
                    } catch (NoSuchElementException e) { // Why does Java suck
                    }

                    // Get rid of the empty list
                    if (entry.getValue().size() == 0) {
                        it.remove();
                    }
                }
            } catch (NoSuchElementException ignored) {
            }
        }

    }

    /**
     * Unregister a particular provider.
     *
     * @param provider The service provider implementation
     */
    public void unregister(Object provider) {
        synchronized (providers) {
            Iterator<Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>>> it = providers.entrySet().iterator();

            try {
                while (it.hasNext()) {
                    Map.Entry<Class<?>, List<RegisteredServiceProvider<?>>> entry = it.next();
                    Iterator<RegisteredServiceProvider<?>> it2 = entry.getValue().iterator();

                    try {
                        // Removed entries that are from this plugin

                        while (it2.hasNext()) {
                            RegisteredServiceProvider<?> registered = it2.next();

                            if (registered.getProvider().equals(provider)) {
                                it2.remove();
                            }
                        }
                    } catch (NoSuchElementException e) { // Why does Java suck
                    }

                    // Get rid of the empty list
                    if (entry.getValue().size() == 0) {
                        it.remove();
                    }
                }
            } catch (NoSuchElementException ignored) {
            }
        }
    }

    /**
     * Queries for a provider. This may return if no provider has been
     * registered for a service. The highest priority provider is returned.
     *
     * @param <T>     The service interface
     * @param service The service interface
     * @return provider or null
     */
    public <T> T load(Class<T> service) {
        synchronized (providers) {
            List<RegisteredServiceProvider<?>> registered = providers.get(service);

            if (registered == null) {
                return null;
            }

            // This should not be null!
            return service.cast(registered.get(0).getProvider());
        }
    }

    /**
     * Queries for a provider registration. This may return if no provider
     * has been registered for a service.
     *
     * @param <T>     The service interface
     * @param service The service interface
     * @return provider registration or null
     */
    @SuppressWarnings("unchecked")
    public <T> RegisteredServiceProvider<T> getRegistration(Class<T> service) {
        synchronized (providers) {
            List<RegisteredServiceProvider<?>> registered = providers.get(service);

            if (registered == null) {
                return null;
            }

            // This should not be null!
            return (RegisteredServiceProvider<T>) registered.get(0);
        }
    }

    /**
     * Get registrations of providers for a service. The returned list is
     * an unmodifiable copy.
     *
     * @param <T>     The service interface
     * @param service The service interface
     * @return a copy of the list of registrations
     */
    @SuppressWarnings("unchecked")
    public <T> List<RegisteredServiceProvider<T>> getRegistrations(Class<T> service) {
        ImmutableList.Builder<RegisteredServiceProvider<T>> ret;
        synchronized (providers) {
            List<RegisteredServiceProvider<?>> registered = providers.get(service);

            if (registered == null) {
                return ImmutableList.of();
            }

            ret = ImmutableList.builder();

            for (RegisteredServiceProvider<?> provider : registered) {
                ret.add((RegisteredServiceProvider<T>) provider);
            }

        }
        return ret.build();
    }

    /**
     * Get a list of known services. A service is known if it has registered
     * providers for it.
     *
     * @return a copy of the set of known services
     */
    public Set<Class<?>> getKnownServices() {
        synchronized (providers) {
            return ImmutableSet.copyOf(providers.keySet());
        }
    }

    /**
     * Returns whether a provider has been registered for a service.
     *
     * @param <T>     service
     * @param service service to check
     * @return true if and only if there are registered providers
     */
    public <T> boolean isProvidedFor(Class<T> service) {
        synchronized (providers) {
            return providers.containsKey(service);
        }
    }
}
