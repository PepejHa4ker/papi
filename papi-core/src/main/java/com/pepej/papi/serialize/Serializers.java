package com.pepej.papi.serialize;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.pepej.papi.gson.JsonBuilder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Utility methods for converting ItemStacks and Inventories to and from JSON.
 */
public final class Serializers {

    public static JsonPrimitive serializeItemstack(@NonNull ItemStack item) {
        return JsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStackToString(item));
    }

    public static ItemStack deserializeItemstack(@NonNull JsonElement data) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeItemStack(data.getAsString());
    }

    public static JsonPrimitive serializeItemstacks(@NonNull ItemStack[] items) {
        return JsonBuilder.primitiveNonNull(InventorySerialization.encodeItemStacksToString(items));
    }

    public static JsonPrimitive serializeInventory(@NonNull Inventory inventory) {
        return JsonBuilder.primitiveNonNull(InventorySerialization.encodeInventoryToString(inventory));
    }

    public static ItemStack[] deserializeItemstacks(@NonNull JsonElement data) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeItemStacks(data.getAsString());
    }

    public static Inventory deserializeInventory(@NonNull JsonElement data, @NonNull String title) {
        Preconditions.checkArgument(data.isJsonPrimitive());
        return InventorySerialization.decodeInventory(data.getAsString(), title);
    }

    private Serializers() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
}