package com.pepej.papi.shadow.bukkit.nbt;

import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.Mapping;
import com.pepej.papi.shadow.bukkit.NmsClassTarget;
import com.pepej.papi.shadow.bukkit.ObfuscatedTarget;
import com.pepej.papi.shadow.bukkit.PackageVersion;

import java.util.Set;

@NmsClassTarget("NBTTagCompound")
public interface NBTTagCompound extends Shadow, NBTBase {

    static NBTTagCompound create() {
        return ShadowFactory.global().constructShadow(NBTTagCompound.class);
    }

    @ObfuscatedTarget({
            @Mapping(value = "c", version = PackageVersion.v1_12_R1),
            @Mapping(value = "c", version = PackageVersion.v1_8_R3)
    })
    Set<String> keySet();

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_12_R1),
            // Not present on 1.8.8
    })
    int size();

    @ObfuscatedTarget({
            @Mapping(value = "set", version = PackageVersion.v1_12_R1),
            @Mapping(value = "set", version = PackageVersion.v1_8_R3)
    })
    void setTag(String key, NBTBase value);

    @ObfuscatedTarget({
            @Mapping(value = "remove", version = PackageVersion.v1_12_R1),
            @Mapping(value = "remove", version = PackageVersion.v1_8_R3)
    })
    void removeTag(String key);

    @ObfuscatedTarget({
            @Mapping(value = "setByte", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setByte", version = PackageVersion.v1_8_R3)
    })
    void setByte(String key, byte value);

    @ObfuscatedTarget({
            @Mapping(value = "setShort", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setShort", version = PackageVersion.v1_8_R3)
    })
    void setShort(String key, short value);

    @ObfuscatedTarget({
            @Mapping(value = "setInt", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setInt", version = PackageVersion.v1_8_R3)
    })
    void setInteger(String key, int value);

    @ObfuscatedTarget({
            @Mapping(value = "setLong", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setLong", version = PackageVersion.v1_8_R3)
    })
    void setLong(String key, long value);

    @ObfuscatedTarget({
            @Mapping(value = "setFloat", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setFloat", version = PackageVersion.v1_8_R3)
    })
    void setFloat(String key, float value);

    @ObfuscatedTarget({
            @Mapping(value = "setDouble", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setDouble", version = PackageVersion.v1_8_R3)
    })
    void setDouble(String key, double value);

    @ObfuscatedTarget({
            @Mapping(value = "setString", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setString", version = PackageVersion.v1_8_R3)
    })
    void setString(String key, String value);

    @ObfuscatedTarget({
            @Mapping(value = "setByteArray", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setByteArray", version = PackageVersion.v1_8_R3)
    })
    void setByteArray(String key, byte[] value);

    @ObfuscatedTarget({
            @Mapping(value = "setIntArray", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setIntArray", version = PackageVersion.v1_8_R3)
    })
    void setIntArray(String key, int[] value);

    @ObfuscatedTarget({
            @Mapping(value = "setBoolean", version = PackageVersion.v1_12_R1),
            @Mapping(value = "setBoolean", version = PackageVersion.v1_8_R3)
    })
    void setBoolean(String key, boolean value);

    @ObfuscatedTarget({
            @Mapping(value = "get", version = PackageVersion.v1_12_R1),
            @Mapping(value = "get", version = PackageVersion.v1_8_R3)
    })
    NBTBase getTag(String key);

    @ObfuscatedTarget({
            @Mapping(value = "d", version = PackageVersion.v1_12_R1),
            @Mapping(value = "b", version = PackageVersion.v1_8_R3)
    })
    byte getTagId(String key);

    @ObfuscatedTarget({
            @Mapping(value = "hasKey", version = PackageVersion.v1_12_R1),
            @Mapping(value = "hasKey", version = PackageVersion.v1_8_R3)
    })
    boolean hasKey(String key);

    @ObfuscatedTarget({
            @Mapping(value = "hasKeyOfType", version = PackageVersion.v1_12_R1),
            @Mapping(value = "hasKeyOfType", version = PackageVersion.v1_8_R3)
    })
    boolean hasKey(String key, int type);

    @ObfuscatedTarget({
            @Mapping(value = "getByte", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getByte", version = PackageVersion.v1_8_R3)
    })
    byte getByte(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getShort", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getShort", version = PackageVersion.v1_8_R3)
    })
    short getShort(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getInt", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getInt", version = PackageVersion.v1_8_R3)
    })
    int getInteger(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getLong", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getLong", version = PackageVersion.v1_8_R3)
    })
    long getLong(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getFloat", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getFloat", version = PackageVersion.v1_8_R3)
    })
    float getFloat(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getDouble", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getDouble", version = PackageVersion.v1_8_R3)
    })
    double getDouble(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getString", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getString", version = PackageVersion.v1_8_R3)
    })
    String getString(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getByteArray", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getByteArray", version = PackageVersion.v1_8_R3)
    })
    byte[] getByteArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getIntArray", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getIntArray", version = PackageVersion.v1_8_R3)
    })
    int[] getIntArray(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getCompound", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getCompound", version = PackageVersion.v1_8_R3)
    })
    NBTTagCompound getCompoundTag(String key);

    @ObfuscatedTarget({
            @Mapping(value = "getList", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getList", version = PackageVersion.v1_8_R3)
    })
    NBTTagList getTagList(String key, int type);

    @ObfuscatedTarget({
            @Mapping(value = "getBoolean", version = PackageVersion.v1_12_R1),
            @Mapping(value = "getBoolean", version = PackageVersion.v1_8_R3)
    })
    boolean getBoolean(String key);

    @ObfuscatedTarget({
            @Mapping(value = "a", version = PackageVersion.v1_12_R1),
            @Mapping(value = "a", version = PackageVersion.v1_8_R3)
    })
    void merge(NBTTagCompound other);

}
