package com.pepej.papi.menu;

import com.google.common.base.Preconditions;
import com.pepej.papi.events.Events;
import com.pepej.papi.menu.SimpleSlot;
import com.pepej.papi.metadata.Metadata;
import com.pepej.papi.metadata.MetadataKey;
import com.pepej.papi.metadata.MetadataMap;
import com.pepej.papi.reflect.MinecraftVersion;
import com.pepej.papi.reflect.MinecraftVersions;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * A simple GUI abstraction
 */
public abstract class Gui implements TerminableConsumer {
    public static final MetadataKey<Gui> OPEN_GUI_KEY = MetadataKey.create("open-gui", Gui.class);

    /**
     * Utility method to get the number of lines needed for x items
     *
     * @param count the number of items
     * @return the number of lines needed
     */
    public static int getMenuSize(int count) {
        Preconditions.checkArgument(count >= 0, "count < 0");
        return getMenuSize(count, 9);
    }

    /**
     * Utility method to get the number of lines needed for x items
     *
     * @param count the number of items
     * @param itemsPerLine the number of items per line
     * @return the number of lines needed
     */
    public static int getMenuSize(int count, int itemsPerLine) {
        Preconditions.checkArgument(itemsPerLine >= 1, "itemsPerLine < 1");
        return (count / itemsPerLine + ((count % itemsPerLine != 0) ? 1 : 0));
    }

    // The player holding the GUI
    private final Player player;
    // The backing inventory instance
    private final Inventory inventory;
    // The initial title set when the inventory was made.
    private final String initialTitle;
    // The slots in the gui, lazily loaded
    private final Map<Integer, SimpleSlot> slots;
    // This remains true until after #redraw is called for the first time
    private boolean firstDraw = true;
    // A function used to build a fallback page when this page is closed.
    @Nullable
    private Function<Player, Gui> fallbackGui = null;

    // Callbacks to be ran when the GUI is invalidated (closed). useful for cancelling tick tasks
    // Also contains the event handlers bound to this GUI, currently listening to events
    private final CompositeTerminable compositeTerminable = CompositeTerminable.create();

    private boolean valid = false;
    private boolean invalidated = false;

    public Gui(Player player, int lines, String title) {
        this.player = Objects.requireNonNull(player, "player");
        this.initialTitle = Text.colorize(Objects.requireNonNull(title, "title"));
        this.inventory = Bukkit.createInventory(player, lines * 9, this.initialTitle);
        this.slots = new HashMap<>();
    }

    /**
     * Places items on the GUI. Called when the GUI is opened.
     * Use {@link #isFirstDraw()} to determine if this is the first time redraw has been called.
     */
    public abstract void redraw();

    /**
     * Gets the player viewing this Gui
     *
     * @return the player viewing this gui
     */
    public Player getPlayer() {
        return this.player;
    }

    /**
     * Gets the delegate Bukkit inventory
     *
     * @return the bukkit inventory being wrapped by this instance
     */
    public Inventory getHandle() {
        return this.inventory;
    }

    /**
     * Gets the initial title which was set when this GUI was made
     *
     * @return the initial title used when this GUI was made
     */
    public String getInitialTitle() {
        return this.initialTitle;
    }

    @Nullable
    public Function<Player, Gui> getFallbackGui() {
        return this.fallbackGui;
    }

    public void setFallbackGui(@Nullable Function<Player, Gui> fallbackGui) {
        this.fallbackGui = fallbackGui;
    }

    @NonNull
    @Override
    public <T extends AutoCloseable> T bind(@NonNull T terminable) {
        return this.compositeTerminable.bind(terminable);
    }

    public boolean isFirstDraw()
    {
        return this.firstDraw;
    }
    public Slot getSlot(int slot) {
        if (slot < 0 || slot >= this.inventory.getSize()) {
            throw new IllegalArgumentException("Invalid slot id: " + slot);
        }

        if (invalidated) {
            return new DummySlot(this, slot);
        }

        return this.slots.computeIfAbsent(slot, i -> new SimpleSlot(this, i));
    }

    public void setItem(int slot, Item item) {
        getSlot(slot).applyFromItem(item);
    }

    public void setItems(Item item, int... slots) {
        Objects.requireNonNull(item, "item");
        for (int slot : slots) {
            setItem(slot, item);
        }
    }

    public void setItems(Iterable<Integer> slots, Item item) {
        Objects.requireNonNull(item, "item");
        Objects.requireNonNull(slots, "slots");
        for (int slot : slots) {
            setItem(slot, item);
        }
    }

    public int getFirstEmpty() {
        int ret = this.inventory.firstEmpty();
        if (ret < 0) {
            throw new IndexOutOfBoundsException("no empty slots");
        }
        return ret;
    }

    public Optional<Slot> getFirstEmptySlot() {
        int ret = this.inventory.firstEmpty();
        if (ret < 0) {
            return Optional.empty();
        }
        return Optional.of(getSlot(ret));
    }

    public void addItem(Item item) {
        Objects.requireNonNull(item, "item");
        getFirstEmptySlot().ifPresent(s -> s.applyFromItem(item));
    }

