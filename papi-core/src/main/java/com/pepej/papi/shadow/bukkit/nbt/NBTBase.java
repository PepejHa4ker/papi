package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.nbt.NBTTagType;
import com.pepej.papi.shadow.Field;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.Static;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@NmsClassTarget("NBTBase")
public interface NBTBase extends Shadow {

    @Static
    @Field
    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_12_R1),
            @Mapping(value = "a", version = PackageVersion.v1_8_R3)
    })
    String[] getTypes();

    @ObfuscatedTarget({
            @Mapping(value = "write", version = PackageVersion.v1_12_R1),
            @Mapping(value = "write", version = PackageVersion.v1_8_R3)
    })
    void write(DataOutput dataOutput) throws IOException;

    @ObfuscatedTarget({
            @Mapping(value = "load", version = PackageVersion.v1_12_R1),
            @Mapping(value = "load", version = PackageVersion.v1_8_R3)
    })
    void load(DataInput dataInput, int depth, NBTReadLimiter readLimiter) throws IOException;

    @ObfuscatedTarget({
            @Mapping(value = "getTypeId", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getTypeId", version = PackageVersion.v1_8_R3)
    })
    byte getTypeId();

    default NBTTagType getType() {
        return NBTTagType.of(getTypeId());
    }

    @ObfuscatedTarget({
            @Mapping(value = "clone", version = PackageVersion.v1_12_R1),
            @Mapping(value = "clone", version = PackageVersion.v1_8_R3)
    })
    NBTBase copy();

    @ObfuscatedTarget({
            @Mapping(value = "isEmpty", version = PackageVersion.v1_12_R1),
            @Mapping(value = "isEmpty", version = PackageVersion.v1_8_R3)
    })
    boolean hasNoTags();

}
