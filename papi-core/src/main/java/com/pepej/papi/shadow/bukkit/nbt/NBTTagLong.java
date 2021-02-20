package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagLong")
public interface NBTTagLong extends Shadow, NBTBase, NBTNumber {

    static NBTTagLong create(long data) {
        return ShadowFactory.global().constructShadow(NBTTagLong.class, data);
    }

}
