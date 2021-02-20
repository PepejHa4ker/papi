package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

@NmsClassTarget("NBTTagString")
public interface NBTTagString extends Shadow, NBTBase {

    static NBTTagString create(String data) {
        return ShadowFactory.global().constructShadow(NBTTagString.class, data);
    }

    @ObfuscatedTarget({
            @Mapping(value = "getString", version = PackageVersion.v1_12_R1),
            @Mapping(value = "a_", version = PackageVersion.v1_8_R3)
    })
    String getString();

}
