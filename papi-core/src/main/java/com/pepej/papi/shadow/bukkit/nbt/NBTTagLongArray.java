package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Field;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

import java.util.List;

@NmsClassTarget("NBTTagLongArray")
public interface NBTTagLongArray extends Shadow, NBTBase {

    static NBTTagLongArray create(long[] data) {
        return ShadowFactory.global().constructShadow(NBTTagLongArray.class, data);
    }

    static NBTTagLongArray create(List<Long> data) {
        return ShadowFactory.global().constructShadow(NBTTagLongArray.class, data);
    }

    @Field
    @ObfuscatedTarget({
            @Mapping(value = "b", version = PackageVersion.v1_12_R1)
    })
    long[] getLongArray();

}