package com.pepej.papi.eventbus;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.reflect.TypeToken;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A subscriber registry.
 *
 * @param <E> the event type
 */
final class SubscriberRegistry<E> {
    /**
     * A cache of class --> a set of interfaces and classes that the class is or is a subtype of.
     */
    private static final LoadingCache<Class<?>, Set<Class<?>>> CLASS_HIERARCHY = CacheBuilder.newBuilder()
                                                                                             .weakKeys()
                                                                                             .build(CacheLoader.from(key -> (Set<Class<?>>) TypeToken.of(key).getTypes().rawTypes()));
    /**
     * A raw, unresolved multimap of class --> event subscriber.
     *
     * <p>An event subscriber is mapped to the class it is registered with.</p>
     */
    // technically a Multimap<Class<T>, EventSubscriber<? super T>>
    private final SetMultimap<Class<?>, EventSubscriber<?>> subscribers = HashMultimap.create();
    /**
     * A cache containing a link between an event class, and the eventsubscribers which
     * should be passed the given type of event.
     */
    private final LoadingCache<Class<?>, List<EventSubscriber<?>>> cache = CacheBuilder.newBuilder()
                                                                                       .initialCapacity(85)
                                                                                       .build(CacheLoader.from(eventClass -> {
                                                                                           final List<EventSubscriber<?>> subscribers = new ArrayList<>();
                                                                                           final Set<? extends Class<?>> types = CLASS_HIERARCHY.getUnchecked(eventClass);
                                                                                           assert types != null;
                                                                                           synchronized(this.lock) {
                                                                                               for(final Class<?> type : types) {
                                                                                                   subscribers.addAll(this.subscribers.get(type));
                                                                                               }
                                                                                           }

                                                                                           subscribers.sort(Comparator.comparingInt(EventSubscriber::postOrder));
                                                                                           return subscribers;
                                                                                       }));
    private final Object lock = new Object();

    SubscriberRegistry() {
    }

    <T extends E> void register(final @Nonnull Class<T> clazz, final @Nonnull EventSubscriber<? super T> subscriber) {
        synchronized(this.lock) {
            this.subscribers.put(clazz, subscriber);
            this.cache.invalidateAll();
        }
    }

    void unregister(final @Nonnull EventSubscriber<?> subscriber) {
        this.unregisterMatching(h -> h.equals(subscriber));
    }

    void unregisterMatching(final @Nonnull Predicate<EventSubscriber<?>> predicate) {
        synchronized(this.lock) {
            final boolean dirty = this.subscribers.values().removeIf(predicate);
            if(dirty) {
                this.cache.invalidateAll();
            }
        }
    }

    void unregisterAll() {
        synchronized(this.lock) {
            this.subscribers.clear();
            this.cache.invalidateAll();
        }
    }

    @Nonnull SetMultimap<Class<?>, EventSubscriber<?>> subscribers() {
        synchronized(this.lock) {
            return ImmutableSetMultimap.copyOf(this.subscribers);
        }
    }

    @Nonnull List<EventSubscriber<?>> subscribers(final @Nonnull Class<?> clazz) {
        return this.cache.getUnchecked(clazz);
    }
}
