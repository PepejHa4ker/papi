package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagFloat")
public interface NBTTagFloat extends Shadow, NBTBase, NBTNumber {

    static NBTTagFloat create(float data) {
        return ShadowFactory.global().constructShadow(NBTTagFloat.class, data);
    }

}
