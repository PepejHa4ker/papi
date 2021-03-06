package com.pepej.papi.menu;

import com.pepej.papi.events.player.PlayerInventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Basic implementation of {@link Slot}.
 */
public class SimpleSlot implements Slot {

    // the parent menu
    private final Menu menu;

    // the id of this slot
    private final int id;
    private boolean cancelClicks;

    // the click handlers for this slot
    protected final Map<ClickType, Set<Consumer<PlayerInventoryClickEvent>>> handlers;

    public SimpleSlot(@NonNull Menu menu, int id) {
        this.menu = menu;
        this.id = id;
        this.cancelClicks = true;
        this.handlers = Collections.synchronizedMap(new EnumMap<>(ClickType.class));
    }

    @Override
    public void handle(@NonNull PlayerInventoryClickEvent event) {
        Set<Consumer<PlayerInventoryClickEvent>> handlers = this.handlers.get(event.getClick());
        if (handlers == null) {
            return;
        }

        for (Consumer<PlayerInventoryClickEvent> handler : handlers) {
            handler.accept(event);
        }
    }

    /**
     * Gets the GUI this slot references
     *
     * @return the parent menu
     */
    @NonNull
    @Override
    public Menu menu() {
        return this.menu;
    }

    /**
     * Gets the id of this slot
     *
     * @return the id
     */
    @Override
    public int getId() {
        return this.id;
    }

    /**
     * Applies an item model to this slot.
     *
     * @param item the item
     * @return this slot
     */
    @Override
    public Slot applyFromItem(Item item) {
        Objects.requireNonNull(item, "item");
        setItem(item.getItemStack());
        clearBindings();
        bindAllConsumers(item.getHandlers().entrySet());
        return this;
    }

    /**
     * Gets the item in this slot
     *
     * @return the item in this slot
     */
    @Nullable
    @Override
    public ItemStack getItem() {
        return this.menu.getHandle().getItem(this.id);
    }

    /**
     * Gets if this slot has an item
     *
     * @return true if this slot has an item
     */
    @Override
    public boolean hasItem() {
        return getItem() != null;
    }

    /**
     * Sets the item in this slot
     *
     * @param item the new item
     * @return this slot
     */
    @NonNull
    @Override
    public Slot setItem(@NonNull ItemStack item) {
        Objects.requireNonNull(item, "item");
        this.menu.getHandle().setItem(this.id, item);
        return this;
    }

    /**
     * Allows players to click and move items in the slot
     */
    @Override
    public void dontCancelClicks() {
        this.cancelClicks = false;
    }

    /**
     * Gets if players are allowed to click and move items in the slot
     *
     * @return if players are allowed to click and move items in the slot
     */
    @Override
    public boolean isClicksCancelled() {
        return this.cancelClicks;
    }

    /**
     * Clears all attributes of the slot.
     *
     * @return this slot
     */
    @Override
    public Slot clear() {
        clearItem();
        clearBindings();
        return this;
    }

    /**
     * Clears the item in this slot
     *
     * @return this slot
     */
    @NonNull
    @Override
    public Slot clearItem() {
        this.menu.getHandle().clear(this.id);
        return this;
    }

    /**
     * Clears all bindings on this slot.
     *
     * @return this slot
     */
    @NonNull
    @Override
    public Slot clearBindings() {
        this.handlers.clear();
        return this;
    }

    /**
     * Clears all bindings on this slot for a given click type.
     *
     * @return this slot
     */
    @NonNull
    @Override
    public Slot clearBindings(ClickType type) {
        this.handlers.remove(type);
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull ClickType type, @NonNull Consumer<PlayerInventoryClickEvent> handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        this.handlers.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet()).add(handler);
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull ClickType type, @NonNull Runnable handler) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(handler, "handler");
        this.handlers.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet()).add(Item.transformRunnable(handler));
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull Consumer<PlayerInventoryClickEvent> handler, @NonNull ClickType... types) {
        for (ClickType type : types) {
            bind(type, handler);
        }
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull Runnable handler, @NonNull ClickType... types) {
        for (ClickType type : types) {
            bind(type, handler);
        }
        return this;
    }

    @NonNull
    @Override
    public <T extends Runnable> Slot bindAllRunnables(@NonNull Iterable<Map.Entry<ClickType, T>> handlers) {
        Objects.requireNonNull(handlers, "handlers");
        for (Map.Entry<ClickType, T> handler : handlers) {
            bind(handler.getKey(), handler.getValue());
        }
        return this;
    }

    @NonNull
    @Override
    public <T extends Consumer<PlayerInventoryClickEvent>> Slot bindAllConsumers(@NonNull Iterable<Map.Entry<ClickType, T>> handlers) {
        Objects.requireNonNull(handlers, "handlers");
        for (Map.Entry<ClickType, T> handler : handlers) {
            bind(handler.getKey(), handler.getValue());
        }
        return this;
    }

}
