package com.pepej.papi.menu.scheme;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.pepej.papi.menu.Menu;
import com.pepej.papi.menu.Item;
import com.pepej.papi.menu.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * A utility to help place items into a {@link Menu}
 */
class MenuPopulatorImpl implements MenuPopulator {

    private final Menu menu;
    private final ImmutableList<Integer> slots;
    protected List<Integer> remainingSlots;

    MenuPopulatorImpl(Menu menu, MenuScheme scheme) {
        Objects.requireNonNull(menu, "menu");
        Objects.requireNonNull(scheme, "scheme");
        this.remainingSlots = scheme.getMaskedIndexes();
        Preconditions.checkArgument(remainingSlots.size() > 0, "no slots in scheme");

        this.menu = menu;
        this.slots = ImmutableList.copyOf(remainingSlots);
    }

    MenuPopulatorImpl(Menu menu, List<Integer> slots) {
        Objects.requireNonNull(menu, "menu");
        Objects.requireNonNull(slots, "slots");
        Preconditions.checkArgument(slots.size() > 0, "no slots in list");
        this.menu = menu;
        this.slots = ImmutableList.copyOf(slots);
        reset();
    }

    private MenuPopulatorImpl(MenuPopulator other) {
        this.menu = other.getHandledMenu();
        this.slots = other.getSlots();
        reset();
    }

    /**
     * Gets an immutable copy of the slots used by this populator.
     *
     * @return the slots used by this populator.
     */
    @Override
    public @NotNull ImmutableList<Integer> getSlots() {
        return slots;
    }

    @Override
    public @NotNull Menu getHandledMenu() {
        return menu;
    }

    /**
     * Resets the slot order used by this populator to the state it was in upon construction
     */
    @Override
    public void reset() {
        this.remainingSlots = new LinkedList<>(slots);
    }

    @Override
    public @NotNull List<@NotNull Integer> getRemainingSlots() {
        return remainingSlots;
    }

    @Override
    public @NotNull MenuPopulator copy() {
        return new MenuPopulatorImpl(this);
    }
}
