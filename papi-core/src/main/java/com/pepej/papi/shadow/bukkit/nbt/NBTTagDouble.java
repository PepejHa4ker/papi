package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagDouble")
public interface NBTTagDouble extends Shadow, NBTBase, NBTNumber {

    static NBTTagDouble create(double data) {
        return ShadowFactory.global().constructShadow(NBTTagDouble.class, data);
    }

}
