package com.pepej.papi.menu;

import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Represents a slot in a {@link Gui}.
 *
 * All changes made to this object are applied to the backing Gui instance, and vice versa.
 */
public interface Slot {

    /**
     * Gets the GUI this slot references
     *
     * @return the parent gui
     */
    @Nonnull
    Gui gui();

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
    @Nonnull
    Slot setItem(@Nonnull ItemStack item);

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
    @Nonnull
    Slot clearItem();

    /**
     * Clears all bindings on this slot.
     *
     * @return this slot
     */
    @Nonnull
    Slot clearBindings();

    /**
     * Clears all bindings on this slot for a given click type.
     *
     * @return this slot
     */
    @Nonnull
    Slot clearBindings(ClickType type);

    @Nonnull
    Slot bind(@Nonnull ClickType type, @Nonnull Consumer<InventoryClickEvent> handler);

    @Nonnull
    Slot bind(@Nonnull ClickType type, @Nonnull Runnable handler);

    @Nonnull
    Slot bind(@Nonnull Consumer<InventoryClickEvent> handler, @Nonnull ClickType... types);

    @Nonnull
    Slot bind(@Nonnull Runnable handler, @Nonnull ClickType... types);

    @Nonnull
    <T extends Runnable> Slot bindAllRunnables(@Nonnull Iterable<Map.Entry<ClickType, T>> handlers);

    @Nonnull
    <T extends Consumer<InventoryClickEvent>> Slot bindAllConsumers(@Nonnull Iterable<Map.Entry<ClickType, T>> handlers);

}
