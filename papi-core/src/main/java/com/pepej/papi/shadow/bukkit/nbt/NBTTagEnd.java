package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagEnd")
public interface NBTTagEnd extends Shadow, NBTBase {

    static NBTTagEnd create() {
        return ShadowFactory.global().constructShadow(NBTTagEnd.class);
    }

}
