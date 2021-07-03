package com.pepej.papi.menu;

import com.pepej.papi.events.player.PlayerInventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a slot in a {@link Menu}.
 *
 * All changes made to this object are applied to the backing Menu instance, and vice versa.
 */
public interface Slot {

    void handle(@NonNull PlayerInventoryClickEvent event);

    /**
     * Gets the GUI this slot references
     *
     * @return the parent gui
     */
    @NonNull
    Menu menu();

    /**
     * Gets the id of this slot
     *
     * @return the id
     */
    int getId();

    /**
     * Applies an item model to this slot.
     *
     * @param item the item
     * @return this slot
     */
    Slot applyFromItem(Item item);

    /**
     * Gets the item in this slot
     *
     * @return the item in this slot
     */
    @Nullable
    ItemStack getItem();

    /**
     * Gets if this slot has an item
     *
     * @return true if this slot has an item
     */
    boolean hasItem();

    /**
     * Sets the item in this slot
     *
     * @param item the new item
     * @return this slot
     */
    @NonNull
    Slot setItem(@NonNull ItemStack item);

    /**
     * Allows players to click and move items in the slot
     */
    void dontCancelClicks();

    /**
     * Gets if players are allowed to click and move items in the slot
     * @return if players are allowed to click and move items in the slot
     */
    boolean isClicksCancelled();

    /**
     * Clears all attributes of the slot.
     *
     * @return this slot
     */
    Slot clear();

    /**
     * Clears the item in this slot
     *
     * @return this slot
     */
    @NonNull
    Slot clearItem();

    /**
     * Clears all bindings on this slot.
     *
     * @return this slot
     */
    @NonNull
    Slot clearBindings();


    /**
     * Clears all bindings on this slot for a given click type.
     * @param type the click type
     * @return this slot
     */
    @NonNull
    Slot clearBindings(ClickType type);

    @NonNull
    Slot bind(@NonNull ClickType type, @NonNull Consumer<PlayerInventoryClickEvent> handler);

    @NonNull
    Slot bind(@NonNull ClickType type, @NonNull Runnable handler);

    @NonNull
    Slot bind(@NonNull Consumer<PlayerInventoryClickEvent> handler, @NonNull ClickType... types);

    @NonNull
    Slot bind(@NonNull Runnable handler, @NonNull ClickType... types);

    @NonNull
    <T extends Runnable> Slot bindAllRunnables(@NonNull Iterable<Map.Entry<ClickType, T>> handlers);

    @NonNull
    <T extends Consumer<PlayerInventoryClickEvent>> Slot bindAllConsumers(@NonNull Iterable<Map.Entry<ClickType, T>> handlers);

}
