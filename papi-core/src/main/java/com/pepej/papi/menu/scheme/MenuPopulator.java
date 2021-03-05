package com.pepej.papi.menu.scheme;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.pepej.papi.menu.Menu;
import com.pepej.papi.menu.Item;
import com.pepej.papi.menu.Slot;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * A utility to help place items into a {@link Menu}
 */
public class MenuPopulator {

    private final Menu menu;
    private final ImmutableList<Integer> slots;
    protected List<Integer> remainingSlots;

    public MenuPopulator(Menu menu, MenuScheme scheme) {
        Objects.requireNonNull(menu, "menu");
        Objects.requireNonNull(scheme, "scheme");

        this.remainingSlots = scheme.getMaskedIndexes();
        Preconditions.checkArgument(remainingSlots.size() > 0, "no slots in scheme");

        this.menu = menu;
        this.slots = ImmutableList.copyOf(remainingSlots);
    }

    public MenuPopulator(Menu menu, List<Integer> slots) {
        Objects.requireNonNull(menu, "menu");
        Objects.requireNonNull(slots, "slots");

        Preconditions.checkArgument(slots.size() > 0, "no slots in list");

        this.menu = menu;
        this.slots = ImmutableList.copyOf(slots);
        reset();
    }

    private MenuPopulator(MenuPopulator other) {
        this.menu = other.menu;
        this.slots = other.slots;
        reset();
    }

    /**
     * Gets an immutable copy of the slots used by this populator.
     *
     * @return the slots used by this populator.
     */
    public ImmutableList<Integer> getSlots() {
        return slots;
    }

    /**
     * Resets the slot order used by this populator to the state it was in upon construction
     */
    public void reset() {
        this.remainingSlots = new LinkedList<>(slots);
    }

    public MenuPopulator consume(Consumer<Slot> action) {
        if (tryConsume(action)) {
            return this;
        } else {
            throw new IllegalStateException("No more slots");
        }
    }

    public MenuPopulator consumeIfSpace(Consumer<Slot> action) {
        tryConsume(action);
        return this;
    }

    public boolean tryConsume(Consumer<Slot> action) {
        Objects.requireNonNull(action, "action");
        if (remainingSlots.isEmpty()) {
            return false;
        }

        int slot = remainingSlots.remove(0);
        action.accept(menu.getSlot(slot));
        return true;
    }

    /**
     * Places an item onto the {@link Menu} using the next available slot in the populator
     *
     * @param item the item to place
     * @return the populator
     * @throws IllegalStateException if there are not more slots
     */
    public MenuPopulator accept(Item item) {
        return consume(s -> s.applyFromItem(item));
    }

    /**
     * Places an item onto the {@link Menu} using the next available slot in the populator
     *
     * @param item the item to place
     * @return the populator
     */
    public MenuPopulator acceptIfSpace(Item item) {
        return consumeIfSpace(s -> s.applyFromItem(item));
    }

    /**
     * Places an item onto the {@link Menu} using the next available slot in the populator
     *
     * @param item the item to place
     * @return true if there was a slot left in the populator to place this item onto, false otherwise
     */
    public boolean placeIfSpace(Item item) {
        return tryConsume(s -> s.applyFromItem(item));
    }

    /**
     * Gets the number of remaining slots in the populator.
     *
     * @return the number of remaining slots
     */
    public int getRemainingSpace() {
        return remainingSlots.size();
    }

    /**
     * Gets if there is any space left in this populator
     *
     * @return if there is more space
     */
    public boolean hasSpace() {
        return !remainingSlots.isEmpty();
    }

    public MenuPopulator copy() {
        return new MenuPopulator(this);
    }
}
