package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

@NmsClassTarget("NBTTagList")
public interface NBTTagList extends Shadow, NBTBase {

    static NBTTagList create() {
        return ShadowFactory.global().constructShadow(NBTTagList.class);
    }

    @ObfuscatedTarget({
            @Mapping(value = "add", version = PackageVersion.v1_12_R1),
            @Mapping(value = "add", version = PackageVersion.v1_8_R3)
    })
    void appendTag(NBTBase nbt);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_12_R1),
            @Mapping(value = "a", version = PackageVersion.v1_8_R3)
    })
    void setTag(int index, NBTBase nbt);

    @ObfuscatedTarget({
            @Mapping(value = "remove", version = PackageVersion.v1_12_R1),
            @Mapping(value = "a", version = PackageVersion.v1_8_R3)
    })
    void removeTag(int index);

    @ObfuscatedTarget({
            @Mapping(value = "i", version = PackageVersion.v1_12_R1),
            @Mapping(value = "g", version = PackageVersion.v1_8_R3)
    })
    NBTBase getTag(int index);

    @ObfuscatedTarget({
            @Mapping(value = "get", version = PackageVersion.v1_12_R1),
            @Mapping(value = "get", version = PackageVersion.v1_8_R3)
    })
    NBTTagCompound getCompoundTag(int index);

    @ObfuscatedTarget({
            @Mapping(value = "size", version = PackageVersion.v1_12_R1),
            @Mapping(value = "size", version = PackageVersion.v1_8_R3)
    })
    int size();

}
