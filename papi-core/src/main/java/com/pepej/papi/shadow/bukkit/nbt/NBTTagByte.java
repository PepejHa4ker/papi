package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagByte")
public interface NBTTagByte extends Shadow, NBTBase, NBTNumber {

    static NBTTagByte create(byte data) {
        return ShadowFactory.global().constructShadow(NBTTagByte.class, data);
    }

}
