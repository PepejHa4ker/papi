package com.pepej.papi.event.bus.api;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

/**
 * A simple implementation of an event bus.
 */
public class SimpleEventBus<E> implements EventBus<E> {
    private final Class<E> type;
    private final SubscriberRegistry<E> registry = new SubscriberRegistry<>();

    public SimpleEventBus(final @NonNull Class<E> type) {
        this.type = requireNonNull(type, "type");
    }
    @NonNull
    @Override
    public Class<E> eventType() {
        return this.type;
    }

    /**
     * Returns if a given event instance is currently "cancelled".
     *
     * <p>The default implementation of this method uses the
     * {@link Cancellable} interface to determine if an event is cancelled.</p>
     *
     * @param event the event
     * @return true if the event is cancelled
     */
    protected boolean eventCancelled(final @NonNull E event) {
        return event instanceof Cancellable && ((Cancellable) event).cancelled();
    }

    /**
     * Gets the generic {@link Type} of a given event.
     *
     * <p>The default implementation of this method uses the
     * {@link ReifiedEvent} interface to read the generic type of
     * an event.</p>
     *
     * @param event the event
     * @return the generic event type, or null if unknown
     */
    @Nullable
    protected Type eventGenericType(final @NonNull E event) {
        return event instanceof ReifiedEvent<?> ? ((ReifiedEvent<?>) event).type().getType() : null;
    }

    /**
     * Tests if the {@code event} should be posted to the {@code subscriber}.
     *
     * <p>The default implementation of this method tests for cancellation
     * status and matching event/subscriber generic types.</p>
     *
     * @param event      the event
     * @param subscriber the subscriber
     * @return true if the event should be posted
     */
    protected boolean shouldPost(final @NonNull E event, final @NonNull EventSubscriber<?> subscriber) {
        if (!subscriber.consumeCancelledEvents() && this.eventCancelled(event)) {
            return false;
        }
        return Objects.equals(this.eventGenericType(event), subscriber.genericType());
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public PostResult post(final @NonNull E event) {
        ImmutableMap.Builder<EventSubscriber<?>, Throwable> exceptions = null; // save on an allocation
        for (final EventSubscriber subscriber : this.registry.subscribers(event.getClass())) {
            if (!this.shouldPost(event, subscriber)) {
                continue;
            }
            try {
                subscriber.invoke(event);
            } catch (final Throwable e) {
                if (exceptions == null) {
                    exceptions = ImmutableMap.builder();
                }
                exceptions.put(subscriber, e);
            }
        }
        if (exceptions == null) {
            return PostResult.success();
        }
        else {
            return PostResult.failure(exceptions.build());
        }
    }

    @Override
    public <T extends E> void register(final @NonNull Class<T> clazz, final @NonNull EventSubscriber<? super T> subscriber) {
        Preconditions.checkArgument(this.type.isAssignableFrom(clazz), "clazz " + clazz + " cannot be casted to event type " + this.type);
        this.registry.register(clazz, subscriber);
    }

    @Override
    public void unregister(final @NonNull EventSubscriber<?> subscriber) {
        this.registry.unregister(subscriber);
    }

    @Override
    public void unregister(final @NonNull Predicate<EventSubscriber<?>> predicate) {
        this.registry.unregisterMatching(predicate);
    }

    @Override
    public void unregisterAll() {
        this.registry.unregisterAll();
    }

    @Override
    public <T extends E> boolean hasSubscribers(final @NonNull Class<T> clazz) {
        Preconditions.checkArgument(this.type.isAssignableFrom(clazz), "clazz " + clazz + " cannot be casted to event type " + this.type);
        return !this.registry.subscribers(clazz).isEmpty();
    }
    @NonNull
    @Override
    public SetMultimap<Class<?>, EventSubscriber<?>> subscribers() {
        return this.registry.subscribers();
    }
}
