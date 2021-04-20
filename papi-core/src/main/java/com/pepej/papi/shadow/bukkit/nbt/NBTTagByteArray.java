package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

import java.util.List;

@NmsClassTarget("NBTTagByteArray")
public interface NBTTagByteArray extends Shadow, NBTBase {

    static NBTTagByteArray create(byte[] data) {
        return ShadowFactory.global().constructShadow(NBTTagByteArray.class, data);
    }

    static NBTTagByteArray create(List<Byte> data) {
        return ShadowFactory.global().constructShadow(NBTTagByteArray.class, data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_12_R1),
            @Mapping(value = "c", version = PackageVersion.v1_8_R3)
    })
    byte[] getByteArray();

}
