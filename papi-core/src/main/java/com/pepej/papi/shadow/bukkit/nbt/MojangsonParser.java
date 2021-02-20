package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.Static;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

@NmsClassTarget("MojangsonParser")
public interface MojangsonParser extends Shadow {

    @Static
    @ObfuscatedTarget({
            @Mapping(value = "parse", version = PackageVersion.v1_12_R1),
            @Mapping(value = "parse", version = PackageVersion.v1_8_R3)
    })
    NBTTagCompound parse(String s);

}
