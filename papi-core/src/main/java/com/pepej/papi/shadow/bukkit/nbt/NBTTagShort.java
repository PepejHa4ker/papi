package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagShort")
public interface NBTTagShort extends Shadow, NBTBase, NBTNumber {

    static NBTTagShort create(short data) {
        return ShadowFactory.global().constructShadow(NBTTagShort.class, data);
    }

}
