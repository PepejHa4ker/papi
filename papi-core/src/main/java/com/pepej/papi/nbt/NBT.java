package com.pepej.papi.nbt;


import com.pepej.papi.shadow.ShadowFactory;
import com.pepej.papi.shadow.bukkit.BukkitShadowFactory;
import com.pepej.papi.shadow.bukkit.nbt.MojangsonParser;
import com.pepej.papi.shadow.bukkit.nbt.NBTBase;
import com.pepej.papi.shadow.bukkit.nbt.NBTTagCompound;

/**
 * Utilities for working with NBT shadows.
 */
public final class NBT {

    private static MojangsonParser parser = null;

    private static MojangsonParser parser() {
        // harmless race
        if (parser == null) {
            return parser = BukkitShadowFactory.global().staticShadow(MojangsonParser.class);
        }
        return parser;
    }

    public static NBTBase shadow(Object nbtObject) {
        // first, shadow as a NBTBase
        NBTBase shadow = BukkitShadowFactory.global().constructShadow(NBTBase.class, nbtObject);

        // extract the tag's type
        NBTTagType type = shadow.getType();
        Class<? extends NBTBase> realClass = type.shadowClass();

        // return a shadow instance for the actual type
        return ShadowFactory.global().shadow(realClass, nbtObject);
    }

    public static NBTTagCompound parse(String s) {
        return parser().parse(s);
    }

    private NBT() {}

}