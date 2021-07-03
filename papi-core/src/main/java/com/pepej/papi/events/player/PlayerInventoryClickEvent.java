package com.pepej.papi.events.player;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

public class PlayerInventoryClickEvent extends InventoryClickEvent {

    public PlayerInventoryClickEvent(final InventoryView view, final InventoryType.SlotType type, final int slot, final ClickType click, final InventoryAction action) {
        super(view, type, slot, click, action);
    }

    public PlayerInventoryClickEvent(final InventoryView view, final InventoryType.SlotType type, final int slot, final ClickType click, final InventoryAction action, final int key) {
        super(view, type, slot, click, action, key);
    }

    public Player getPlayer() {
        return (Player) getWhoClicked();
    }
}
