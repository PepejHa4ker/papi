package com.pepej.papi.shadow.bukkit.itemstack;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.Static;
import com.pepej.papi.shadow.bukkit.ObcClassTarget;
import org.bukkit.inventory.ItemStack;

@ObcClassTarget("inventory.CraftItemStack")
public interface CraftItemStackShadow extends Shadow {

    @Static
    NMSItemStackShadow asNMSCopy(ItemStack item);

    @Static
    ItemStack asBukkitCopy(NMSItemStackShadow original);
}
