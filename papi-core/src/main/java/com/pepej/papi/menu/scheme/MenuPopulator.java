package com.pepej.papi.menu.scheme;

import com.google.common.collect.ImmutableList;
import com.pepej.papi.menu.Item;
import com.pepej.papi.menu.Menu;
import com.pepej.papi.menu.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface MenuPopulator {

    @NotNull
    static MenuPopulator create(@NotNull Menu menu, @NotNull MenuScheme scheme) {
        return new MenuPopulatorImpl(menu, scheme);
    }

    @NotNull
    static MenuPopulator create(@NotNull Menu menu, @NotNull List<Integer> slots) {
        return new MenuPopulatorImpl(menu, slots);
    }

    /**
     * Gets an immutable copy of the slots used by this populator.
     *
     * @return the slots used by this populator.
     */
    @NotNull
    ImmutableList<Integer> getSlots();

    @NotNull
    Menu getHandledMenu();

    /**
     * Resets the slot order used by this populator to the state it was in upon construction
     */
    void reset();

    @NotNull
    List<@NotNull Integer> getRemainingSlots();

    @NotNull
    MenuPopulator copy();

    /**
     * Gets if there is any space left in this populator
     *
     * @return if there is more space
     */
    default boolean hasSpace() {
        return !getRemainingSlots().isEmpty();
    }

    default MenuPopulator consume(Consumer<Slot> action) {
        if (tryConsume(action)) {
            return this;
        } else {
            throw new IllegalStateException("No more slots");
        }
    }

    default MenuPopulator consumeIfSpace(Consumer<Slot> action) {
        tryConsume(action);
        return this;
    }

    default boolean tryConsume(@NotNull Consumer<Slot> action) {
        Objects.requireNonNull(action, "action");
        if (getRemainingSlots().isEmpty()) {
            return false;
        }

        int slot = getRemainingSlots().remove(0);
        action.accept(getHandledMenu().getSlot(slot));
        return true;
    }
    /**
     * Places an item onto the {@link Menu} using the next available slot in the populator
     *
     * @param item the item to place
     * @return the populator
     * @throws IllegalStateException if there are not more slots
     */
    default MenuPopulator accept(@NotNull Item item) {
        return consume(s -> s.applyFromItem(item));
    }

    /**
     * Places an item onto the {@link Menu} using the next available slot in the populator
     *
     * @param item the item to place
     * @return the populator
     */
    default MenuPopulator acceptIfSpace(@NotNull Item item) {
        return consumeIfSpace(s -> s.applyFromItem(item));
    }

    /**
     * Places an item onto the {@link Menu} using the next available slot in the populator
     *
     * @param item the item to place
     * @return true if there was a slot left in the populator to place this item onto, false otherwise
     */
    default boolean placeIfSpace(@NotNull Item item) {
        return tryConsume(s -> s.applyFromItem(item));
    }


    /**
     * Gets the number of remaining slots in the populator.
     *
     * @return the number of remaining slots
     */
    default int getRemainingSpace() {
        return getRemainingSlots().size();
    }

}
