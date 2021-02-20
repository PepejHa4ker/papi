package com.pepej.papi.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    /**
     * Gets the GUI this slot references
     *
     * @return the parent gui
     */
    @NonNull
    Menu gui();

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
     *
     * @return this slot
     */
    @NonNull
    Slot clearBindings(ClickType type);

    @NonNull
    Slot bind(@NonNull ClickType type, @NonNull Consumer<InventoryClickEvent> handler);

    @NonNull
    Slot bind(@NonNull ClickType type, @NonNull Runnable handler);

    @NonNull
    Slot bind(@NonNull Consumer<InventoryClickEvent> handler, @NonNull ClickType... types);

    @NonNull
    Slot bind(@NonNull Runnable handler, @NonNull ClickType... types);

    @NonNull
    <T extends Runnable> Slot bindAllRunnables(@NonNull Iterable<Map.Entry<ClickType, T>> handlers);

    @NonNull
    <T extends Consumer<InventoryClickEvent>> Slot bindAllConsumers(@NonNull Iterable<Map.Entry<ClickType, T>> handlers);

}
