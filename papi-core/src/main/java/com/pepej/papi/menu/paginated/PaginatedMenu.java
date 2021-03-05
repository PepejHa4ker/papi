package com.pepej.papi.menu.paginated;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.pepej.papi.item.ItemStackBuilder;
import com.pepej.papi.menu.Menu;
import com.pepej.papi.menu.Item;
import com.pepej.papi.menu.Slot;
import com.pepej.papi.menu.scheme.MenuScheme;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Extension of {@link Menu} which automatically paginates {@link Item}s.
 */
public class PaginatedMenu extends Menu {

    private final MenuScheme scheme;
    private final List<Integer> itemSlots;

    private final int nextPageSlot;
    private final int previousPageSlot;
    private final Function<PageInfo, ItemStack> nextPageItem;
    private final Function<PageInfo, ItemStack> previousPageItem;

    private List<Item> content;

    // starts at 1
    private int page;

    public PaginatedMenu(Function<PaginatedMenu, List<Item>> content, Player player, PaginatedMenuBuilder model) {
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
        scheme.apply(this);

        // get available slots for items
        List<Integer> slots = new ArrayList<>(itemSlots);

        // work out the items to display on this page
        List<List<Item>> pages = Lists.partition(content, slots.size());

        // normalize page number
        if (page < 1) {
            page = 1;
        } else if (page > pages.size()) {
            page = Math.max(1, pages.size());
        }

        List<Item> currentPage = pages.isEmpty() ? new ArrayList<>() : pages.get(page - 1);

        // place prev/next page buttons
        if (this.page == 1) {
            // can't go back further
            // remove the item if the current slot contains a previous page item type
            Slot slot = getSlot(this.previousPageSlot);
            slot.clearBindings();
            if (slot.getItem() != null && slot.getItem().getType() == previousPageItem.apply(PageInfo.create(0, 0)).getType()) {
                slot.clearItem();
            }
        } else {
            setItem(this.previousPageSlot, ItemStackBuilder.of(previousPageItem.apply(PageInfo.create(page, pages.size())))
                                                           .build(() -> {
                                                               page -= 1;
                                                               redraw();
                                                           }));
        }

        if (this.page >= pages.size()) {
            // can't go forward a page
            // remove the item if the current slot contains a next page item type
            Slot slot = getSlot(nextPageSlot);
            slot.clearBindings();
            if (slot.getItem() != null && slot.getItem().getType() == nextPageItem.apply(PageInfo.create(0, 0)).getType()) {
                slot.clearItem();
            }
        } else {
            setItem(this.nextPageSlot, ItemStackBuilder.of(nextPageItem.apply(PageInfo.create(this.page, pages.size())))
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
        for (Item item : currentPage) {
            int index = slots.remove(0);
            setItem(index, item);
        }
    }

    public void updateContent(List<Item> content) {
        this.content = ImmutableList.copyOf(content);
        redraw();
    }

}
