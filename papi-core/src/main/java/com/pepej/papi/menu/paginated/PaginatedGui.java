package com.pepej.papi.menu.paginated;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.pepej.papi.item.ItemStackBuilder;
import com.pepej.papi.menu.Gui;
import com.pepej.papi.menu.Item;
import com.pepej.papi.menu.Slot;
import com.pepej.papi.menu.scheme.MenuScheme;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Extension of {@link Gui} which automatically paginates {@link Item}s.
 */
public class PaginatedGui extends Gui {

    private final MenuScheme scheme;
    private final List<Integer> itemSlots;

    private final int nextPageSlot;
    private final int previousPageSlot;
    private final Function<PageInfo, ItemStack> nextPageItem;
    private final Function<PageInfo, ItemStack> previousPageItem;

    private List<Item> content;

    // starts at 1
    private int page;

    public PaginatedGui(Function<PaginatedGui, List<Item>> content, Player player, PaginatedGuiBuilder model) {
        super(player, model.getLines(), model.getTitle());

        this.content = ImmutableList.copyOf(content.apply(this));
        this.page = 1;

        this.scheme = model.getScheme();
        this.itemSlots = ImmutableList.copyOf(model.getItemSlots());
        this.nextPageSlot = model.getNextPageSlot();
        this.previousPageSlot = model.getPreviousPageSlot();
        this.nextPageItem = model.getNextPageItem();
        this.previousPageItem = model.getPreviousPageItem();
    }

    @Override
    public void redraw() {
        this.scheme.apply(this);

        // get available slots for items
        List<Integer> slots = new ArrayList<>(this.itemSlots);

        // work out the items to display on this page
        List<List<Item>> pages = Lists.partition(this.content, slots.size());

        // normalize page number
        if (this.page < 1) {
            this.page = 1;
        } else if (this.page > pages.size()) {
            this.page = Math.max(1, pages.size());
        }

        List<Item> page = pages.isEmpty() ? new ArrayList<>() : pages.get(this.page - 1);

        // place prev/next page buttons
        if (this.page == 1) {
            // can't go back further
            // remove the item if the current slot contains a previous page item type
            Slot slot = getSlot(this.previousPageSlot);
            slot.clearBindings();
            if (slot.hasItem() && slot.getItem().getType() == this.previousPageItem.apply(PageInfo.create(0, 0)).getType()) {
                slot.clearItem();
            }
        } else {
            setItem(this.previousPageSlot, ItemStackBuilder.of(this.previousPageItem.apply(PageInfo.create(this.page, pages.size())))
                                                           .build(() -> {
                                                               this.page -= 1;
                                                               redraw();
                                                           }));
        }

        if (this.page >= pages.size()) {
            // can't go forward a page
            // remove the item if the current slot contains a next page item type
            Slot slot = getSlot(this.nextPageSlot);
            slot.clearBindings();
            if (slot.hasItem() && slot.getItem().getType() == this.nextPageItem.apply(PageInfo.create(0, 0)).getType()) {
                slot.clearItem();
            }
        } else {
            setItem(this.nextPageSlot, ItemStackBuilder.of(this.nextPageItem.apply(PageInfo.create(this.page, pages.size())))
                                                       .build(() -> {
                                                           this.page += 1;
                                                           redraw();
                                                       }));
        }

        // remove previous items
        if (!isFirstDraw()) {
            slots.forEach(this::removeItem);
        }

        // place the actual items
        for (Item item : page) {
            int index = slots.remove(0);
            setItem(index, item);
        }
    }

    public void updateContent(List<Item> content) {
        this.content = ImmutableList.copyOf(content);
    }

}
