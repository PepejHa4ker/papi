package com.pepej.papi.events;

import com.google.common.reflect.TypeToken;
import com.pepej.papi.Papi;
import com.pepej.papi.event.functional.merged.MergedSubscriptionBuilder;
import com.pepej.papi.event.functional.single.SingleSubscriptionBuilder;
import com.pepej.papi.events.command.CommandCallEvent;
import com.pepej.papi.events.player.AsyncPlayerFirstJoinEvent;
import com.pepej.papi.events.server.ServerUpdateEvent;
import com.pepej.papi.scheduler.Schedulers;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A functional event listening utility.
 */
public final class Events {

    private static final Map<String, Class<? extends Event>> eventRegistry = new HashMap<>();

    static {
        bind("command:call", CommandCallEvent.class);
        bind("block:break", BlockBreakEvent.class);
        bind("block:place", BlockPlaceEvent.class);
        bind("player:death", PlayerDeathEvent.class);
        bind("player:interact", PlayerInteractEvent.class);
        bind("player:asyncjoin", AsyncPlayerFirstJoinEvent.class);
        bind("player:interact_at_entity", PlayerInteractAtEntityEvent.class);
        bind("player:interact_entity", PlayerInteractEntityEvent.class);
        bind("player:respawn", PlayerRespawnEvent.class);
        bind("player:join", PlayerJoinEvent.class);
        bind("player:drop", PlayerDropItemEvent.class);
        bind("player:gamemode", PlayerGameModeChangeEvent.class);
        bind("player:quit", PlayerQuitEvent.class);
        bind("player:chat", AsyncPlayerChatEvent.class);
        bind("player:move", PlayerMoveEvent.class);
        bind("player:sneak", PlayerToggleSneakEvent.class);
        bind("player:login", PlayerLoginEvent.class);
        bind("player:teleport", PlayerTeleportEvent.class);
        bind("player:sprint", PlayerToggleSprintEvent.class);
        bind("entity:interact", EntityInteractEvent.class);
        bind("entity:spawn", EntitySpawnEvent.class);
        bind("entity:teleport", EntityTeleportEvent.class);
        bind("server:update", ServerUpdateEvent.class);
    }

    private static void bind(String name, Class<? extends Event> eventType) {
        if (eventRegistry.containsKey(name)) {
            throw new IllegalStateException("Event type " + name + " is already registered!");
        }
        eventRegistry.put(name, eventType);
    }


    /**
     * Makes a SingleSubscriptionBuilder for a given event
     *
     * @param eventName the name of the registered event (see above)
     * @return a {@link SingleSubscriptionBuilder} to construct the event handler
     * @throws NullPointerException if eventName is undefined
     */
    @NonNull
    public static SingleSubscriptionBuilder<? extends Event> subscribe(@NonNull String eventName) {
        return SingleSubscriptionBuilder.newBuilder(eventRegistry.get(eventName));
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
    @NonNull
    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@NonNull Class<T> eventClass, @NonNull EventPriority priority) {
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
    @NonNull
    public static <T extends Event> SingleSubscriptionBuilder<T> subscribe(@NonNull Class<T> eventClass) {
        return SingleSubscriptionBuilder.newBuilder(eventClass);
    }


    /**
     * Makes a MergedSubscriptionBuilder for a given super type
     *
     * @param handledClass the super type of the event handler
     * @param <T>          the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    public static <T> MergedSubscriptionBuilder<T> merge(@NonNull Class<T> handledClass) {
        return MergedSubscriptionBuilder.newBuilder(handledClass);
    }

    /**
     * Makes a MergedSubscriptionBuilder for a given super type
     *
     * @param type the super type of the event handler
     * @param <T>  the super type class
     * @return a {@link MergedSubscriptionBuilder} to construct the event handler
     */
    @NonNull
    public static <T> MergedSubscriptionBuilder<T> merge(@NonNull TypeToken<T> type) {
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
    @NonNull
    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(@NonNull Class<S> superClass, @NonNull Class<? extends S>... eventClasses) {
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
    @NonNull
    @SafeVarargs
    public static <S extends Event> MergedSubscriptionBuilder<S> merge(@NonNull Class<S> superClass, @NonNull EventPriority priority, @NonNull Class<? extends S>... eventClasses) {
        return MergedSubscriptionBuilder.newBuilder(superClass, priority, eventClasses);
    }

    /**
     * Submit the event on the current thread
     *
     * @param event the event to call
     */
    public static void call(@NonNull Event event) {
        Papi.plugins().callEvent(event);
    }

    /**
     * Submit the event on a new async thread.
     *
     * @param event the event to call
     */
    public static void callAsync(@NonNull Event event) {
        Schedulers.async().run(() -> call(event));
    }

    /**
     * Submit the event on the main server thread.
     *
     * @param event the event to call
     */
    public static void callSync(@NonNull Event event) {
        Schedulers.sync().run(() -> call(event));
    }

    /**
     * Submit the event on the current thread
     * @param <T> the type of the event
     * @param event the event to call
     * @return event
     */
    @NonNull
    public static <T extends Event> T callAndReturn(@NonNull T event) {
        Papi.plugins().callEvent(event);
        return event;
    }

    /**
     * Submit the event on a new async thread.
     * @param <T> the type of the event
     * @param event the event to call
     * @return event
     */
    @NonNull
    public static <T extends Event> T callAsyncAndJoin(@NonNull T event) {
        return Schedulers.async().supply(() -> callAndReturn(event)).join();
    }

    /**
     * Submit the event on the main server thread.
     * @param <T> the type of the event
     * @param event the event to call
     * @return event
     */
    @NonNull
    public static <T extends Event> T callSyncAndJoin(@NonNull T event) {
        return Schedulers.sync().supply(() -> callAndReturn(event)).join();
    }

    private Events() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
