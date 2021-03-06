package com.pepej.papi.menu;

import com.pepej.papi.events.player.PlayerInventoryClickEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Useless implementation of {@link Slot} to fulfill not-null contracts.
 */
public class DummySlot implements Slot {

    // the parent menu
    private final Menu menu;

    // the id of this slot
    private final int id;

    public DummySlot(@NonNull Menu menu, int id) {
        this.menu = menu;
        this.id = id;
    }

    @Override
    public void handle(@NonNull final PlayerInventoryClickEvent event) {

    }

    @NonNull
    @Override
    public Menu menu() {
        return this.menu;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public Slot applyFromItem(Item item) {
        return this;
    }

    @Nullable
    @Override
    public ItemStack getItem() {
        return null;
    }

    @Override
    public boolean hasItem() {
        return false;
    }

    @NonNull
    @Override
    public Slot setItem(@NonNull ItemStack item) {
        return this;
    }

    @Override
    public void dontCancelClicks() {

    }

    @Override
    public boolean isClicksCancelled() {
        return false;
    }

    @Override
    public Slot clear() {
        return this;
    }

    @NonNull
    @Override
    public Slot clearItem() {
        return this;
    }

    @NonNull
    @Override
    public Slot clearBindings() {
        return this;
    }

    @NonNull
    @Override
    public Slot clearBindings(ClickType type) {
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull ClickType type, @NonNull Consumer<PlayerInventoryClickEvent> handler) {
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull ClickType type, @NonNull Runnable handler) {
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull Consumer<PlayerInventoryClickEvent> handler, @NonNull ClickType... types) {
        return this;
    }

    @NonNull
    @Override
    public Slot bind(@NonNull Runnable handler, @NonNull ClickType... types) {
        return this;
    }

    @NonNull
    @Override
    public <T extends Runnable> Slot bindAllRunnables(@NonNull Iterable<Map.Entry<ClickType, T>> handlers) {
        return this;
    }

    @NonNull
    @Override
    public <T extends Consumer<PlayerInventoryClickEvent>> Slot bindAllConsumers(@NonNull Iterable<Map.Entry<ClickType, T>> handlers) {
        return this;
    }
}
