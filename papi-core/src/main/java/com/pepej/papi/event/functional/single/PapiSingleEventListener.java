package com.pepej.papi.event.functional.single;

import com.pepej.papi.Papi;
import com.pepej.papi.event.SingleSubscription;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

class PapiSingleEventListener<T extends Event> implements SingleSubscription<T>, EventExecutor, Listener {
    private final Class<T> eventClass;
    private final EventPriority priority;

    private final BiConsumer<? super T, Exception> exceptionConsumer;
    private final boolean handleSubclasses;
    private final boolean ignoreCancelled;

    private final Predicate<T>[] filters;
    private final BiPredicate<SingleSubscription<T>, T>[] preExpiryTests;
    private final BiPredicate<SingleSubscription<T>, T>[] midExpiryTests;
    private final BiPredicate<SingleSubscription<T>, T>[] postExpiryTests;
    private final BiConsumer<SingleSubscription<T>, ? super T>[] handlers;

    private final AtomicLong callCount = new AtomicLong(0);
    private final AtomicBoolean active = new AtomicBoolean(true);

    @SuppressWarnings("unchecked")
    PapiSingleEventListener(SingleSubscriptionBuilderImpl<T> builder, List<BiConsumer<SingleSubscription<T>, ? super T>> handlers) {
        this.eventClass = builder.eventClass;
        this.priority = builder.priority;
        this.exceptionConsumer = builder.exceptionConsumer;
        this.handleSubclasses = builder.handleSubclasses;
        this.ignoreCancelled = builder.ignoreCancelled;
        this.filters = builder.filters.toArray(new Predicate[0]);
        this.preExpiryTests = builder.preExpiryTests.toArray(new BiPredicate[0]);
        this.midExpiryTests = builder.midExpiryTests.toArray(new BiPredicate[0]);
        this.postExpiryTests = builder.postExpiryTests.toArray(new BiPredicate[0]);
        this.handlers = handlers.toArray(new BiConsumer[0]);
    }

    void register(Plugin plugin) {
        Papi.plugins().registerEvent(eventClass, this, priority, this, plugin, false);
    }

    @Override
    public void execute(Listener listener, Event event) {
        // check we actually want this event
        if (handleSubclasses) {
            if (!eventClass.isInstance(event)) {
                return;
            }
        }
        else {
            if (event.getClass() != eventClass) {
                return;
            }
        }

        // this handler is disabled, so unregister from the event.
        if (!active.get()) {
            event.getHandlers().unregister(listener);
            return;
        }

        // obtain the event instance
        T eventInstance = eventClass.cast(event);

        if (eventInstance instanceof Cancellable) {
            Cancellable cancellable = (Cancellable) eventInstance;
            if (cancellable.isCancelled()) {
                if (!ignoreCancelled) {
                    return;
                }
            }
        }


        if (eventInstance instanceof com.pepej.papi.event.bus.api.Cancellable) {
            com.pepej.papi.event.bus.api.Cancellable c = (com.pepej.papi.event.bus.api.Cancellable) eventInstance;
            if (c.cancelled()) {
                if (!ignoreCancelled) {
                    return;
                }
            }
        }

        // check pre-expiry tests
        for (BiPredicate<SingleSubscription<T>, T> test : preExpiryTests) {
            if (test.test(this, eventInstance)) {
                event.getHandlers().unregister(listener);
                active.set(false);
                return;
            }
        }

        // begin "handling" of the event
        try {
            // check the filters
            for (Predicate<T> filter : filters) {
                if (!filter.test(eventInstance)) {
                    return;
                }
            }

            // check mid-expiry tests
            for (BiPredicate<SingleSubscription<T>, T> test : midExpiryTests) {
                if (test.test(this, eventInstance)) {
                    event.getHandlers().unregister(listener);
                    active.set(false);
                    return;
                }
            }

            // call the handler
            for (BiConsumer<SingleSubscription<T>, ? super T> handler : handlers) {
                handler.accept(this, eventInstance);
            }

            // increment call counter
            callCount.incrementAndGet();
        } catch (Exception t) {
            exceptionConsumer.accept(eventInstance, t);
        }

        // check post-expiry tests
        for (BiPredicate<SingleSubscription<T>, T> test : postExpiryTests) {
            if (test.test(this, eventInstance)) {
                event.getHandlers().unregister(listener);
                active.set(false);
                return;
            }
        }
    }

    @NonNull
    @Override
    public Class<T> getEventClass() {
        return this.eventClass;
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public boolean isClosed() {
        return !active.get();
    }

    @Override
    public long getCallCounter() {
        return callCount.get();
    }

    @Override
    public boolean unregister() {
        // already unregistered
        if (!active.getAndSet(false)) {
            return false;
        }

        // also remove the handler directly, just in case the event has a really low throughput.
        // (the event would also be unregistered next time it's called - but this obviously assumes
        // the event will be called again soon)
        unregisterListener(eventClass, this);

        return true;
    }

    @Override
    public Collection<Object> getFunctions() {
        List<Object> functions = new ArrayList<>();
        Collections.addAll(functions, filters);
        Collections.addAll(functions, preExpiryTests);
        Collections.addAll(functions, midExpiryTests);
        Collections.addAll(functions, postExpiryTests);
        Collections.addAll(functions, handlers);
        return functions;
    }

    private static void unregisterListener(Class<? extends Event> eventClass, Listener listener) {
        try {
            // unfortunately we can't cache this reflect call, as the method is static
            Method getHandlerListMethod = eventClass.getMethod("getHandlerList");
            HandlerList handlerList = (HandlerList) getHandlerListMethod.invoke(null);
            handlerList.unregister(listener);
        } catch (Exception ignored) {
        }
    }
}
