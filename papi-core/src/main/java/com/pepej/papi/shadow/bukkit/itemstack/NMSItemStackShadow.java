package com.pepej.papi.shadow.bukkit.itemstack;

import com.pepej.papi.shadow.Field;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.nbt.NBTTagCompound;

@NmsClassTarget("ItemStack")
public interface NMSItemStackShadow extends Shadow {

    NBTTagCompound getTag();

    @Field
    void setTag(NBTTagCompound tagCompound);

}
