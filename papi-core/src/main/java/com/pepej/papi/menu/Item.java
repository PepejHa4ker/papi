package com.pepej.papi.menu;

import com.google.common.collect.ImmutableMap;
import com.pepej.papi.events.player.PlayerInventoryClickEvent;
import com.pepej.papi.utils.Delegates;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The initial model of a clickable item in a
 */
public class Item {

    @NonNull
    public static Builder builder(@NonNull ItemStack itemStack) {
        return new Builder(itemStack);
    }

    // the click handlers for this item
    private final Map<ClickType, Consumer<PlayerInventoryClickEvent>> handlers;
    // the backing itemstack
    private final ItemStack itemStack;

    public Item(@NonNull Map<ClickType, Consumer<PlayerInventoryClickEvent>> handlers, @NonNull ItemStack itemStack) {
        Objects.requireNonNull(handlers, "handlers");
        Objects.requireNonNull(itemStack, "itemStack");
        this.handlers = ImmutableMap.copyOf(handlers);
        this.itemStack = itemStack;
    }

    /**
     * Gets the click handlers for this Item.
     *
     * @return the click handlers
     */
    @NonNull
    public Map<ClickType, Consumer<PlayerInventoryClickEvent>> getHandlers() {
        return this.handlers;
    }

    /**
     * Gets the ItemStack backing this Item.
     *
     * @return the backing itemstack
     */
    @NonNull
    public ItemStack getItemStack() {
        return this.itemStack;
    }


    /**
     * Aids creation of {@link Item} instances.
     */
    public static final class Builder {
        private final ItemStack itemStack;
        private final Map<ClickType, Consumer<PlayerInventoryClickEvent>> handlers;

        private Builder(@NonNull ItemStack itemStack) {
            this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
            this.handlers = new HashMap<>();
        }

        @NonNull
        public Builder bind(@NonNull ClickType type, @Nullable Consumer<PlayerInventoryClickEvent> handler) {
            Objects.requireNonNull(type, "type");
            if (handler != null) {
                this.handlers.put(type, handler);
            }
            else {
                this.handlers.remove(type);
            }
            return this;
        }

        @NonNull
        public Builder bind(@NonNull ClickType type, @Nullable Runnable handler) {
            Objects.requireNonNull(type, "type");
            if (handler != null) {
                this.handlers.put(type, transformRunnable(handler));
            }
            else {
                this.handlers.remove(type);
            }
            return this;
        }

        @NonNull
        public Builder bind(@Nullable Consumer<PlayerInventoryClickEvent> handler, @NonNull ClickType... types) {
            for (ClickType type : types) {
                bind(type, handler);
            }
            return this;
        }

        @NonNull
        public Builder bind(@Nullable Runnable handler, @NonNull ClickType... types) {
            for (ClickType type : types) {
                bind(type, handler);
            }
            return this;
        }

        @NonNull
        public <T extends Runnable> Builder bindAllRunnables(@NonNull Iterable<Map.Entry<ClickType, T>> handlers) {
            Objects.requireNonNull(handlers, "handlers");
            for (Map.Entry<ClickType, T> handler : handlers) {
                bind(handler.getKey(), handler.getValue());
            }
            return this;
        }

        @NonNull
        public <T extends Consumer<PlayerInventoryClickEvent>> Builder bindAllConsumers(@NonNull Iterable<Map.Entry<ClickType, T>> handlers) {
            Objects.requireNonNull(handlers, "handlers");
            for (Map.Entry<ClickType, T> handler : handlers) {
                bind(handler.getKey(), handler.getValue());
            }
            return this;
        }

        @NonNull
        public Item build() {
            return new Item(this.handlers, this.itemStack);
        }
    }

    @NonNull
    public static Consumer<PlayerInventoryClickEvent> transformRunnable(@NonNull Runnable runnable) {
        return Delegates.runnableToConsumer(runnable);
    }
}
