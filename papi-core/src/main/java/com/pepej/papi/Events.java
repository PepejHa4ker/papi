package com.pepej.papi;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.command.event.CommandCallEvent;
import com.pepej.papi.event.functional.merged.MergedSubscriptionBuilder;
import com.pepej.papi.event.functional.single.SingleSubscriptionBuilder;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A functional event listening utility.
 */
public final class Events {

    private static final Map<String, Class<? extends Event>> eventRegistry = new HashMap<>();

    static {
        register("command:call", CommandCallEvent.class);
        register("block:break", BlockBreakEvent.class);
        register("block:place", BlockPlaceEvent.class);
        register("player:death", PlayerDeathEvent.class);
        register("player:interact", PlayerInteractEvent.class);
        register("player:respawn", PlayerRespawnEvent.class);
        register("player:join", PlayerJoinEvent.class);
        register("player:quit", PlayerQuitEvent.class);
        register("player:chat", AsyncPlayerChatEvent.class);
        register("player:move", PlayerMoveEvent.class);
        register("player:sneak", PlayerToggleSneakEvent.class);
        register("player:login", PlayerLoginEvent.class);
        register("player:teleport", PlayerTeleportEvent.class);
        register("player:sprint", PlayerToggleSprintEvent.class);
        register("entity:interact", EntityInteractEvent.class);
        register("entity:spawn", EntitySpawnEvent.class);
        register("entity:teleport", EntityTeleportEvent.class);
    }

    private static void register(String name, Class<? extends Event> eventType) {
        if (eventRegistry.containsKey(name)) {
            throw new IllegalStateException("Event type " + name + " is already registered!");
        }
        eventRegistry.put(name, eventType);
    }


    /**
     * Makes a SingleSubscriptionBuilder for a given event
     *
     * @param eventName the name of the event
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventName is undefined
     */
    @Nonnull
    public static SingleSubscriptionBuilder<? extends Event> subscribe(@Nonnull String eventName) {
        Class<? extends Event> type = Objects.requireNonNull(eventRegistry.get(eventName), "Unknown event type: " + eventName);
        return SingleSubscriptionBuilder.newBuilder(type);
    }

    /**
     * Makes a SingleSubscriptionBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param priority   the priority to listen at
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass or priority is null
     */
    @Nonnull
    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@Nonnull Class<T> eventClass, @Nonnull EventPriority priority) {
        return SingleSubscriptionBuilder.newBuilder(eventClass, priority);
    }

    /**
     * Makes a SingleSubscriptionBuilder for a given event
     *
     * @param eventClass the class of the event
     * @param <T>        the event type
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventClass is null
     */
    @Nonnull
    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@Nonnull Class<T> eventClass) {
        return SingleSubscriptionBuilder.newBuilder(eventClass);
    }


    /**
     * Makes a MergedSubscriptionBuilder for a given super type
     *
     * @param handledClass the super type of the event handler
     * @param <T>          the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @Nonnull
    public static <T> MergedSubscriptionBuilder<T> merge(@Nonnull Class<T> handledClass) {
        return MergedSubscriptionBuilder.newBuilder(handledClass);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a given super type
     *
     * @param type the super type of the event handler
     * @param <T>  the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @Nonnull
    public static <T> MergedSubscriptionBuilder<T> merge(@Nonnull TypeToken<T> type) {
        return MergedSubscriptionBuilder.newBuilder(type);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a super event class
     *
     * @param superClass   the abstract super event class
     * @param eventClasses the event classes to be bound to
     * @param <S>          the super class type
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @Nonnull
    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(@Nonnull Class<S> superClass, @Nonnull Class<? extends S>... eventClasses) {
        return MergedSubscriptionBuilder.newBuilder(superClass, eventClasses);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a super event class
     *
     * @param superClass   the abstract super event class
     * @param priority     the priority to listen at
     * @param eventClasses the event classes to be bound to
     * @param <S>          the super class type
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @Nonnull
    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(@Nonnull Class<S> superClass, @Nonnull EventPriority priority, @Nonnull Class<? extends S>... eventClasses) {
        return MergedSubscriptionBuilder.newBuilder(superClass, priority, eventClasses);
    }

    /**
     * Submit the event on the current thread
     *
     * @param event the event to call
     */
    public static void call(@Nonnull Event event) {
        Papi.plugins().callEvent(event);
    }

    /**
     * Submit the event on a new async thread.
     *
     * @param event the event to call
     */
    public static void callAsync(@Nonnull Event event) {
        Schedulers.async().run(() -> call(event));
    }

    /**
     * Submit the event on the main server thread.
     *
     * @param event the event to call
     */
    public static void callSync(@Nonnull Event event) {
        Schedulers.sync().run(() -> call(event));
    }

    /**
     * Submit the event on the current thread
     *
     * @param event the event to call
     */
    @Nonnull
    public static <T extends Event> T callAndReturn(@Nonnull T event) {
        Papi.plugins().callEvent(event);
        return event;
    }

    /**
     * Submit the event on a new async thread.
     *
     * @param event the event to call
     */
    @Nonnull
    public static <T extends Event> T callAsyncAndJoin(@Nonnull T event) {
        return Schedulers.async().supply(() -> callAndReturn(event)).join();
    }

    /**
     * Submit the event on the main server thread.
     *
     * @param event the event to call
     */
    @Nonnull
    public static <T extends Event> T callSyncAndJoin(@Nonnull T event) {
        return Schedulers.sync().supply(() -> callAndReturn(event)).join();
    }

    private Events() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
