package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagInt")
public interface NBTTagInt extends Shadow, NBTBase, NBTNumber {

    static NBTTagInt create(int data) {
        return ShadowFactory.global().constructShadow(NBTTagInt.class, data);
    }

}