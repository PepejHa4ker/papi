package com.pepej.papi.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.pepej.papi.events.player.PlayerInventoryClickEvent;
import com.pepej.papi.menu.Item;
import com.pepej.papi.text.Text;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/**
 * Easily construct {@link ItemStack} instances
 */
public final class ItemStackBuilder {
    private static final ItemFlag[] ALL_FLAGS = new ItemFlag[]{
            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES,
            ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS,
            ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_PLACED_ON
    };

    private final ItemStack itemStack;

    public static ItemStackBuilder of(Material material) {
        return new ItemStackBuilder(new ItemStack(material)).hideAttributes();
    }

    public static ItemStackBuilder of(ItemStack itemStack) {
        return new ItemStackBuilder(itemStack).hideAttributes();
    }

    public static ItemStackBuilder of(ConfigurationSection config) {
        return ItemStackReader.DEFAULT.read(config);
    }

    public static ItemStackBuilder head(String headSignature) {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        if (headSignature.isEmpty()) {
            throw new IllegalArgumentException(("headSignature cannot be null"));
        }

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", headSignature));
        Field profileField;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return new ItemStackBuilder(head).hideAttributes();
    }

    private ItemStackBuilder(ItemStack itemStack) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }


    public ItemStackBuilder transform(Consumer<ItemStack> is) {
        is.accept(this.itemStack);
        return this;
    }

    public <T extends ItemMeta> ItemStackBuilder transformMeta(Consumer<? super T> meta) {
        //noinspection unchecked
        T m = (T) this.itemStack.getItemMeta();
        if (m != null) {
            meta.accept(m);
            this.itemStack.setItemMeta(m);
        }
        return this;
    }

    public ItemStackBuilder name(String name) {
        return transformMeta(meta -> meta.setDisplayName(Text.colorize(name)));
    }

    public ItemStackBuilder nameClickable(String name) {
        return transformMeta(meta -> meta.setDisplayName(Text.colorize(name + " &7(Клик)")));
    }

    public ItemStackBuilder type(Material material) {
        return transform(itemStack -> itemStack.setType(material));
    }

    public ItemStackBuilder loreUnique(String line) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            if (!lore.contains(Text.colorize(line))) {
                lore.add(Text.colorize(line));
                meta.setLore(lore);
            }
        });
    }

    public ItemStackBuilder loreClickable(String action) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(Text.colorize("&7Кликните, чтобы " + action));
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder loreRightClickable(String action) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(Text.colorize("&7Нажмите ПКМ, чтобы " + action));
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder loreLeftClickable(String action) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            lore.add(Text.colorize("&7Нажмите ЛКМ, чтобы " + action));
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder lore(String line) {
        return lore(new String[]{line});
    }

    public ItemStackBuilder lore(String... lines) {
        return transformMeta(meta -> {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : meta.getLore();
            for (String line : lines) {
                lore.add(Text.colorize(line));
            }
            meta.setLore(lore);
        });
    }

    public ItemStackBuilder lore(Iterable<String> lines) {
        for (String line : lines) {
            lore(line);
        }
        return this;
    }

    public ItemStackBuilder clearLore() {
        return transformMeta(meta -> meta.getLore().clear());
    }

    public ItemStackBuilder durability(int durability) {
        return transform(itemStack -> itemStack.setDurability((short) durability));
    }

    public ItemStackBuilder data(int data) {
        return durability(data);
    }

    public ItemStackBuilder amount(int amount) {
        return transform(itemStack -> itemStack.setAmount(amount));
    }

    public ItemStackBuilder enchant(Enchantment enchantment, int level) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, level));
    }

    public ItemStackBuilder enchant(Enchantment enchantment) {
        return transform(itemStack -> itemStack.addUnsafeEnchantment(enchantment, 1));
    }

    public ItemStackBuilder clearEnchantments() {
        return transform(itemStack -> itemStack.getEnchantments().keySet().forEach(itemStack::removeEnchantment));
    }

    public ItemStackBuilder flag(ItemFlag... flags) {
        return transformMeta(meta -> meta.addItemFlags(flags));
    }

    public ItemStackBuilder unflag(ItemFlag... flags) {
        return transformMeta(meta -> meta.removeItemFlags(flags));
    }

    public ItemStackBuilder hideAttributes() {
        return flag(ALL_FLAGS);
    }

    public ItemStackBuilder showAttributes() {
        return unflag(ALL_FLAGS);
    }

    public ItemStackBuilder color(Color color) {
        return transform(itemStack -> {
            if (itemStack.getItemMeta() instanceof LeatherArmorMeta) {
                this.<LeatherArmorMeta>transformMeta(m -> m.setColor(color));
            }
        });
    }

    public ItemStackBuilder breakable(boolean flag) {
        return transformMeta(meta -> meta.setUnbreakable(!flag));
    }

    public ItemStackBuilder apply(Consumer<ItemStackBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public ItemStack build() {
        return this.itemStack;
    }

    public Item.Builder buildItem() {
        return Item.builder(build());
    }

    public Item build(@Nullable Runnable handler) {
        return buildItem().bind(handler, ClickType.RIGHT, ClickType.LEFT).build();
    }

    public Item build(ClickType type, @Nullable Runnable handler) {
        return buildItem().bind(type, handler).build();
    }

    public Item build(@Nullable Runnable rightClick, @Nullable Runnable leftClick) {
        return buildItem().bind(ClickType.RIGHT, rightClick).bind(ClickType.LEFT, leftClick).build();
    }

    public Item buildFromMap(Map<ClickType, Runnable> handlers) {
        return buildItem().bindAllRunnables(handlers.entrySet()).build();
    }

    public Item buildConsumer(@Nullable Consumer<PlayerInventoryClickEvent> handler) {
        return buildItem().bind(handler, ClickType.RIGHT, ClickType.LEFT).build();
    }

    public Item buildConsumer(ClickType type, @Nullable Consumer<PlayerInventoryClickEvent> handler) {
        return buildItem().bind(type, handler).build();
    }

    public Item buildConsumer(@Nullable Consumer<PlayerInventoryClickEvent> rightClick, @Nullable Consumer<PlayerInventoryClickEvent> leftClick) {
        return buildItem().bind(ClickType.RIGHT, rightClick).bind(ClickType.LEFT, leftClick).build();
    }

    public Item buildFromConsumerMap(Map<ClickType, Consumer<PlayerInventoryClickEvent>> handlers) {
        return buildItem().bindAllConsumers(handlers.entrySet()).build();
    }

}