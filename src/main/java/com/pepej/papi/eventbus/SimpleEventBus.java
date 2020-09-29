package com.pepej.papi.eventbus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * A simple implementation of an event bus.
 */
public class SimpleEventBus<E> implements EventBus<E> {
    private final Class<E> type;
    private final SubscriberRegistry<E> registry = new SubscriberRegistry<>();

    public SimpleEventBus(final @Nonnull Class<E> type) {
        this.type = requireNonNull(type, "type");
    }

    @Override
    public @Nonnull Class<E> eventType() {
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
    protected boolean eventCancelled(final @Nonnull E event) {
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
    protected @Nullable Type eventGenericType(final @Nonnull E event) {
        return event instanceof ReifiedEvent<?> ? ((ReifiedEvent<?>) event).type().getType() : null;
    }

    /**
     * Tests if the {@code event} should be posted to the {@code subscriber}.
     *
     * <p>The default implementation of this method tests for cancellation
     * status and matching event/subscriber generic types.</p>
     *
     * @param event the event
     * @param subscriber the subscriber
     * @return true if the event should be posted
     */
    protected boolean shouldPost(final @Nonnull E event, final @Nonnull EventSubscriber<?> subscriber) {
        if(!subscriber.consumeCancelledEvents() && this.eventCancelled(event)) {
            return false;
        }
        return Objects.equals(this.eventGenericType(event), subscriber.genericType());
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nonnull PostResult post(final @Nonnull E event) {
        ImmutableMap.Builder<EventSubscriber<?>, Throwable> exceptions = null; // save on an allocation
        for(final EventSubscriber subscriber : this.registry.subscribers(event.getClass())) {
            if(!this.shouldPost(event, subscriber)) {
                continue;
            }
            try {
                subscriber.invoke(event);
            } catch(final Throwable e) {
                if(exceptions == null) {
                    exceptions = ImmutableMap.builder();
                }
                exceptions.put(subscriber, e);
            }
        }
        if(exceptions == null) {
            return PostResult.success();
        } else {
            return PostResult.failure(exceptions.build());
        }
    }

    @Override
    public <T extends E> void register(final @Nonnull Class<T> clazz, final @Nonnull EventSubscriber<? super T> subscriber) {
        checkArgument(this.type.isAssignableFrom(clazz), "clazz " + clazz + " cannot be casted to event type " + this.type);
        this.registry.register(clazz, subscriber);
    }

    @Override
    public void unregister(final @Nonnull EventSubscriber<?> subscriber) {
        this.registry.unregister(subscriber);
    }

    @Override
    public void unregister(final @Nonnull Predicate<EventSubscriber<?>> predicate) {
        this.registry.unregisterMatching(predicate);
    }

    @Override
    public void unregisterAll() {
        this.registry.unregisterAll();
    }

    @Override
    public <T extends E> boolean hasSubscribers(final @Nonnull Class<T> clazz) {
        checkArgument(this.type.isAssignableFrom(clazz), "clazz " + clazz + " cannot be casted to event type " + this.type);
        return !this.registry.subscribers(clazz).isEmpty();
    }

    @Override
    public @Nonnull SetMultimap<Class<?>, EventSubscriber<?>> subscribers() {
        return this.registry.subscribers();
    }
}
