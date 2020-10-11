package com.pepej.papi.event.functional.merged;

import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import java.util.function.Function;

class MergedHandlerMapping<T, E extends Event> {
    private final EventPriority priority;
    private final Function<Object, T> function;

    MergedHandlerMapping(EventPriority priority, Function<E, T> function) {
        this.priority = priority;
        //noinspection unchecked
        this.function = o -> function.apply((E) o);
    }

    public Function<Object, T> getFunction() {
        return this.function;
    }

    public EventPriority getPriority() {
        return this.priority;
    }
}
