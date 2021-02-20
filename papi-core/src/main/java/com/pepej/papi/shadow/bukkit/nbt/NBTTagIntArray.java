package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

import java.util.List;

@NmsClassTarget("NBTTagIntArray")
public interface NBTTagIntArray extends Shadow, NBTBase {

    static NBTTagIntArray create(int[] data) {
        return ShadowFactory.global().constructShadow(NBTTagIntArray.class, (Object) data);
    }

    static NBTTagIntArray create(List<Integer> data) {
        return ShadowFactory.global().constructShadow(NBTTagIntArray.class, (Object) data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_12_R1),
            @Mapping(value = "c", version = PackageVersion.v1_8_R3)
    })
    int[] getIntArray();

}
