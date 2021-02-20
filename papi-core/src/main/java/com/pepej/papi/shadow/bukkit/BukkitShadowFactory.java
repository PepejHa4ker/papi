package com.pepej.papi.shadow.bukkit;

import com.pepej.papi.shadow.ShadowFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An extension of {@link ShadowFactory} with pre-registered target resolvers for
 * {@link NmsClassTarget}, {@link ObcClassTarget} and {@link ObfuscatedTarget} annotations.
 */
public class BukkitShadowFactory extends ShadowFactory {

    private static final BukkitShadowFactory INSTANCE = new BukkitShadowFactory();

    public static @NonNull BukkitShadowFactory global() {
        return INSTANCE;
    }

    public BukkitShadowFactory() {
        registerTargetResolver(NmsClassTarget.RESOLVER);
        registerTargetResolver(ObcClassTarget.RESOLVER);
        registerTargetResolver(ObfuscatedTarget.RESOLVER);
    }

}