    public void addItems(Iterable<Item> items) {
        Objects.requireNonNull(items, "items");
        for (Item item : items) {
            addItem(item);
        }
    }

    public void fillWith(Item item) {
        Objects.requireNonNull(item, "item");
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            setItem(i, item);
        }
    }

    public void removeItem(int slot) {
        getSlot(slot).clear();
    }

    public void removeItems(int... slots) {
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    public void removeItems(Iterable<Integer> slots) {
        Objects.requireNonNull(slots, "slots");
        for (int slot : slots) {
            removeItem(slot);
        }
    }

    public void clearItems() {
        this.inventory.clear();
        this.slots.values().forEach(Slot::clearBindings);
    }

    public void open() {
        if (MinecraftVersion.getRuntimeVersion().isAfterOrEq(MinecraftVersions.v1_16)) {
            // delay by a tick in 1.16+ to prevent an unwanted PlayerInteractEvent interfering with inventory clicks
            Schedulers.sync().runLater(() -> {
                if (!this.player.isOnline()) {
                    return;
                }
                handleOpen();
            }, 1);
        } else {
            handleOpen();
        }
    }

    private void handleOpen() {
        if (this.valid) {
            throw new IllegalStateException("Gui is already opened.");
        }
        this.firstDraw = true;
        this.invalidated = false;
        try {
            redraw();
        } catch (Exception e) {
            e.printStackTrace();
            invalidate();
            return;
        }

        this.firstDraw = false;
        startListening();
        this.player.openInventory(this.inventory);
        Metadata.provideForPlayer(this.player).put(OPEN_GUI_KEY, this);
        this.valid = true;
    }

    public void close() {
        this.player.closeInventory();
    }

    private void invalidate() {
        this.valid = false;
        this.invalidated = true;

        MetadataMap metadataMap = Metadata.provideForPlayer(this.player);
        Gui existing = metadataMap.getOrNull(OPEN_GUI_KEY);
        if (existing == this) {
            metadataMap.remove(OPEN_GUI_KEY);
        }

        // stop listening
        this.compositeTerminable.closeAndReportException();

        // clear all items from the GUI, just in case the menu didn't close properly.
        clearItems();
    }

    /**
     * Returns true unless this GUI has been invalidated, through being closed, or the player leaving.
     * @return true unless this GUI has been invalidated.
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Registers the event handlers for this GUI
     */
    private void startListening() {
        Events.merge(Player.class)
              .bindEvent(PlayerDeathEvent.class, PlayerDeathEvent::getEntity)
              .bindEvent(PlayerQuitEvent.class, PlayerEvent::getPlayer)
              .bindEvent(PlayerChangedWorldEvent.class, PlayerEvent::getPlayer)
              .bindEvent(PlayerTeleportEvent.class, PlayerEvent::getPlayer)
              .filter(p -> p.equals(this.player))
              .filter(p -> isValid())
              .handler(p -> invalidate())
              .bindWith(this);

        Events.subscribe(InventoryDragEvent.class)
              .filter(e -> e.getInventory().getHolder() != null)
              .filter(e -> e.getInventory().getHolder().equals(this.player))
              .handler(e -> {
                  e.setCancelled(true);
                  if (!isValid()) {
                      close();
                  }
              }).bindWith(this);

        Events.subscribe(InventoryClickEvent.class)
              .filter(e -> e.getInventory().getHolder() != null)
              .filter(e -> e.getInventory().getHolder().equals(this.player))
              .handler(e -> {
                  e.setCancelled(true);

                  if (!isValid()) {
                      close();
                      return;
                  }

                  if (!e.getInventory().equals(this.inventory)) {
                      return;
                  }

                  int slotId = e.getRawSlot();

                  // check if the click was in the top inventory
                  if (slotId != e.getSlot()) {
                      return;
                  }

                  SimpleSlot slot = this.slots.get(slotId);
                  if (slot != null) {
                      slot.handle(e);
                  }
              })
              .bindWith(this);

        Events.subscribe(InventoryOpenEvent.class)
              .filter(e -> e.getPlayer().equals(this.player))
              .filter(e -> !e.getInventory().equals(this.inventory))
              .filter(e -> isValid())
              .handler(e -> invalidate())
              .bindWith(this);

        Events.subscribe(InventoryCloseEvent.class)
              .filter(e -> e.getPlayer().equals(this.player))
              .filter(e -> isValid())
              .handler(e -> {
                  invalidate();

                  if (!e.getInventory().equals(this.inventory)) {
                      return;
                  }

                  // Check for a fallback GUI
                  Function<Player, Gui> fallback = this.fallbackGui;
                  if (fallback == null) {
                      return;
                  }

                  // Open at a delay
                  Schedulers.sync().runLater(() -> {
                      if (!this.player.isOnline()) {
                          return;
                      }
                      Gui fallbackGui = fallback.apply(this.player);
                      if (fallbackGui == null) {
                          throw new IllegalStateException("Fallback function " + fallback + " returned null");
                      }
                      if (fallbackGui.valid) {
                          throw new IllegalStateException("Fallback function " + fallback + " produced a GUI " + fallbackGui + " which is already open");
                      }
                      fallbackGui.open();

                  }, 1L);
              })
              .bindWith(this);
    }

}
