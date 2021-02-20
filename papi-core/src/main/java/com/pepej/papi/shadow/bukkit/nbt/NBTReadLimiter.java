package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Field;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.Static;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

@NmsClassTarget("NBTReadLimiter")
public interface NBTReadLimiter extends Shadow {

    static NBTReadLimiter create(long max) {
        return ShadowFactory.global().constructShadow(NBTReadLimiter.class, max);
    }

    @Static
    @Field
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_12_R1),
            @Mapping(value = "a", version = PackageVersion.v1_8_R3)
    })
    NBTReadLimiter infinite();

}
